package com.c1ok.bedwars.internal.feature.inventory

import net.minestom.server.MinecraftServer
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.InventoryType
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import java.util.function.BiPredicate

object TestInventory {
    val inventory = Inventory(InventoryType.CHEST_6_ROW, "测试界面")
    init {
        inventory.addItemStack(ItemStack.builder(Material.BOW).build())
        val node = EventNode.type("click", EventFilter.INVENTORY, BiPredicate { _, inv ->
            return@BiPredicate inventory == inv
        }).addListener(InventoryPreClickEvent::class.java) {
        }
        MinecraftServer.getGlobalEventHandler().addChild(node)
    }
}