package com.c1ok.bedwars.internal.feature.inventory.trade

import com.c1ok.bedwars.api.game.bedwars.IBedWarsPlayer

fun interface TradeInventoryCreator {
    fun createTradeInventory(game: IBedWarsPlayer): TradeInventory
}