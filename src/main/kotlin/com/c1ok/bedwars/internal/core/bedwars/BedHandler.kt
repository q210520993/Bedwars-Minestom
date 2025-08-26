package com.c1ok.bedwars.internal.core.bedwars

import com.c1ok.bedwars.api.game.bedwars.IBedWarsPlayer
import com.c1ok.bedwars.api.game.bedwars.Team
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import net.minestom.server.instance.block.BlockHandler

class BedHandler(val team: Team): BlockHandler {

    override fun onDestroy(destroy: BlockHandler.Destroy) {
        team.destroyBed()
    }

    fun canDestory(player: IBedWarsPlayer): Boolean {
        if (team.containsPlayer(player)) {
            player.miniPlayer.player?.sendMessage(Component.text("你不能破坏自家的床！"))
            return false
        }
        return true
    }

    override fun getKey(): Key {
        return Key.key("bedwars:bedestory")
    }

}