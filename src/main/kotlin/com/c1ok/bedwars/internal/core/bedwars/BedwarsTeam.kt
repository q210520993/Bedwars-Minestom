package com.c1ok.bedwars.internal.core.bedwars

import com.c1ok.bedwars.api.game.MiniGame
import com.c1ok.bedwars.api.game.bedwars.BedWarsGame
import com.c1ok.bedwars.api.game.bedwars.IBedWarsPlayer
import com.c1ok.bedwars.api.game.bedwars.Team
import com.c1ok.bedwars.internal.feature.inventory.trade.TradeInventory
import com.c1ok.bedwars.internal.feature.inventory.trade.TradeInventoryCreator
import com.c1ok.bedwars.utils.placeBed
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.Title
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.entity.Player
import net.minestom.server.instance.block.Block
import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.InventoryType
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.utils.Direction
import java.util.*

open class BedwarsTeam(
    // MiniMessage支持
    final override val name: String,
    override val bedPoint: Point,
    override val bedBlock: Block,
    override val bedDirection: Direction,
    override val currentGame: BedWarsGame,
    override val respawnPoint: Pos,
    override val id: UUID = UUID.randomUUID(),
    override val minPlayers: Int = 1,
    override val maxPlayers: Int = 4,
    override val priority: Int
) : Team {

    override val players: MutableSet<IBedWarsPlayer> = mutableSetOf()
    override var isBedDestroyed: Boolean = false
    override var isWipedOut: Boolean = false
        get() {
            if (field) return field
            return players.all {
                it.spectator
            }
        }
        set(value) {
            if (value && !field) {
                if (!isBedDestroyed) {
                    isBedDestroyed = true
                }
                field = true
            }
        }

    override fun createBed() {
        placeBed(this)
    }

    override val tradeInventoryCreator: TradeInventoryCreator = TradeInventoryCreator {
        return@TradeInventoryCreator TradeInventory(it)
    }


    override fun destroyBed() {
        isBedDestroyed = true
        currentGame.gamePlayers.forEach {
            val minestomPlayer = it.value.miniPlayer.player ?: return@forEach
            val component = MiniMessage.miniMessage().deserialize("${name}的床被破坏！")
            minestomPlayer.showTitle(Title.title(component, Component.empty(), Title.DEFAULT_TIMES))
        }
    }

    override val sharedInventory: Inventory = Inventory(InventoryType.CHEST_3_ROW, name)

    override fun containsPlayer(player: IBedWarsPlayer): Boolean {
        return players.contains(player)
    }

    override fun getTeamItemStack(): ItemStack {
        return ItemStack.builder(Material.AIR).build()
    }

    @Synchronized
    override fun addPlayer(player: IBedWarsPlayer): Boolean {
        if (currentGame.currentState() != MiniGame.GameState.LOBBY) return false
        if (players.size > maxPlayers) return false
        if (player.team != null) {
            player.team?.removePlayer(player)
        }
        player.team = this
        player.miniPlayer.player?.let {
            setPlayerDisplay(it)
        }
        return players.add(player)
    }

    fun setPlayerDisplay(player: Player) {
        player.displayName = MiniMessage.miniMessage().deserialize("${name}${player.username}")
    }

    override fun removePlayer(player: IBedWarsPlayer): Boolean {
        if (currentGame.currentState() == MiniGame.GameState.LOBBY) {
            return players.remove(player)
        }
        if (currentGame.currentState() == MiniGame.GameState.STARTING) {
            player.spectator = true
            player.miniPlayer.player?.gameMode = GameMode.SPECTATOR
            return players.remove(player)
        }
        return false
    }

    override fun onGameStop() {
        players.clear()
    }

    fun getPlayers(isAlive: Boolean): List<IBedWarsPlayer> {
        if (isAlive) {
            return players.filter { !it.spectator }.toList()
        }
        return players.toList()
    }

    override fun toString(): String {
        return "" +
                "ID: ${name},isWiped: ${isWipedOut}, " +
                "alivePlayers: ${getPlayers(isAlive = true)}, " +
                "allPlayers: ${getPlayers(isAlive = false).size}" +
                "bedBreaked: $isBedDestroyed"
    }

}