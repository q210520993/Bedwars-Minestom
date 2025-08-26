package com.c1ok.bedwars.internal.command

import com.c1ok.bedwars.internal.core.bedwars.BaseBedWarsGame
import com.c1ok.bedwars.internal.manager.PlayerManagerImpl
import net.minestom.server.command.builder.Command
import net.minestom.server.entity.Player

object TeamStart: Command("TestStart") {
    init {
        addSyntax({ sender, context ->
            val player = sender as Player
            val miniPlayer = PlayerManagerImpl.getPlayer(player.uuid)
            val game = (miniPlayer?.game as? BaseBedWarsGame) ?: return@addSyntax
            game.gameStateMachine.forceStarted = true
        })
    }
}