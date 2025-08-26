package com.c1ok.bedwars.api.game.bedwars

import com.c1ok.bedwars.internal.feature.inventory.trade.TradeInventoryCreator
import net.minestom.server.coordinate.Point
import net.minestom.server.coordinate.Pos
import net.minestom.server.instance.block.Block
import net.minestom.server.inventory.Inventory
import net.minestom.server.item.ItemStack
import net.minestom.server.utils.Direction
import java.util.*

interface Team {

    // 队伍唯一标识符 - 新增（用于唯一识别队伍）
    val id: UUID

    // 队伍名称（帽子绿，姨妈红...）
    val name: String

    // 最小玩家数（推荐设置默认值）
    val minPlayers: Int get() = 1

    // 最大玩家数（推荐设置默认值）
    val maxPlayers: Int get() = 4

    // 床的位置
    val bedPoint: Point

    //床
    val bedBlock: Block

    // 床的方向
    val bedDirection: Direction

    // 当前游戏实例（改名更符合Kotlin规范）- 改进
    val currentGame: BedWarsGame

    val respawnPoint: Pos

    val priority: Int

    // 当前队伍玩家列表（改为Set避免重复）- 改进
    val players: Set<IBedWarsPlayer>

    // 床是否被摧毁（改名更明确）- 改进
    var isBedDestroyed: Boolean

    // 队伍是否被团灭
    var isWipedOut: Boolean

    val tradeInventoryCreator: TradeInventoryCreator

    fun createBed()
    fun destroyBed()

    // 队伍共享库存（更名明确用途）- 改进
    val sharedInventory: Inventory

    // 玩家管理方法
    fun containsPlayer(player: IBedWarsPlayer): Boolean
    fun addPlayer(player: IBedWarsPlayer): Boolean
    fun removePlayer(player: IBedWarsPlayer): Boolean

    // 选择界面的物品
    fun getTeamItemStack(): ItemStack

    fun onGameStop()
}