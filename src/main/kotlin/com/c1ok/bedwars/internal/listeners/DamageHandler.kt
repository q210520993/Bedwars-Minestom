package com.c1ok.bedwars.internal.listeners

import net.minestom.server.event.entity.EntityDamageEvent

object DamageHandler {
    fun regsiter() {
        getBedwarsEventChild().addListener(EntityDamageEvent::class.java) {

        }
    }
}