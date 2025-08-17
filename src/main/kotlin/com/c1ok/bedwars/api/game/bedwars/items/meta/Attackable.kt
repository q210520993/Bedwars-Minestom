package com.c1ok.bedwars.api.game.bedwars.items.meta

import com.c1ok.bedwars.api.game.bedwars.items.SpecialItem
import com.c1ok.bedwars.internal.bedwars.BedWarsPlayer

/**
 * 攻击时触发
 */
interface Attackable: SpecialItem {
    fun onAttack(player: BedWarsPlayer)
}