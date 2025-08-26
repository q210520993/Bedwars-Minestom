package com.c1ok.bedwars.internal.core.bedwars.item.items

import com.c1ok.bedwars.api.game.bedwars.items.SpecialItem
import com.c1ok.bedwars.api.game.bedwars.items.meta.Clickable
import com.c1ok.bedwars.internal.core.bedwars.BaseBedWarsGame
import net.minestom.server.entity.Player

class SelectTeam(val game: BaseBedWarsGame): SpecialItem, Clickable {

    override val tagValue: String = "selectTeam"

    override fun onClick(player: Player) {
        val gamePlayer = game.gamePlayers[player.uuid] ?: return
        game.openTeamSelectInventory(gamePlayer)
    }

}