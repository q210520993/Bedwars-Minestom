package com.c1ok.bedwars.api.game.bedwars.items.meta

import com.c1ok.bedwars.api.game.bedwars.items.SpecialItem
import com.c1ok.bedwars.internal.core.bedwars.BedWarsPlayer
import net.minestom.server.entity.Player

/**
 *
 * 受到攻击时触发
 *
 */
interface Damageable: SpecialItem {
    fun onDamage(player: Player)
}