package com.c1ok.bedwars.internal.core.bedwars

import com.c1ok.bedwars.api.game.MiniGame
import com.c1ok.bedwars.api.game.bedwars.genertors.Generator
import com.c1ok.bedwars.api.game.bedwars.genertors.ResourceEntity
import com.c1ok.bedwars.api.game.bedwars.genertors.SpawnResourceType
import com.c1ok.bedwars.utils.SchedulerBuilder
import net.minestom.server.utils.validate.Check
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

open class SimpleGenerator(val game: BaseBedWarsGame): Generator {
    val resources = ConcurrentHashMap<String, SpawnResourceType>()
    private var currentTime: AtomicInteger = AtomicInteger(0)
    var started = false
    var forceStop = false

    override fun start() {
        if (started) return
        forceStop = false
        SchedulerBuilder(game.gameInstance.scheduler(), Runnable {
            currentTime.addAndGet(1)
            val currentTimeUnwarp = currentTime.get()
            resources.filter {
                currentTimeUnwarp % it.value.spawnInterval  == 0
            }.forEach {
                val resourceEntity = ResourceEntity(it.value)
                resourceEntity.setInstance(game.gameInstance, it.value.point)
            }
        }).condition {
            game.currentState() == MiniGame.GameState.STARTING && !forceStop
        }.repeat(java.time.Duration.ofSeconds(1)).conditionFalseTask {
            started = false
        }.conditionFalseTask {
            currentTime.set(0)
        }.schedule()
    }

    override fun addGenerator(resourceType: SpawnResourceType) {
        resources[resourceType.id] = resourceType
    }

    override fun resetGenerator(resourceType: SpawnResourceType) {
        Check.isTrue(resources.containsKey(resourceType.id), "你无法重设该generator，因为它不存在")
        resources.remove(resourceType.id)
        addGenerator(resourceType)
    }

    override fun removeGenerator(resourceType: SpawnResourceType) {
        resources.remove(resourceType.id)
    }

    override fun removeGenerator(id: String) {
        resources.remove(id)
    }

    override fun getGenerator(id: String): SpawnResourceType? {
        return resources[id]
    }

    override fun close() {
        started = false
        forceStop = true
    }

}
