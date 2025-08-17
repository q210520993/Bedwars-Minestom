package com.c1ok.bedwars.api.game.bedwars.items.meta

import com.c1ok.bedwars.api.game.bedwars.IBedWarsPlayer
import com.c1ok.bedwars.api.game.bedwars.items.SpecialItem

/**
 *
 * 右键时触发
 *
 */
interface Clickable: SpecialItem {
    fun onClick(player: IBedWarsPlayer)
}