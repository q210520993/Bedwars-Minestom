package com.c1ok.bedwars.api.game.bedwars.items.meta

import com.c1ok.bedwars.api.game.bedwars.items.SpecialItem
import com.c1ok.bedwars.internal.bedwars.BedWarsPlayer

/**
 *
 * 受到攻击时触发
 *
 */
interface Damageable: SpecialItem {
    fun onDamage(player: BedWarsPlayer)
}