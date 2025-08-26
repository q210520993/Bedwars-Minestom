package com.c1ok.bedwars.internal.core.bedwars.item.items

import com.c1ok.bedwars.api.game.bedwars.items.SpecialItem
import com.c1ok.bedwars.api.game.bedwars.items.meta.Clickable
import com.c1ok.bedwars.internal.core.bedwars.BaseBedWarsGame
import com.c1ok.bedwars.internal.manager.PlayerManagerImpl
import net.minestom.server.entity.Player

class ExitTeam(val game: BaseBedWarsGame): SpecialItem, Clickable {

    override val tagValue: String = "exitTeam"

    override fun onClick(player: Player) {
        val mp = PlayerManagerImpl.getPlayer(player.uuid) ?: return
        game.leaveGame(mp)
    }

}