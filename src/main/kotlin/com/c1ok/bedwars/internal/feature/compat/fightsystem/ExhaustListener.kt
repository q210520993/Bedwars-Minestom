package com.c1ok.bedwars.internal.feature.compat.fightsystem

import com.c1ok.bedwars.Bedwars
import com.c1ok.bedwars.api.game.MiniGame
import com.c1ok.bedwars.api.game.bedwars.BedWarsGame
import com.c1ok.bedwars.internal.manager.PlayerManagerImpl
import io.github.togar2.pvp.events.PlayerExhaustEvent
import net.minestom.server.MinecraftServer

object ExhaustListener {
    fun register() {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerExhaustEvent::class.java) {
            val msPlayer = it.player
            if (msPlayer.instance == Bedwars.lobby) {
                it.isCancelled = true
            }
            val player = PlayerManagerImpl.getPlayer(msPlayer.uuid) ?: return@addListener
            val game = player.game ?: return@addListener
            if (game is BedWarsGame && game.currentState() == MiniGame.GameState.LOBBY) {
                it.isCancelled = true
            }
        }
    }
}