package com.c1ok.bedwars.internal.manager

import com.c1ok.bedwars.api.game.IMiniPlayer
import com.c1ok.bedwars.api.game.bedwars.BedWarsGame
import com.c1ok.bedwars.api.manager.GameManager
import net.minestom.server.instance.Instance
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

object BedWarsManager: GameManager<BedWarsGame> {
    private val games = ConcurrentHashMap<UUID, BedWarsGame>()

    override fun getGames(): List<BedWarsGame> {
        return games.values.toList()
    }

    fun addGameToManager(game: BedWarsGame): Boolean {
        if (games.values.contains(game)) {
            return false
        }
        games[game.uuid] = game
        return true
    }

    fun removeGame(game: BedWarsGame) {
        games.remove(game.uuid)
    }

    override fun getInstanceGame(instance: Instance): BedWarsGame? {
        val instances = games.values.map { it.gameInstance }
        if (!instances.contains(instance)) { return null }
        return games.values.first { it.gameInstance == instance }
    }

    override fun getGame(uuid: UUID): BedWarsGame? {
        return games[uuid]
    }

    override fun getGameInstance(game: BedWarsGame): Instance {
        return game.gameInstance
    }

    override fun isGameInstance(instance: Instance): Boolean {
        return games.values.find { it.gameInstance == instance }!!.gameInstance == instance
    }

    override fun playerIsInGame(player: IMiniPlayer): Boolean {
        return player.game is BedWarsGame
    }

}