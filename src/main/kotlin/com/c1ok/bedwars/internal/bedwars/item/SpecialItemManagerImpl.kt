package com.c1ok.bedwars.internal.bedwars.item

import com.c1ok.bedwars.api.game.bedwars.items.SpecialItem
import com.c1ok.bedwars.api.game.bedwars.items.SpecialManager
import net.minestom.server.item.ItemStack
import net.minestom.server.tag.Tag
import net.minestom.server.tag.TagHandler
import java.util.concurrent.ConcurrentHashMap

class SpecialItemManagerImpl: SpecialManager {

    companion object {
        val tag = Tag.String("special_item_manager")

        private val tagHandler: TagHandler = TagHandler.newHandler()
    }

    private val specialItems = ConcurrentHashMap<String, SpecialItem>()

    override fun getSpecialHandler(itemStack: ItemStack): SpecialItem? {
        val id = itemStack.getTag(tag) ?: return null
        return specialItems[id]
    }

    override fun getSpecialHandler(id: String): SpecialItem? {
        return specialItems[id]
    }

    override fun isSpecial(itemStack: ItemStack): Boolean {
        return itemStack.hasTag(tag)
    }

    override fun addSpecial(specialItem: SpecialItem): SpecialItem? {
        return specialItems.put(specialItem.tagValue, specialItem)
    }

    override fun setItemToSpecial(itemStack: ItemStack, specialItem: SpecialItem): Boolean {
        if (itemStack.hasTag(tag)) {
            return false
        }
        tagHandler.setTag(tag, specialItem.tagValue)
        return true
    }

    override fun unsafeSetItemToSpecial(itemStack: ItemStack, specialItem: SpecialItem) {
        tagHandler.setTag(tag, specialItem.tagValue)
    }

}