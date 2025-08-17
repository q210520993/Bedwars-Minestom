package com.c1ok.bedwars.internal.bedwars.bounds

import com.c1ok.bedwars.api.game.bedwars.BedwarsBound
import com.c1ok.bedwars.internal.bedwars.BaseBedWarsGame
import com.c1ok.bedwars.internal.inventory.TeamInventory

class TeamInventoryTick(private val game: BaseBedWarsGame): BedwarsBound {
    override val priority: Int = 5
    override val id: String = "TeamInventoryTick"

    override fun onTick() {
        game.gamePlayers.values.forEach {
            val player = it.miniPlayer.player ?: return@forEach
            val inventory = player.openInventory ?: return@forEach
            if (inventory is TeamInventory) {
                inventory.onGameUpdate()
            }
        }
    }

}