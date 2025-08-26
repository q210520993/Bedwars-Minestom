package com.c1ok.bedwars.internal.core.bedwars.bounds

import com.c1ok.bedwars.api.game.bedwars.BedWarsGame
import com.c1ok.bedwars.api.game.bedwars.BedwarsBound
import com.c1ok.bedwars.internal.core.bedwars.trader.Trader
import net.minestom.server.coordinate.Pos

class GameTrader(val game: BedWarsGame): BedwarsBound {
    override val priority: Int = 1
    override val id: String = "GameTrader"

    override fun onInit() {
        val entity = Trader()
        entity.setInstance(game.gameInstance, Pos(-194.0, 45.0, 539.0))
    }

}