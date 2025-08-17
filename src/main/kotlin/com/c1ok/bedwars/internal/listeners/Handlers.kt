package com.c1ok.bedwars.internal.listeners

import com.c1ok.bedwars.Bedwars
import com.c1ok.bedwars.api.game.bedwars.BedWarsGame
import com.c1ok.bedwars.api.game.bedwars.genertors.ResourceEntity
import com.c1ok.bedwars.api.game.bedwars.items.meta.Clickable
import com.c1ok.bedwars.internal.manager.BedWarsManager
import com.c1ok.bedwars.internal.manager.PlayerManagerImpl
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.event.entity.EntityDamageEvent
import net.minestom.server.event.item.PickupItemEvent
import net.minestom.server.event.player.PlayerBlockBreakEvent
import net.minestom.server.event.player.PlayerBlockPlaceEvent
import net.minestom.server.event.player.PlayerDeathEvent
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerRespawnEvent
import net.minestom.server.event.player.PlayerUseItemEvent

object Handlers {
    fun register() {
        getBedwarsEventChild().addListener(PlayerBlockPlaceEvent::class.java) {
            val player = PlayerManagerImpl.getPlayer(it.player.uuid) ?: return@addListener
            val game = (player.game as? BedWarsGame) ?: return@addListener
            game.onBlockPlace(it)
        }
        getBedwarsEventChild().addListener(PlayerBlockBreakEvent::class.java) {
            if (it.entity.instance == Bedwars.lobby) it.isCancelled = true
            val player = PlayerManagerImpl.getPlayer(it.player.uuid) ?: return@addListener
            val game = (player.game as? BedWarsGame) ?: return@addListener
            game.onBlockBreak(it)
        }
        getBedwarsEventChild().addListener(PlayerDeathEvent::class.java) {
            val player = PlayerManagerImpl.getPlayer(it.player.uuid) ?: return@addListener
            val game = (player.game as? BedWarsGame) ?: return@addListener
            game.onPlayerDeath(it)
        }

        getBedwarsEventChild().addListener(PlayerRespawnEvent::class.java) {
            if (it.player.instance == Bedwars.lobby) it.respawnPosition = Pos(-2.5, 26.0, -40.0)
            val player = PlayerManagerImpl.getPlayer(it.player.uuid) ?: return@addListener
            val game = (player.game as? BedWarsGame) ?: return@addListener
            game.onPlayerRespawn(it)
        }

        getBedwarsEventChild().addListener(PickupItemEvent::class.java) {
            val itemEntity = it.itemEntity
            if (itemEntity !is ResourceEntity) return@addListener
            val minestomPlayer = (it.livingEntity as? Player) ?: return@addListener
            val player = PlayerManagerImpl.getPlayer(minestomPlayer.uuid) ?: return@addListener
            val game = (player.game as? BedWarsGame) ?: return@addListener
            val ist = game.isGameResource(itemEntity.resourceType)
            val gamePlayer = game.gamePlayers[player.uuid] ?: return@addListener
            if (ist) {
                itemEntity.resourceType.onCollect(gamePlayer, itemEntity)
            } else return@addListener
        }
        getBedwarsEventChild().addListener(EntityDamageEvent::class.java) {
            if (it.entity.instance == Bedwars.lobby) it.isCancelled = true
        }
        getBedwarsEventChild().addListener(PlayerUseItemEvent::class.java) {
            val player = PlayerManagerImpl.getPlayer(it.player.uuid) ?: return@addListener
            val game = (player.game as? BedWarsGame) ?: return@addListener
            val gamePlayer = game.gamePlayers[player.uuid] ?: return@addListener
            val isSpecial = game.getSpecialItemManager().isSpecial(it.itemStack)
            if (!isSpecial) return@addListener
            val special = game.getSpecialItemManager().getSpecialHandler(it.itemStack) ?: return@addListener
            if (special is Clickable) {
                special.onClick(gamePlayer)
            }
        }

        getBedwarsEventChild().addListener(PlayerDisconnectEvent::class.java) {
            val player = PlayerManagerImpl.getPlayer(it.player.uuid) ?: return@addListener
            val game = (player.game as? BedWarsGame) ?: return@addListener
            game.gamePlayers[player.uuid] ?: return@addListener
            game.leaveGame(player)
        }

        getBedwarsEventChild().addListener(EntityDamageEvent::class.java) {
            val player = it.entity as? Player ?: return@addListener
            val player_ = PlayerManagerImpl.getPlayer(player.uuid) ?: return@addListener
            val game = (player_.game as? BedWarsGame) ?: return@addListener
            game.onPlayerDamaged(event = it)
        }

    }
}