package com.c1ok.bedwars.api.game.bedwars.items.meta

import com.c1ok.bedwars.api.game.bedwars.items.SpecialItem
import net.minestom.server.entity.Player

/**
 * 攻击时触发
 */
interface Attackable: SpecialItem {
    fun onAttack(player: Player)
}