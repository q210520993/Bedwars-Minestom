package com.c1ok.bedwars.api.game.bedwars.genertors

import net.minestom.server.entity.ItemEntity
import net.minestom.server.item.ItemStack

open class ResourceEntity(val resourceType: SpawnResourceType): ItemEntity(ItemStack.of(resourceType.material))