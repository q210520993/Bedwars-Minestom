package com.c1ok.bedwars.api.game.bedwars

import com.c1ok.bedwars.api.game.IMiniPlayer

fun interface BedPlayerCreator {
    fun create(bedWarsGame: BedWarsGame, miniPlayer: IMiniPlayer): IBedWarsPlayer
}