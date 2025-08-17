package com.c1ok.bedwars.internal.bedwars.item.items

import com.c1ok.bedwars.api.game.bedwars.IBedWarsPlayer
import com.c1ok.bedwars.api.game.bedwars.items.SpecialItem
import com.c1ok.bedwars.api.game.bedwars.items.meta.Clickable

object ExitTeam: SpecialItem, Clickable {

    override val tagValue: String = "exitTeam"

    override fun onClick(player: IBedWarsPlayer) {
        player.game.leaveGame(player.miniPlayer)
    }

}