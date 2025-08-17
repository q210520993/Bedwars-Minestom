package com.c1ok.bedwars.internal.bedwars

import com.c1ok.bedwars.Bedwars
import com.c1ok.bedwars.api.game.IMiniPlayer
import com.c1ok.bedwars.api.game.MiniGame
import com.c1ok.bedwars.api.game.bedwars.*
import com.c1ok.bedwars.api.game.bedwars.genertors.Generator
import com.c1ok.bedwars.internal.bedwars.bounds.GameDataCleaner
import com.c1ok.bedwars.internal.manager.BedWarsManager
import com.c1ok.bedwars.utils.SchedulerBuilder
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.event.entity.EntityDamageEvent
import net.minestom.server.event.player.PlayerBlockBreakEvent
import net.minestom.server.event.player.PlayerBlockPlaceEvent
import net.minestom.server.event.player.PlayerDeathEvent
import net.minestom.server.event.player.PlayerRespawnEvent
import net.minestom.server.instance.Instance
import net.minestom.server.instance.block.Block
import net.minestom.server.inventory.Inventory
import net.minestom.server.timer.Task
import net.minestom.server.timer.TaskSchedule
import net.minestom.server.utils.validate.Check
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CopyOnWriteArraySet

abstract class BaseBedWarsGame(
    override val displayName: String,
    override val maxPlayers: Int,
    override val minPlayers: Int,
    override val waitingPos: Pos
): BedWarsGame {

    abstract fun createInstance(): Instance
    abstract fun createUUID(): UUID
    abstract fun createPlayer(): BedPlayerCreator
    abstract fun initTeams()
    abstract fun assignTeam()
    abstract fun assignItems()
    abstract val generator: Generator
    abstract fun createInventory(gamePlayer: IBedWarsPlayer): Inventory
    abstract fun onTick()

    val bounds: MutableList<BedwarsBound> = CopyOnWriteArrayList()
    override val teams: MutableList<Team> = CopyOnWriteArrayList()
    override val gameInstance: Instance by lazy { createInstance() }
    override val bedPlayerCreator: BedPlayerCreator by lazy { createPlayer() }
    val beds: MutableMap<Point, Team> = ConcurrentHashMap()
    val blocks: MutableMap<Point, Block> = ConcurrentHashMap()
    var isFirstInit: Boolean = true
        internal set

    override val lobby: Instance = Bedwars.lobby

    lateinit var task: Task

    abstract override val gameStateMachine: BaseStateMachine

    override val gamePlayers = ConcurrentHashMap<UUID, IBedWarsPlayer>()
    override val uuid: UUID by lazy { createUUID() }

    override fun currentState(): MiniGame.GameState {
        return gameStateMachine.currentState()
    }

    // 注册一堆用的，在init前bounds
    abstract fun registerBounds()

    // gameStateMachine的入口
    override fun init() {
        initTeams()
        gameStateMachine.init()
        var maxPlayer = 0
        teams.forEach {
            maxPlayer += it.maxPlayers
        }
        if (maxPlayer != maxPlayers) {
            gameStateMachine.shutdown()
        }
        registerBounds()
        bounds.forEach {
            it.onInit()
        }
        task = SchedulerBuilder(gameInstance.scheduler(), Runnable {
            onTick()
            bounds.forEach {
                it.onTick()
            }
        }).condition {
            return@condition currentState() != MiniGame.GameState.CLOSED &&
                    currentState() != MiniGame.GameState.RESTARTING &&
                    currentState() != MiniGame.GameState.ENDED
        }.repeat(TaskSchedule.nextTick()).schedule()

        if (isFirstInit) {
            isFirstInit = false
        }
    }

    fun addBound(bedwarsBound: BedwarsBound) {
        bounds.add(bedwarsBound)
        bounds.sortBy {
            it.priority
        }
    }

    fun replaceBound(bedwarsBound: BedwarsBound) {
        val old = bounds.first { it.id == bedwarsBound.id }
        bounds.remove(old)
        bounds.add(bedwarsBound)
    }

    // 关闭房间
    override fun close() {
        bounds.forEach {
            it.onClose()
        }
        BedWarsManager.removeGame(this)
        MinecraftServer.getInstanceManager().unregisterInstance(gameInstance)
    }

    // 做一些游戏启动后的处理
    override fun onGameStart() {
        teams.filter { it.players.isEmpty() }.forEach {
            it.isWipedOut = true
        }
        assignTeam()
        assignItems()
        teams.forEach {
            if (it.isWipedOut) return@forEach
            addBedToTeam(it)
        }
        bounds.forEach {
            it.onGameStart()
        }
    }

    override fun onBlockPlace(event: PlayerBlockPlaceEvent) {
        blocks[event.blockPosition] = event.block
    }

    override fun onBlockBreak(event: PlayerBlockBreakEvent) {
        val gamePlayer = gamePlayers[event.player.uuid] ?: run {
            event.isCancelled = true
            return
        }
        val handler = event.block.handler() ?: run {
            event.isCancelled = true
            return
        }
        if (handler is BedHandler) {
            if (!handler.canDestory(gamePlayer)) {
                event.isCancelled = true
            }
            return
        }
        if (!blocks.containsValue(event.block)) {
            event.isCancelled = true
        }
    }


    override fun rebuild(): CompletableFuture<Any?> {
        bounds.forEach {
            it.onRebuild()
        }
        return CompletableFuture.completedFuture(null)
    }

    override fun joinGame(player: IMiniPlayer): Boolean {
        val minestomPlayer = player.player ?: return false
        if (currentState() == MiniGame.GameState.INITIALIZING) {
            minestomPlayer.sendMessage("房间初始化中，无法进入")
            return false
        }
        if (currentState() == MiniGame.GameState.RESTARTING) {
            minestomPlayer.sendMessage("房间重启中，无法进入")
            return false
        }
        if (currentState() == MiniGame.GameState.ENDED || currentState() == MiniGame.GameState.CLOSED) {
            minestomPlayer.sendMessage("房间已经被关闭了，无法进入")
            return false
        }
        if (gamePlayers.size > maxPlayers) {
            minestomPlayer.sendMessage("房间满人喽")
            return false
        }
        if (currentState() == MiniGame.GameState.LOBBY) {
            val playerB = bedPlayerCreator.create(this, player)
            gamePlayers[player.uuid] = playerB
            minestomPlayer.setInstance(this.gameInstance, waitingPos)
            minestomPlayer.teleport(waitingPos)
            playerB.refreshPlayer()
            return true
        }
        return false
    }

    override fun leaveGame(player: IMiniPlayer) {
        player.player?.let { gamePlayers[player.uuid]?.info?.removeViewer(it) }
        gamePlayers[player.uuid]?.restoreInv()
        gamePlayers.remove(player.uuid)
        player.player?.setInstance(lobby)
        player.onPlayerExitGame()
    }

    override fun onPlayerDamaged(event: EntityDamageEvent) {
        if (currentState() != MiniGame.GameState.STARTING) {
            event.isCancelled = true
        }
        val player = event.entity as? Player ?: return
        val attacker = event.damage.attacker as? Player ?: return
        val gameplayer = gamePlayers[player.uuid] ?: return
        val attackerGamePlayer = gamePlayers[attacker.uuid] ?: return
        if (gameplayer.team == attackerGamePlayer.team) event.isCancelled = true
    }

    override fun onEnd(): CompletableFuture<Void> {
        gamePlayers.forEach { gamePlayer ->
            val minestomPlayer = gamePlayer.value.miniPlayer.player ?: return@forEach
            minestomPlayer.sendMessage("游戏结束！")
            if (teams.filter { !it.isWipedOut }.size != 1) return@forEach
            minestomPlayer.sendMessage("本场游戏的获胜队伍为${teams.first { !it.isWipedOut }.name}")
            leaveGame(player = gamePlayer.value.miniPlayer)
        }
        if (::task.isInitialized) {
            task.cancel()
        }
        return CompletableFuture.completedFuture(null)
    }

    fun addBedToTeam(team: Team) {
        team.createBed()
        beds[team.bedPoint] = team
    }

    override fun onPlayerDeath(event: PlayerDeathEvent) {
        if (gamePlayers[event.player.uuid] == null) {
            return
        }
        if (currentState() == MiniGame.GameState.LOBBY) {
            event.player.respawn()
        }
        if (currentState() != MiniGame.GameState.STARTING) {
            return
        }
        val gamePlayer = gamePlayers[event.player.uuid] ?: return
        val team = gamePlayer.team ?: return
        if(team.isBedDestroyed) {
            gamePlayer.spectator = true
            if (team.players.all { it.spectator }) {
                team.isWipedOut = true
            }
        }
        gamePlayer.deaths.addAndGet(1)
    }

    override fun onPlayerRespawn(event: PlayerRespawnEvent) {
        Check.stateCondition(gamePlayers[event.player.uuid] == null, "玩家没有参与该游戏")
        val gamePlayer = gamePlayers[event.player.uuid]!!
        if (gamePlayer.spectator) {
            event.player.gameMode = GameMode.SPECTATOR
        }
        event.respawnPosition = gamePlayer.team?.respawnPoint ?: return
    }

    override fun killPlayer(entity: IBedWarsPlayer): Boolean {
        entity.miniPlayer.player?.kill()
        return true
    }

}