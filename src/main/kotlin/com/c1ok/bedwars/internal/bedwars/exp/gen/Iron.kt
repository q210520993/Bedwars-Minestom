package com.c1ok.bedwars.internal.bedwars.exp.gen

import com.c1ok.bedwars.api.game.bedwars.IBedWarsPlayer
import com.c1ok.bedwars.api.game.bedwars.genertors.SpawnResourceType
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.ItemEntity
import net.minestom.server.item.Material

class Iron(
    id: String,
    point: Pos
): SpawnResourceType(id, "level", "铁", Material.IRON_INGOT, 1, 1, point) {

    override fun onCollect(player: IBedWarsPlayer, itemEntity: ItemEntity): Boolean {
        player.game.addPlayerResource(player, "level", itemEntity.itemStack.amount())
        return true
    }

}