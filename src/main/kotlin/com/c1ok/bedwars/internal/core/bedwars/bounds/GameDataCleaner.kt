package com.c1ok.bedwars.internal.core.bedwars.bounds

import com.c1ok.bedwars.api.game.bedwars.BedwarsBound
import com.c1ok.bedwars.internal.core.bedwars.BaseBedWarsGame
import net.minestom.server.entity.Player
import net.minestom.server.instance.block.Block

open class GameDataCleaner(private val game: BaseBedWarsGame): BedwarsBound {
    override fun onRebuild() {
        clearData()
    }

    override val id: String = "cleaner"

    override val priority: Int = 0

    override fun onClose() {
        clearData()
    }

    protected fun clearData() {
        game.gamePlayers.apply {
            values.forEach {
                game.leaveGame(it.miniPlayer)
            }
        }
        game.gameInstance.entities.filter {
            it !is Player
        }.forEach {
            it.remove()
        }
        game.teams.apply {
            forEach{
                it.onGameStop()
            }
            clear()
        }

        game.beds.clear()
        game.blocks.apply {
            game.blocks.forEach { (k,v) ->
                game.gameInstance.setBlock(k, Block.AIR)
            }
            clear()
        }
    }

}