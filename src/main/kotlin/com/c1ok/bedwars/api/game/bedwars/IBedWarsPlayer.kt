package com.c1ok.bedwars.api.game.bedwars

import com.c1ok.bedwars.api.StoredInventory
import com.c1ok.bedwars.api.game.IMiniPlayer
import net.minestom.server.scoreboard.Sidebar
import java.util.concurrent.atomic.AtomicInteger

interface IBedWarsPlayer {

    var spectator: Boolean

    var team: Team?

    val game: BedWarsGame

    val miniPlayer: IMiniPlayer

    val storedInventory: StoredInventory?

    // 杀敌数
    val kills: AtomicInteger
    // 死亡数
    val deaths: AtomicInteger

    // 玩家信息
    val info: Sidebar

    fun tryLeave(game: BedWarsGame): Boolean

    fun updateInfo(time: Int)

    // 刷新玩家的游戏数据
    fun refreshPlayer()

    //当玩家离开时，加载玩家的一切数据
    fun restoreInv() {
        val player = miniPlayer.player ?: return
        val storedInventory = storedInventory ?: return
        storedInventory.inventory.withIndex().forEach {
            player.inventory.setItemStack(it.index, it.value)
        }
        player.exp = storedInventory.xp
        player.level = storedInventory.level
        player.displayName = storedInventory.displayName
        player.food = storedInventory.foodLevel
        player.helmet = storedInventory.armor[0]
        player.chestplate = storedInventory.armor[1]
        player.leggings = storedInventory.armor[2]
        player.boots = storedInventory.armor[3]
        player.gameMode = storedInventory.gamemode
    }

}