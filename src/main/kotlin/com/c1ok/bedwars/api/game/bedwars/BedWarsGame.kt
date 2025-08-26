package com.c1ok.bedwars.api.game.bedwars

import com.c1ok.bedwars.api.game.IMiniPlayer
import com.c1ok.bedwars.api.game.MiniGame
import com.c1ok.bedwars.api.game.bedwars.genertors.SpawnResourceType
import com.c1ok.bedwars.api.game.bedwars.items.SpecialManager
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.event.entity.EntityDamageEvent
import net.minestom.server.event.player.PlayerBlockBreakEvent
import net.minestom.server.event.player.PlayerBlockPlaceEvent
import net.minestom.server.event.player.PlayerDeathEvent
import net.minestom.server.event.player.PlayerRespawnEvent
import net.minestom.server.instance.Instance
import java.util.*
import java.util.concurrent.CompletableFuture

interface BedWarsGame: MiniGame {

    // 得到该游戏展示的名字
    val displayName: String

    val gameStateMachine: GameStateMachine

    // 游戏的世界
    val gameInstance: Instance

    // 游戏结束时玩家返回的地方
    val lobby: Instance

    val teams: MutableList<Team>

    val gamePlayers: MutableMap<UUID, IBedWarsPlayer>

    val bedPlayerCreator: BedPlayerCreator

    val waitingPos: Pos

    fun getBedwarsPlayer(miniPlayer: IMiniPlayer): IBedWarsPlayer? {
        val pl = miniPlayer.player ?: return null
        return getBedwarsPlayer(pl)
    }

    fun getBedwarsPlayer(player: Player): IBedWarsPlayer? {
        return getBedwarsPlayer(player.uuid)
    }

    fun getBedwarsPlayer(uuid: UUID): IBedWarsPlayer?

    /**
     * 床被破坏事件处理
     *
     * 核心游戏机制处理：
     *   - 检查破坏者是否是床所属队伍的敌对成员
     *   - 更新游戏状态（队伍失去复活能力）
     *   - 广播全服消息和音效提示
     *   - 触发后续效果（如队伍团灭检测）
     *   - 显示特效（爆炸粒子效果等）
     */
    fun onBlockBreak(event: PlayerBlockBreakEvent)

    /**
     * 方块被放置事件处理
     *
     * 核心游戏机制处理：
     *   - 为Insatnce复用房间，方块放置将为方块生成一个特殊的blockhandler，rebuild时自动清理
     *   - 特殊的方块放置，如安全墙这些
     */
    fun onBlockPlace(event: PlayerBlockPlaceEvent)

    /**
     * 房间开启（准备阶段）
     *
     * 非游戏开始，而是：
     *   - 加载地图资源
     *   - 初始化游戏变量
     *   - 放置初始结构（床、资源点等）
     *   - 进入玩家等待状态（倒计时开始）
     *   泛型中的Boolean指是否为第一次init
     */
    fun init(): CompletableFuture<Boolean>
    // 当游戏刚刚进入倒计时的时候触发
    fun onGameInCountdown()
    fun onGameStart()

    /**
     * 房间永久关闭
     *
     * 完全销毁房间实例：
     *   - 清理所有玩家数据
     *   - 释放地图资源
     */
    fun close(): CompletableFuture<Void>

    /**
     * 房间重置（新局准备）
     *
     * 保留房间实例但重置游戏状态：
     *   - 重置所有实体（玩家、生物、掉落物）
     *   - 恢复地图原始状态（重建床、方块结构）
     *   - 重置队伍状态（重生次数、装备等）
     *   - 保留房间配置参数（玩家人数、地图选择等）
     */
    fun rebuild(): CompletableFuture<Void>

    fun onEnd(): CompletableFuture<Void>

    fun killPlayer(entity: IBedWarsPlayer): Boolean

    // 在游戏时间更新状态中每一秒都会触发什么

    /**
     * @param time Int 当前的时间
     */
    fun onGameSecondCount(time: Int)
    /**
     * @param time Int 当前的时间
     */
    fun onGameWatingSecond(time: Int)

    fun onPlayerDeath(event: PlayerDeathEvent) {}
    fun onPlayerDamaged(event: EntityDamageEvent) {}
    fun onPlayerRespawn(event: PlayerRespawnEvent) {}
    fun getSpecialItemManager(): SpecialManager

    fun addPlayerResource(player: IBedWarsPlayer, resource: String, count: Number): Boolean
    fun getPlayerResource(player: IBedWarsPlayer, resource: String): Number
    fun isGameResource(resourceType: SpawnResourceType): Boolean
    fun setPlayerResource(player: IBedWarsPlayer, resource: String, count: Number)
    fun removePlayerResource(player: IBedWarsPlayer, resource: String, count: Number): Boolean

    fun openTeamSelectInventory(player: IBedWarsPlayer)

}