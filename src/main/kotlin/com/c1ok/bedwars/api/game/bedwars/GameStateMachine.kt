package com.c1ok.bedwars.api.game.bedwars

import com.c1ok.bedwars.api.game.MiniGame

// 游戏状态管理
interface GameStateMachine {
    val game: BedWarsGame
    fun init()
    fun startLobby()
    fun startGame()
    fun endGame()
    fun restartGame()
    fun currentState(): MiniGame.GameState
    fun shutdown()
}