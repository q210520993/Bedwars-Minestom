package com.c1ok.bedwars.internal.lobby

import com.c1ok.bedwars.Bedwars
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent
import net.minestom.server.event.player.PlayerSpawnEvent
import net.minestom.server.instance.anvil.AnvilLoader


fun set() {
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
        }
    }

}

private fun instanceSetter() {
    val instance = MinecraftServer.getInstanceManager().createInstanceContainer()
    instance.chunkLoader = AnvilLoader("lobby")
    Bedwars.lobby = instance
}

