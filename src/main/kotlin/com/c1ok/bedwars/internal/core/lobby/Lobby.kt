package com.c1ok.bedwars.internal.core.lobby

import com.c1ok.bedwars.internal.core.bedwars.item.SpecialItemManagerImpl

var SpecialManager = SpecialItemManagerImpl()

fun registerSpecial() {
    SpecialManager.addSpecial(SelectGameItem)
}