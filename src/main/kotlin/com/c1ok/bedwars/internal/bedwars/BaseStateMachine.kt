package com.c1ok.bedwars.internal.bedwars

import com.c1ok.bedwars.api.game.MiniGame
import com.c1ok.bedwars.api.game.bedwars.GameStateMachine
import com.c1ok.bedwars.utils.SchedulerBuilder
import net.minestom.server.utils.validate.Check
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

open class BaseStateMachine(override val game: BaseBedWarsGame): GameStateMachine {

    companion object {
        // 等待时间是120秒
        const val DEFAULT_TIME = 120
        //
        const val GAME_TIME = 3600
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    var waiting_timer = AtomicInteger(DEFAULT_TIME)

    var game_timer = AtomicInteger(GAME_TIME)

    var forceStarted = false
        set(value) {
            if (value) {
                field = true
                startGame()
            }
        }

    private val state: AtomicReference<MiniGame.GameState> = AtomicReference(MiniGame.GameState.CLOSED)
    var isWatingClock = false

    override fun currentState(): MiniGame.GameState {
        return state.get()
    }

    override fun init() {
        if (state.get() != MiniGame.GameState.CLOSED && state.get() != MiniGame.GameState.RESTARTING) {
            logger.error("初始化失败，状态不对")
        }
        state.set(MiniGame.GameState.INITIALIZING)
        startLobby()
    }

    override fun startLobby() {
        if(state.get() == MiniGame.GameState.STARTING) {
            logger.error("游戏被启动了就无法进入等待大厅了")
            return
        }

        if(state.get() == MiniGame.GameState.STARTING || state.get() == MiniGame.GameState.LOBBY) {
            game.close()
            return
        }
        state.set(MiniGame.GameState.LOBBY)
        game.onGameInCountdown()
        SchedulerBuilder(game.gameInstance.scheduler(),
            Runnable {
                if (game.gameInstance.players.size >= game.minPlayers) {
                    isWatingClock = true
                    waiting_timer.addAndGet(-1)
                    game.onGameWatingSecond(waiting_timer.get())
                }
                if (game.gameInstance.players.size < game.minPlayers) {
                    isWatingClock = false
                    waiting_timer.set(DEFAULT_TIME)
                }
            })
            .condition { waiting_timer.get() >= 0 && currentState() == MiniGame.GameState.LOBBY }
            .conditionFalseTask {
                isWatingClock = false
                waiting_timer.set(DEFAULT_TIME)
                if (state.get() == MiniGame.GameState.STARTING) return@conditionFalseTask
                startGame()
            }.repeat(Duration.ofSeconds(1)).schedule()
    }

    override fun startGame() {
        if(state.get() != MiniGame.GameState.LOBBY) {
            logger.error("游戏已经被启动了，无法再启动")
            return
        }
        state.set(MiniGame.GameState.STARTING)
        game.onGameStart()
        SchedulerBuilder(game.gameInstance.scheduler(), Runnable {
            game_timer.addAndGet(-1)
            game.onGameSecondCount(game_timer.get())
        }).delay(Duration.ofSeconds(1)).condition {
            gameStartingCondition(game_timer.get())
        }.conditionFalseTask {
            game_timer.set(GAME_TIME)
            endGame()
        }.repeat(Duration.ofSeconds(1)).schedule()
    }

    private fun gameStartingCondition(time: Int): Boolean {
//        println("Test 01::${currentState() == MiniGame.GameState.STARTING}")
//        println("Test 02::${game.gamePlayers.isNotEmpty()}")
//        println("Test 03::${game.teams.filter { !it.isWipedOut }.size > 1}")
//        println("Test 04::${time}")
        return  time > 0 &&
                currentState() == MiniGame.GameState.STARTING &&
                game.teams.filter { !it.isWipedOut }.size > 1 &&
                game.gamePlayers.isNotEmpty()
    }

    override fun endGame() {
        Check.stateCondition(state.get() != MiniGame.GameState.STARTING, "游戏都没进行，无法直接结算")
        state.set(MiniGame.GameState.ENDED)
        game.onEnd().thenAccept {
            restartGame()
        }
    }

    override fun restartGame() {
        Check.stateCondition(state.get() == MiniGame.GameState.RESTARTING, "游戏已经进入重启状态了，无法再次进入")
        state.set(MiniGame.GameState.RESTARTING)
        game.rebuild().thenAccept {
            game.init()
        }
    }

    override fun shutdown() {
        Check.stateCondition(state.get() == MiniGame.GameState.ENDED, "游戏已经被关闭了，无法再次关闭了")
        state.set(MiniGame.GameState.CLOSED)
        game.close()
    }

}