package com.c1ok.bedwars.internal.feature.inventory.trade

import com.c1ok.bedwars.internal.core.bedwars.BedWarsPlayer

interface Commodity {

    // 商品ID
    val id: String

    //  需要满足什么才可以购买
    fun condition(player: BedWarsPlayer): Boolean

    fun success(player: BedWarsPlayer)

}