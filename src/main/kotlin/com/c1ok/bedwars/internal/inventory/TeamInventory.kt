package com.c1ok.bedwars.internal.inventory

import com.c1ok.bedwars.api.game.bedwars.IBedWarsPlayer
import com.c1ok.bedwars.api.game.bedwars.Team
import net.kyori.adventure.text.Component
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.inventory.InventoryType

class TeamInventory(val gamePlayer: IBedWarsPlayer): SimpleInventory(InventoryType.CHEST_1_ROW, Component.text("起床战争队伍选择界面")),  Updateable {
    // 物品摆放位置 --> Team
    private val teams = HashMap<Int, Team>()

    val game = gamePlayer.game

    init {
        val sortedTeams = game.teams.sortedBy {
            it.priority
        }
        for ((index, team) in sortedTeams.withIndex()) {
            val amount = if (team.players.isEmpty()) 1 else team.players.size
            val itemstack = team.getTeamItemStack().withAmount(amount)
            setItemStack(index, itemstack)
            teams[index] = team
        }
    }

    override fun onPreClick(event: InventoryPreClickEvent) {
        event.isCancelled = true
        if (teams.containsKey(event.slot)) {
            teams[event.slot]!!.addPlayer(gamePlayer)
        }
        println(teams)
        println(event.slot)
    }

    // 当游戏更新的时候, 实际上就是onTick
    override fun onGameUpdate() {
        for ((index, team) in teams) {
            val amount = if (team.players.isEmpty()) 1 else team.players.size
            val itemstack = team.getTeamItemStack().withAmount(amount)
            setItemStack(index, itemstack)
            teams[index] = team
        }
    }

}