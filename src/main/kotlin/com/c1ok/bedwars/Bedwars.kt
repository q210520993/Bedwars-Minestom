package com.c1ok.bedwars

import com.c1ok.bedwars.internal.bedwars.exp.ExpBedWars
import com.c1ok.bedwars.internal.command.TeamStart
import com.c1ok.bedwars.internal.command.Test
import com.c1ok.bedwars.internal.inventory.SimpleInventory
import com.c1ok.bedwars.internal.listeners.Handlers
import com.c1ok.bedwars.internal.listeners.registerNode
import com.c1ok.bedwars.internal.lobby.set
import com.c1ok.bedwars.internal.manager.BedWarsManager
import com.redstone.beacon.api.plugin.Plugin
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.adventure.audience.Audiences
import net.minestom.server.event.server.ServerTickMonitorEvent
import net.minestom.server.instance.Instance
import net.minestom.server.monitoring.TickMonitor
import net.minestom.server.utils.MathUtils
import net.minestom.server.utils.time.TimeUnit
import java.util.concurrent.atomic.AtomicReference

object Bedwars: Plugin() {

    override fun onEnable() {
        registerNode()
        set()
        Handlers.register()
        MinecraftServer.getCommandManager().register(Test)
        MinecraftServer.getCommandManager().register(TeamStart)
        val bedwars = ExpBedWars()
        bedwars.init()
        BedWarsManager.addGameToManager(bedwars)
        runBenchMarkSend()
        SimpleInventory.regsiter()
        MinecraftServer.getTeamManager().createTeam("test", Component.text("绿队"), NamedTextColor.GREEN, Component.text(""))
    }

    private val LAST_TICK = AtomicReference<TickMonitor>()

    fun runBenchMarkSend() {
        MinecraftServer.getGlobalEventHandler().addListener(
            ServerTickMonitorEvent::class.java
        ) { event: ServerTickMonitorEvent ->
            LAST_TICK.set(
                event.tickMonitor
            )
        }
        MinecraftServer.getSchedulerManager().buildTask {
            if (LAST_TICK.get() == null || MinecraftServer.getConnectionManager()
                    .onlinePlayerCount == 0
            ) return@buildTask
            var ramUsage: Long = MinecraftServer.getBenchmarkManager().usedMemory
            ramUsage = (ramUsage / 1e6).toLong() // bytes to MB

            val tickMonitor: TickMonitor = LAST_TICK.get()
            val header: Component =
                Component.text("RAM USAGE: $ramUsage MB")
                    .append(Component.newline())
                    .append(
                        Component.text(
                            "TICK TIME: " + MathUtils.round(
                                tickMonitor.tickTime,
                                2
                            ) + "ms"
                        )
                    )
                    .append(Component.newline())
                    .append(
                        Component.text(
                            "ACQ TIME: " + MathUtils.round(
                                tickMonitor.acquisitionTime,
                                2
                            ) + "ms"
                        )
                    )
            val footer: Component = MinecraftServer.getBenchmarkManager().cpuMonitoringMessage
            Audiences.players()
                .sendPlayerListHeaderAndFooter(header, footer)
        }.repeat(10, TimeUnit.SERVER_TICK).schedule()
    }
    lateinit var lobby: Instance

}