package com.c1ok.bedwars.internal

import com.c1ok.bedwars.api.game.MiniGame
import com.c1ok.bedwars.api.game.IMiniPlayer
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import java.util.*

class MiniPlayer(override val uuid: UUID): IMiniPlayer {

    override val player: Player?
        get() = MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(uuid)

    override var game: MiniGame? = null

    override fun onPlayerExitGame() {
        game = null
    }

}