package com.c1ok.bedwars.api.game

import net.minestom.server.entity.Player
import java.util.*

interface IMiniPlayer {

    // 玩家的UUID
    val uuid: UUID

    val player: Player?

    // 得到玩家在玩的游戏
    var game: MiniGame?

    // 当玩家离开游戏的时候，它将会触发一些清除任务
    fun onPlayerExitGame()

}