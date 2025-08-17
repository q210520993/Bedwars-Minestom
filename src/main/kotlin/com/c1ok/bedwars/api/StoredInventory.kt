package com.c1ok.bedwars.api

import net.kyori.adventure.text.Component
import net.minestom.server.entity.GameMode
import net.minestom.server.item.ItemStack

open class StoredInventory(
    var armor: Array<ItemStack>,
    var displayName: Component?,
    var foodLevel: Int = 0,
    var inventory: Array<ItemStack>,
    var gamemode: GameMode,
    var xp: Float = 0f,
    var level: Int = 0
)