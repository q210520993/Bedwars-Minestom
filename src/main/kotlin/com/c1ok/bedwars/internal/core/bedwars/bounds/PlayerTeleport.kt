package com.c1ok.bedwars.internal.core.bedwars.bounds

import com.c1ok.bedwars.api.game.bedwars.BedwarsBound
import com.c1ok.bedwars.internal.core.bedwars.BaseBedWarsGame

class PlayerTeleport(private val game: BaseBedWarsGame): BedwarsBound {
    override val id: String = "playerTeleport"
    override val priority: Int = 1

    override fun onGameStart() {
        game.teams.forEach {
            it.players.forEach { player ->
                player.miniPlayer.player?.teleport(it.respawnPoint)
            }
        }
    }

}