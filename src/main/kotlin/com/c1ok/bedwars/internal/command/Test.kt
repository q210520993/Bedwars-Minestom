package com.c1ok.bedwars.internal.command

import com.c1ok.bedwars.internal.feature.inventory.GameInventory
import net.minestom.server.command.builder.Command
import net.minestom.server.entity.Player

object Test: Command("TestInventory") {
    init {
        addSyntax({ sender, context ->
            val player = sender as Player
            player.openInventory(GameInventory.inventory)
        })
    }
}