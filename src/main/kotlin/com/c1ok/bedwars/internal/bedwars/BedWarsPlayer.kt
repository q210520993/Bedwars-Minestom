package com.c1ok.bedwars.internal.bedwars

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

    override val info: Sidebar = Sidebar(Component.text("起床战争", TextColor.color(255, 255, 85))).apply {
        var index = 1
        createLine(Sidebar.ScoreboardLine("date", Component.text(Date.from(Instant.now()).toString()), index))
        index++
        createLine(Sidebar.ScoreboardLine("air1", Component.text(""), index))
        index++
        game.teams.forEach {
            createLine(
                Sidebar.ScoreboardLine(it.id.toString(), MiniMessage.miniMessage().
                deserialize("${it.name} <state>", Placeholder.unparsed("state", getTeamState(it))), index)
            )
            index ++
        }
        createLine(Sidebar.ScoreboardLine("deaths", Component.text("死亡数: ").color(TextColor.color(0xFFD700))
            .decorate(TextDecoration.BOLD).append(Component.text(kills.get()).color(TextColor.color(0x00FF00))
                .decorate(TextDecoration.UNDERLINED)), index))
        index ++
        createLine(Sidebar.ScoreboardLine("time",Component.text("none"), index))
    }

    override fun updateInfo(time: Int) {
        info.updateLineContent("date", Component.text(Date.from(Instant.now()).toString()))
        info.updateLineContent("deaths", Component.text("死亡数: ").color(TextColor.color(0xFFD700))
            .decorate(TextDecoration.BOLD).append(Component.text(kills.get()).color(TextColor.color(0x00FF00))
                .decorate(TextDecoration.UNDERLINED)))
        info.updateLineContent("time", Component.text(time))
        game.teams.forEach {
            info.updateLineContent(it.id.toString(), MiniMessage.miniMessage().deserialize("${it.name} <state>", Placeholder.unparsed("state", getTeamState(it))))
        }
    }

    private fun getTeamState(team: Team): String {
        if (!team.isBedDestroyed) {
            return "<bold><green>√</bold>"
        }
        return "<bold><red>×</bold>"
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