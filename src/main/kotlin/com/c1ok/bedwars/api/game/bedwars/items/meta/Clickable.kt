package com.c1ok.bedwars.api.game.bedwars.items.meta

import com.c1ok.bedwars.api.game.bedwars.items.SpecialItem
import net.minestom.server.entity.Player

/**
 *
 * 右键时触发
 *
 */
interface Clickable: SpecialItem {
    fun onClick(player: Player)
}