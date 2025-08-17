package com.c1ok.bedwars.api.game.bedwars

/**
 * 这个接口，代表着一个起床战争的伴生类
 * 它会随着起床战争的开启而开启
 * 会随着起床战争的关闭而关闭
 * 会随着起床战争状态的更新而更新
 */
interface BedwarsBound {

    val priority: Int
    val id: String

    // 在游戏房间初始化时调用
    fun onInit() {}

    // 在游戏开始时调用
    fun onGameStart() {}

    // 在游戏结束时调用
    fun onEnd() {}

    // 在游戏房间关闭时调用
    fun onClose() {}

    // 在游戏重建时调用
    fun onRebuild() {}

    fun onTick() {}

}