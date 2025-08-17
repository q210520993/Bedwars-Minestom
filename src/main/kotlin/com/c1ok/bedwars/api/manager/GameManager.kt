package com.c1ok.bedwars.api.manager

import com.c1ok.bedwars.api.game.MiniGame
import com.c1ok.bedwars.api.game.IMiniPlayer
import net.minestom.server.entity.Player
import net.minestom.server.instance.Instance
import java.util.*

interface GameManager<T: MiniGame> {

    fun getGame(uuid: UUID): T?

    fun playerIsInGame(player: IMiniPlayer): Boolean

    fun isGameInstance(instance: Instance): Boolean

    fun getInstanceGame(instance: Instance): T?

    fun getGameInstance(game: T): Instance

    fun getGames(): List<T>

}