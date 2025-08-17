package com.c1ok.bedwars.api.game.bedwars.items

import net.minestom.server.item.ItemStack

interface SpecialManager {

    fun getSpecialHandler(itemStack: ItemStack): SpecialItem?
    fun getSpecialHandler(id: String): SpecialItem?

    fun isSpecial(itemStack: ItemStack): Boolean

    fun addSpecial(specialItem: SpecialItem): SpecialItem?

    fun setItemToSpecial(itemStack: ItemStack, specialItem: SpecialItem): Boolean
    fun unsafeSetItemToSpecial(itemStack: ItemStack, specialItem: SpecialItem)

}