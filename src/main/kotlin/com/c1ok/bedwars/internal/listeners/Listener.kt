package com.c1ok.bedwars.internal.listeners

import net.minestom.server.MinecraftServer
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode

fun registerNode() {
    MinecraftServer.getGlobalEventHandler().addChild(EventNode.all("bedwars").setPriority(4))
}

fun getBedwarsEventChild(): EventNode<Event> {
    return MinecraftServer.getGlobalEventHandler().findChildren("bedwars")[0]
}