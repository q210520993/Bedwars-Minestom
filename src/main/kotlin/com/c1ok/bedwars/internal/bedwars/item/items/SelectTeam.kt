package com.c1ok.bedwars.internal.bedwars.item.items

import com.c1ok.bedwars.api.game.bedwars.IBedWarsPlayer
import com.c1ok.bedwars.api.game.bedwars.items.SpecialItem
import com.c1ok.bedwars.api.game.bedwars.items.meta.Clickable

object SelectTeam: SpecialItem, Clickable {

    override val tagValue: String = "selectTeam"

    override fun onClick(player: IBedWarsPlayer) {
        player.game.openTeamSelectInventory(player)
    }

}