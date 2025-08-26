package com.c1ok.bedwars.internal.feature.inventory.trade

import com.c1ok.bedwars.api.game.bedwars.IBedWarsPlayer
import com.c1ok.bedwars.internal.feature.inventory.SimpleInventory
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minestom.server.event.inventory.InventoryPreClickEvent
import net.minestom.server.inventory.InventoryType
import net.minestom.server.inventory.click.Click.Left
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.tag.Tag
import java.util.concurrent.ConcurrentHashMap

class TradeInventory(val gamePlayer: IBedWarsPlayer): SimpleInventory(InventoryType.CHEST_2_ROW, Component.text("测试商店")) {

    // tag<name> --> itemstack
    val commodities = ConcurrentHashMap<String, ItemStack>()

    companion object {
        val name = Tag.String("trade-name")
        val type = Tag.String("trade-type")
        val value = Tag.Integer("trade-value")
    }

    init {
        setItemStack(0,
            ItemStack.builder(Material.DIAMOND_BLOCK)
                .lore(MiniMessage.miniMessage().deserialize("<green>需要1经验"))
                .set(name, "teamBlock")
                .set(value, 1)
                .build()
        )
        commodities["teamBlock"] = ItemStack.builder(Material.DIAMOND_BLOCK).amount(64).build()
    }

    override fun onPreClick(event: InventoryPreClickEvent) {
        event.isCancelled = true
        val name = event.clickedItem.getTag(name) ?: return
        val singleValue = event.clickedItem.getTag(value) ?: return
        val itemstack = commodities[name] ?: return
        var allValue = 0
        if (event.click !is Left) return
        allValue += singleValue
        if (allValue > gamePlayer.game.getPlayerResource(gamePlayer, "level").toInt()) {
            gamePlayer.miniPlayer.player?.sendMessage("你的不经验足,无法购买")
            return
        }
        gamePlayer.game.removePlayerResource(gamePlayer, "level", allValue)
        gamePlayer.miniPlayer.player?.inventory?.addItemStack(itemstack.withAmount(64))
    }

}
