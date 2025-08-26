package com.c1ok.bedwars.internal.core.bedwars.exp

import com.c1ok.bedwars.api.game.IMiniPlayer
import com.c1ok.bedwars.api.game.MiniGame
import com.c1ok.bedwars.api.game.bedwars.BedPlayerCreator
import com.c1ok.bedwars.api.game.bedwars.IBedWarsPlayer
import com.c1ok.bedwars.api.game.bedwars.genertors.Generator
import com.c1ok.bedwars.api.game.bedwars.genertors.SpawnResourceType
import com.c1ok.bedwars.api.game.bedwars.items.SpecialManager
import com.c1ok.bedwars.internal.core.bedwars.*
import com.c1ok.bedwars.internal.core.bedwars.bounds.*
import com.c1ok.bedwars.internal.core.bedwars.exp.gen.Iron
import com.c1ok.bedwars.internal.core.bedwars.item.SpecialItemManagerImpl
import com.c1ok.bedwars.internal.core.bedwars.item.items.ExitTeam
import com.c1ok.bedwars.internal.core.bedwars.item.items.SelectTeam
import com.c1ok.bedwars.internal.feature.inventory.TeamInventory
import com.c1ok.bedwars.internal.feature.inventory.trade.TradeInventory
import com.c1ok.bedwars.internal.feature.inventory.trade.TradeInventoryCreator
import com.c1ok.bedwars.utils.setSpecial
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import net.minestom.server.MinecraftServer
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.GameMode
import net.minestom.server.instance.Instance
import net.minestom.server.instance.anvil.AnvilLoader
import net.minestom.server.instance.block.Block
import net.minestom.server.inventory.Inventory
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import net.minestom.server.scoreboard.Sidebar
import net.minestom.server.sound.SoundEvent
import net.minestom.server.utils.Direction
import java.util.*
import java.util.concurrent.CompletableFuture

class ExpBedWars: BaseBedWarsGame("test", 64, 2, Pos(-190.0, 58.0, 463.0)) {

    override fun createInventory(gamePlayer: IBedWarsPlayer): Inventory {
        return TeamInventory(gamePlayer)
    }

    override fun createInstance(): Instance {
        val instance = MinecraftServer.getInstanceManager().createInstanceContainer()
        instance.chunkLoader = AnvilLoader("test")
        return instance
    }

    override fun init(): CompletableFuture<Boolean> {
        return super.init().thenApply {
            if (it) {
                specialManager.addSpecial(SelectTeam(this))
                specialManager.addSpecial(ExitTeam(this))
            }
            return@thenApply it
        }
    }

    override fun openTeamSelectInventory(player: IBedWarsPlayer) {
        val minestomPlayer = player.miniPlayer.player ?: return
        minestomPlayer.openInventory(createInventory(player))
    }

    override fun createUUID(): UUID {
        return gameInstance.uuid
    }

    override fun registerBounds() {
        addBound(GameDataCleaner(this))
        addBound(PlayerTeleport(this))
        addBound(GeneratorBound(this))
        addBound(TeamInventoryTick(this))
        addBound(GameTrader(this))
    }

    override fun createPlayer(): BedPlayerCreator {
        return BedPlayerCreator { bedWarsGame, miniPlayer -> return@BedPlayerCreator BedWarsPlayer(miniPlayer, bedWarsGame) }
    }

    val lobbySidebar = Sidebar(Component.text("起床战争", TextColor.color(255, 255, 85))).apply {
        val map = MiniMessage.miniMessage().deserialize("<white>地图: <green>花园")
        val team = MiniMessage.miniMessage().deserialize("<white>队伍: <green>4人 4队")
        createLine(Sidebar.ScoreboardLine("none1", Component.text(""), 15))
        createLine(Sidebar.ScoreboardLine("map", map, 14))
        createLine(Sidebar.ScoreboardLine("team", team, 13))
        createLine(Sidebar.ScoreboardLine("air1", Component.text(""), 12))
        val people = MiniMessage.miniMessage().deserialize("<white>玩家: <green><current>/64", Placeholder.unparsed("current", gamePlayers.values.size.toString()))
        createLine(Sidebar.ScoreboardLine("people", people, 11))
        createLine(Sidebar.ScoreboardLine("none2", Component.text(""), 10))
        val start = MiniMessage.miniMessage().deserialize("<white>即将在<green>120<white>秒后开始")
        createLine(Sidebar.ScoreboardLine("start", start, 9))
        createLine(Sidebar.ScoreboardLine("none3", Component.text(""), 8))
        createLine(Sidebar.ScoreboardLine("mode", MiniMessage.miniMessage().deserialize("<white>你的模式: <green>经验模式"), 7))
        createLine(Sidebar.ScoreboardLine("none4", Component.text(""), 6))
        createLine(Sidebar.ScoreboardLine("server",MiniMessage.miniMessage().deserialize("<white>线程数: <green>3"), 5))
        createLine(Sidebar.ScoreboardLine("air2", Component.text(""), 4))
        createLine(Sidebar.ScoreboardLine("footer", Component.text("minestom.net", TextColor.color(255, 255, 85)), 3))
    }

    private val specialManager = SpecialItemManagerImpl()

    private fun updateWatingSidebar(time: Int) {
        val start = MiniMessage.miniMessage().deserialize("<white>即将在<green><timex><white>秒后开始", Placeholder.unparsed("timex", time.toString()))
        lobbySidebar.updateLineContent("start", start)
        val people = MiniMessage.miniMessage().deserialize("<white>玩家: <green><current>/64", Placeholder.unparsed("current", gamePlayers.values.size.toString()))
        lobbySidebar.updateLineContent("people", people)
    }

    override fun onTick() {
        if (currentState() == MiniGame.GameState.LOBBY) {
            updateWatingSidebar(gameStateMachine.waiting_timer.get())
        }
        if (currentState() == MiniGame.GameState.STARTING) {
            gamePlayers.values.forEach {
                val player = it.miniPlayer.player ?: return@forEach
                it.updateInfo(gameStateMachine.game_timer.get())
                if(!it.info.players.contains(player)) {
                    it.info.addViewer(player)
                }
            }
        }
    }


    override fun getSpecialItemManager(): SpecialManager {
        return specialManager
    }

    override fun initTeams() {
        val team1 = object : BedwarsTeam("<red>裤衩红",
            Pos(-194.0,45.0,547.0),
            Block.RED_BED, Direction.EAST,
            this,
            Pos(-181.4,45.0,546.0),
            priority = 1,
            minPlayers = 1,
            maxPlayers = 32,
        ) {
            override val tradeInventoryCreator: TradeInventoryCreator = TradeInventoryCreator {
                return@TradeInventoryCreator TradeInventory(it)
            }

            override fun getTeamItemStack(): ItemStack {
                return ItemStack.builder(Material.RED_WOOL).build()
            }
        }
        val team2 = object: BedwarsTeam("<green>色发绿",
            Pos(-187.0,45.0,373.0),
            Block.RED_BED, Direction.EAST,
            this,
            Pos(-187.0,45.0,373.0),
            priority = 2,
            minPlayers = 1,
            maxPlayers = 32
        ) {
            override fun getTeamItemStack(): ItemStack {
                return ItemStack.builder(Material.GREEN_WOOL).build()
            }
            override val tradeInventoryCreator: TradeInventoryCreator = TradeInventoryCreator {
                return@TradeInventoryCreator TradeInventory(it)
            }
        }
        teams.add(team1)
        teams.add(team2)
        generator.addGenerator(Iron("GREEN_IRON_1", Pos(-191.0, 46.0, 375.0)))
        generator.addGenerator(Iron("GREEN_IRON_1", Pos(-190.0, 46.0, 547.0)))
    }

    override val gameStateMachine: BaseStateMachine = BaseStateMachine(this)

    override fun onGameInCountdown() {

    }

    override fun assignTeam() {
        gamePlayers.values.filter {
            it.team == null
        }.forEach { player ->
            player.team = teams.first {
                it.players.size < maxPlayers
            }
        }
    }

    override fun assignItems() {

    }

    override val generator: Generator = SimpleGenerator(this)

    override fun onGameSecondCount(time: Int) {
    }

    override fun onGameStart() {
        super.onGameStart()
        gamePlayers.values.forEach {
            val player = it.miniPlayer.player ?: return@forEach
            lobbySidebar.removeViewer(player)
            player.inventory.clear()
            player.gameMode = GameMode.SURVIVAL
        }
    }

    override fun onGameWatingSecond(time: Int) {
    }

    override fun joinGame(player: IMiniPlayer): Boolean {
        val minestomPlayer = player.player ?: return false
        val join = super.joinGame(player)
        if (!join) return false
        if (currentState() == MiniGame.GameState.LOBBY) {
            lobbySidebar.addViewer(minestomPlayer)
            minestomPlayer.gameMode = GameMode.ADVENTURE
            player.player?.inventory?.clear()
            player.player?.inventory?.setItemStack(0, ItemStack.builder(Material.BEACON)
                .customName(Component.text("队伍选择器"))
                .setSpecial("selectTeam")
                .build())
            player.player?.inventory?.setItemStack(8, ItemStack.builder(Material.SLIME_BALL)
                .customName(Component.text("离开游戏"))
                .setSpecial("exitTeam")
                .build())
        }
        player.game = this
        if (currentState() == MiniGame.GameState.STARTING) {
            minestomPlayer.gameMode = GameMode.SURVIVAL
        }
        return true
    }

    override fun leaveGame(player: IMiniPlayer) {
        super.leaveGame(player)
        val ms = player.player ?: return
        if (currentState() == MiniGame.GameState.LOBBY) {
            lobbySidebar.removeViewer(ms)
        }
        player.player?.level = 0
    }

    override fun addPlayerResource(player: IBedWarsPlayer, resource: String, count: Number): Boolean {
        if (resource != "level") return false
        val minePlayer = player.miniPlayer.player ?: return false
        minePlayer.level += count.toInt()
        minePlayer.playSound(Sound.sound(SoundEvent.ENTITY_EXPERIENCE_ORB_PICKUP, Sound.Source.PLAYER, 1.0f, 1.0f))
        return true
    }

    override fun getPlayerResource(player: IBedWarsPlayer, resource: String): Int {
        if (resource == "level") {
            return player.miniPlayer.player?.level ?: 0
        }
        return 0
    }


    override fun isGameResource(resourceType: SpawnResourceType): Boolean {
        return resourceType.typeName == "level"
    }

    override fun setPlayerResource(player: IBedWarsPlayer, resource: String, count: Number) {
        if (resource == "level") {
            player.miniPlayer.player?.level = count.toInt()
        }
    }

    override fun removePlayerResource(player: IBedWarsPlayer, resource: String, count: Number): Boolean {
        val minePlayer = player.miniPlayer.player ?: return false
        val countA = count.toInt()
        if (minePlayer.level < countA) {
            return false
        }
        minePlayer.level -= countA
        return true
    }

}