package com.c1ok.bedwars.internal.core.lobby

import com.c1ok.bedwars.Bedwars
import com.c1ok.bedwars.utils.setSpecial
import net.kyori.adventure.text.Component
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.event.player.PlayerSpawnEvent
import net.minestom.server.instance.anvil.AnvilLoader
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material

fun lobbyInit() {
    instanceSetter()
    listenerSetter()
}

private fun listenerSetter() {

    MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent::class.java) {
        it.spawningInstance = Bedwars.lobby
    }

    MinecraftServer.getGlobalEventHandler().addListener(PlayerSpawnEvent::class.java) {
        if(it.instance == Bedwars.lobby) {
            it.player.gameMode = GameMode.CREATIVE
            it.player.teleport(Pos(-2.5, 26.0, -40.0))
            if (it.isFirstSpawn) {
                it.player.inventory.addItemStack(ItemStack.builder(Material.COMPASS).customName(Component.text("测试游戏选择器")).
                setSpecial("selectGame").build())
            }
        }
    }

}

private fun instanceSetter() {
    val instance = MinecraftServer.getInstanceManager().createInstanceContainer()
    instance.chunkLoader = AnvilLoader("lobby")
    Bedwars.lobby = instance
}

