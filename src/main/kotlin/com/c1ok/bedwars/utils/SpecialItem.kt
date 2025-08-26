package com.c1ok.bedwars.utils

import com.c1ok.bedwars.internal.core.bedwars.item.SpecialItemManagerImpl
import net.minestom.server.item.ItemStack

fun ItemStack.Builder.setSpecial(id: String): ItemStack.Builder {
    return this.set(SpecialItemManagerImpl.tag, id)
}