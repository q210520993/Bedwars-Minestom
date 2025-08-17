package com.c1ok.bedwars.api.game

interface GameTimer {

    /**
     * 添加一个时间点事件
     * @param tick 游戏刻(tick)触发时间
     * @param eventId 事件唯一标识符
     * @param callback 事件回调函数
     */
    fun addEvent(tick: Int, eventId: String, callback: () -> Unit)

    /**
     * 添加周期性事件
     * @param interval 触发间隔(单位: tick)
     * @param eventId 事件唯一标识符
     * @param callback 事件回调函数
     */
    fun addRepeatingEvent(interval: Int, eventId: String, callback: () -> Unit)

    /**
     * 添加延迟触发事件
     * @param delay 延迟时间(单位: tick)
     * @param eventId 事件唯一标识符
     * @param callback 事件回调函数
     */
    fun addDelayedEvent(delay: Int, eventId: String, callback: () -> Unit)

    /**
     * 添加延迟循环事件
     *
     * @param initialDelay 首次触发前的延迟（tick数）
     * @param interval 后续每次触发的时间间隔（tick数）
     * @param eventId 事件唯一标识符
     * @param callback 事件回调函数
     */
    fun addDelayedRepeatingEvent(
        initialDelay: Int,
        interval: Int,
        eventId: String,
        callback: () -> Unit
    )

    /**
     * 移除指定事件
     * @param eventId 要移除的事件ID
     */
    fun removeEvent(eventId: String)

    /**
     * 检查事件是否存在
     * @param eventId 查询的事件ID
     */
    fun hasEvent(eventId: String): Boolean

    /**
     * 清除所有计时器事件
     */
    fun clearEvents()

    /**
     * 获取当前游戏刻
     */
    fun getCurrentTick(): Int

    /**
     * 暂停计时器
     */
    fun pause()

    /**
     * 恢复计时器
     */
    fun resume()

    /**
     * 重置计时器并清除所有事件
     */
    fun reset()

    /**
     * 设置时间缩放因子(用于游戏加速/减速效果)
     * @param factor 时间缩放系数 (1.0 = 正常速度)
     */
    fun setTimeScale(factor: Float)

    /**
     * 添加关键帧序列(多个按顺序执行的事件)
     * @param sequenceId 序列唯一ID
     * @param events 事件列表及其延迟时间(相对于序列开始)
     */
    fun addSequence(sequenceId: String, events: List<Pair<Int, () -> Unit>>)

    /**
     * 开始或继续指定序列
     * @param sequenceId 要开始的序列ID
     */
    fun startSequence(sequenceId: String)

    /**
     * 暂停指定序列
     * @param sequenceId 要暂停的序列ID
     */
    fun pauseSequence(sequenceId: String)

    /**
     * 停止并重置指定序列
     * @param sequenceId 要停止的序列ID
     */
    fun stopSequence(sequenceId: String)


    /**
     * 更新计时器状态
     *
     * @param deltaTicks 要推进的 tick 数量
     */
    fun update(deltaTicks: Int)

}