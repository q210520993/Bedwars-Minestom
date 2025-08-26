package com.c1ok.bedwars.internal.core.bedwars.bounds

import com.c1ok.bedwars.api.game.bedwars.BedwarsBound
import com.c1ok.bedwars.internal.core.bedwars.BaseBedWarsGame

class GeneratorBound(private val game: BaseBedWarsGame): BedwarsBound {
    override val id: String = "generator"
    override val priority: Int = 1

    override fun onGameStart() {
        game.generator.start()
    }

    override fun onClose() {
        game.generator.close()
    }

    override fun onEnd() {
        game.generator.close()
    }

}