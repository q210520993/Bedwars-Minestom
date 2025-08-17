package com.c1ok.bedwars.api.manager

import com.c1ok.bedwars.api.game.IMiniPlayer
import java.util.*

interface PlayerManager {

    fun getPlayer(uuid: UUID): IMiniPlayer?

    fun isPlayerInGame(uuid: UUID): Boolean

    fun isPlayerInGame(player: IMiniPlayer): Boolean

}