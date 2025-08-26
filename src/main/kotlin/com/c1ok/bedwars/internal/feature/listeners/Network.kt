package com.c1ok.bedwars.internal.feature.listeners

import com.c1ok.bedwars.internal.core.network.onPlayerSpawnNetwork
import net.minestom.server.MinecraftServer
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerSpawnEvent

object NetworkListener {
    internal fun registerNetworkNode() {
        val node = EventNode.all("beacon-packet").setPriority(999)
        MinecraftServer.getGlobalEventHandler().addChild(node)
    }

    fun getNetworkNode(): EventNode<in Event> {
        return MinecraftServer.getGlobalEventHandler().findChildren("beacon-packet").first()
    }

    fun regsiterListener() {
        getNetworkNode().addListener(PlayerSpawnEvent::class.java) {
            onPlayerSpawnNetwork(it)
        }
    }

}