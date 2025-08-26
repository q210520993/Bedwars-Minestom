package com.c1ok.bedwars.internal.core.bedwars

import com.c1ok.bedwars.api.StoredInventory
import com.c1ok.bedwars.api.game.IMiniPlayer
import com.c1ok.bedwars.api.game.MiniGame
import com.c1ok.bedwars.api.game.bedwars.BedWarsGame
import com.c1ok.bedwars.api.game.bedwars.IBedWarsPlayer
import com.c1ok.bedwars.api.game.bedwars.Team
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.minestom.server.scoreboard.Sidebar
import java.time.Instant
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

open class BedWarsPlayer(override val miniPlayer: IMiniPlayer, override val game: BedWarsGame): IBedWarsPlayer {
    override var spectator: Boolean = false
    override val kills: AtomicInteger = AtomicInteger(0)
    override val deaths: AtomicInteger = AtomicInteger(0)

    override val info: Sidebar = buildSidebar()

    /**
     * 构建 Sidebar 初始状态
     */
    private fun buildSidebar(): Sidebar {
        val sidebar = Sidebar(Component.text("起床战争", TextColor.color(255, 255, 85)))
        var index = 1

        // 添加当前日期
        sidebar.createLine(Sidebar.ScoreboardLine("date", getCurrentDateText(), index++))

        // 空行
        sidebar.createLine(Sidebar.ScoreboardLine("air1", Component.text(""), index++))

        // 添加所有队伍的状态
        game.teams.forEach { team ->
            val teamLine = Sidebar.ScoreboardLine(
                team.id.toString(),
                getTeamLineContent(team),
                index++
            )
            sidebar.createLine(teamLine)
        }

        // 添加玩家死亡数显示
        sidebar.createLine(
            Sidebar.ScoreboardLine("deaths", getDeathsLineContent(), index++)
        )

        // 添加时间行（占位，初始为“none”）
        sidebar.createLine(Sidebar.ScoreboardLine("time", Component.text("none"), index))

        return sidebar
    }

    /**
     * 更新 Sidebar 的内容
     *
     * @param time 剩余时间
     */
    override fun updateInfo(time: Int) {
        info.updateLineContent("date", getCurrentDateText())
        info.updateLineContent("deaths", getDeathsLineContent())
        info.updateLineContent("time", Component.text("剩余时间: $time 秒"))

        game.teams.forEach { team ->
            info.updateLineContent(
                team.id.toString(),
                getTeamLineContent(team)
            )
        }
    }

    /**
     * 获取队伍状态行内容
     *
     * @param team 队伍实例
     * @return 队伍的状态文本
     */
    private fun getTeamLineContent(team: Team): Component {
        val stateText = getTeamState(team)
        return MiniMessage.miniMessage().deserialize("${team.name} $stateText")
    }

    /**
     * 获取当前日期文本
     *
     * @return 表示当前日期的文本组件
     */
    private fun getCurrentDateText(): Component {
        return Component.text(Date.from(Instant.now()).toString())
    }

    /**
     * 获取死亡数显示行内容
     *
     * @return 死亡数的文本组件
     */
    private fun getDeathsLineContent(): Component {
        val deathsLabel = Component.text("死亡数: ")
            .color(TextColor.color(0xFFD700)) // 金色
            .decorate(TextDecoration.BOLD)

        val deathsCount = Component.text(kills.get())
            .color(TextColor.color(0x00FF00)) // 绿色
            .decorate(TextDecoration.UNDERLINED)

        return deathsLabel.append(deathsCount)
    }

    /**
     * 获取队伍状态：是否床已被摧毁
     *
     * @param team 队伍
     * @return 队伍的状态，绿色勾（未摧毁）或红色叉（已摧毁）
     */
    private fun getTeamState(team: Team): String {
        return if (!team.isBedDestroyed) {
            "<green>√" // 床未被摧毁
        } else {
            "<red>×" // 床已被摧毁
        }
    }

    override var team: Team? = null
        set(value) {
            if (game.currentState() != MiniGame.GameState.LOBBY) field = null

            field = value
        }

    override val storedInventory: StoredInventory? = run {
        val minestomPlayer = miniPlayer.player ?: return@run null
        val helmet = minestomPlayer.helmet
        val chestplate = minestomPlayer.chestplate
        val legging = minestomPlayer.leggings
        val boots = minestomPlayer.boots
        val eq = arrayOf(helmet, chestplate, legging, boots)
        val inv = minestomPlayer.inventory.itemStacks
        return@run StoredInventory(eq,
            minestomPlayer.displayName,
            minestomPlayer.food, inv,
            minestomPlayer.gameMode,
            minestomPlayer.exp,
            minestomPlayer.level
        )
    }

    override fun tryLeave(game: BedWarsGame): Boolean {
        game.leaveGame(miniPlayer)
        return true
    }

    override fun refreshPlayer() {
        val minestomP = this.miniPlayer.player ?: return
//        if (storedInventory != null) {
//            val helmet = minestomP.helmet
//            val chestplate = minestomP.chestplate
//            val legging = minestomP.leggings
//            val boots = minestomP.boots
//            val eq = arrayOf(helmet, chestplate, legging, boots)
//        }
        minestomP.exp = 0.0F
        minestomP.closeInventory()
        minestomP.inventory.clear()
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + game.hashCode()
        result = prime * result + miniPlayer.hashCode()
        return result
    }

    // 注意：要实现正确的匹配，必须同时重写 equals() 方法
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IBedWarsPlayer) return false

        return this.game == other.game &&
                this.miniPlayer == other.miniPlayer
    }


}