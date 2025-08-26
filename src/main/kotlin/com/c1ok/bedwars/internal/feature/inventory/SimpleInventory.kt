package com.c1ok.bedwars.internal.feature.inventory

import net.kyori.adventure.text.Component
import net.minestom.server.MinecraftServer
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.inventory.InventoryClickEvent
import net.minestom.server.event.inventory.InventoryCloseEvent
import net.minestom.server.event.inventory.InventoryOpenEvent
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.InventoryType
import java.util.function.BiPredicate

abstract class SimpleInventory(inventory: InventoryType, display: Component): Inventory(inventory, display) {
    companion object{
        fun regsiter() {
            val node = EventNode.type("simpleinventory", EventFilter.INVENTORY, BiPredicate { _, inv ->
                return@BiPredicate inv is SimpleInventory
            }).addListener(InventoryPreClickEvent::class.java) {
                val inventory = it.inventory as SimpleInventory
                inventory.onPreClick(it)
            }.addListener(InventoryCloseEvent::class.java) {
                val inventory = it.inventory as SimpleInventory
                inventory.onClose(it)
            }.addListener(InventoryClickEvent::class.java) {
                val inventory = it.inventory as SimpleInventory
                inventory.onClick(it)
            }.addListener(InventoryOpenEvent::class.java) {
                val inventory = it.inventory as SimpleInventory
                inventory.onOpen(it)
            }
            MinecraftServer.getGlobalEventHandler().addChild(node)
        }
    }

    open fun onPreClick(event: InventoryPreClickEvent) {}
    open fun onClick(event: InventoryClickEvent) {}
    open fun onClose(event: InventoryCloseEvent) {}
    open fun onOpen(event: InventoryOpenEvent) {}

}