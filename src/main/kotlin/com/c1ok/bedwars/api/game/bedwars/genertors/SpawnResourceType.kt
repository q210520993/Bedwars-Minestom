package com.c1ok.bedwars.api.game.bedwars.genertors

import com.c1ok.bedwars.api.game.bedwars.IBedWarsPlayer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.ItemEntity
import net.minestom.server.item.Material

/**
 * 资源类型
 *
 * @property typeName 资源名称
 * @property displayName 资源显示名称
 * @property material 对应的物品材料
 * @property spawnCount 用于生成的物品数量 (默认1个)
 * @property spawnInterval 生成间隔 (Tick)
 * @property spawnLimit 单次生成的最大数量
 * @property spawnStrategy 生成策略 (地面实体/直接给予玩家等)
 *
 * 全部都是可变的
 */
open class SpawnResourceType(
    val id: String,
    var typeName: String,
    var displayName: String,
    var material: Material,
    var spawnCount: Int,
    var spawnInterval: Int,
    var point: Pos
) {
    /**
     * 特殊效果
     */
    open fun onCollect(player: IBedWarsPlayer, itemEntity: ItemEntity): Boolean {
        // 默认无效果
        return true
    }

}