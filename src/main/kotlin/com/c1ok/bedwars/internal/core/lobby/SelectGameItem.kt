package com.c1ok.bedwars.internal.core.lobby

import com.c1ok.bedwars.api.game.bedwars.items.SpecialItem
import com.c1ok.bedwars.api.game.bedwars.items.meta.Clickable
import com.c1ok.bedwars.internal.feature.inventory.GameInventory
import net.minestom.server.entity.Player

object SelectGameItem: SpecialItem, Clickable {
    override val tagValue: String = "selectGame"

    override fun onClick(player: Player) {
        player.openInventory(GameInventory.inventory)
    }
}