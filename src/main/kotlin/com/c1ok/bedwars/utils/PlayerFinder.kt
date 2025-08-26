package com.c1ok.bedwars.utils

import com.c1ok.bedwars.api.game.IMiniPlayer
import com.c1ok.bedwars.api.game.bedwars.BedWarsGame
import com.c1ok.bedwars.api.game.bedwars.IBedWarsPlayer
import com.c1ok.bedwars.internal.manager.PlayerManagerImpl
import net.minestom.server.entity.Player

fun Player.getMiniPlayer(): IMiniPlayer? {
    return PlayerManagerImpl.getPlayer(uuid)
}

fun Player.getBedwarsPlayer(): IBedWarsPlayer? {
    val mp = getMiniPlayer() ?: return null
    val game = mp.game as? BedWarsGame ?: return null
    return game.getBedwarsPlayer(this)
}
