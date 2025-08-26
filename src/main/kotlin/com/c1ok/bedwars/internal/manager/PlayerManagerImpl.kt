package com.c1ok.bedwars.internal.manager

import com.c1ok.bedwars.api.game.IMiniPlayer
import com.c1ok.bedwars.api.manager.PlayerManager
import com.c1ok.bedwars.internal.MiniPlayer
import net.minestom.server.MinecraftServer
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object PlayerManagerImpl: PlayerManager {

    private val players = ConcurrentHashMap<UUID, IMiniPlayer>()

    override fun getPlayer(uuid: UUID): IMiniPlayer? {
        if (players.containsKey(uuid)) return players[uuid]
        if (MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(uuid) == null)
            return null
        if (players[uuid] == null) {
            val player = MiniPlayer(uuid)
            this.players[uuid] = player
            return player
        }
        return null
    }

    override fun isPlayerInGame(uuid: UUID): Boolean {
        val player = players[uuid] ?: return false
        return isPlayerInGame(player)
    }

    override fun isPlayerInGame(player: IMiniPlayer): Boolean {
        return player.game != null
    }

}