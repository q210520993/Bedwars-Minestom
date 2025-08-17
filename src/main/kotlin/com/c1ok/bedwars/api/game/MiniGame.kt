package com.c1ok.bedwars.api.game

import java.util.UUID

// 代表着一个小游戏
interface MiniGame {

    val uuid: UUID

    val maxPlayers: Int

    fun currentState(): GameState

    val minPlayers: Int

    fun joinGame(player: IMiniPlayer): Boolean

    fun leaveGame(player: IMiniPlayer)

    enum class GameState {
        INITIALIZING,
        LOBBY,
        STARTING,
        ENDED,
        RESTARTING,
        CLOSED
    }

}