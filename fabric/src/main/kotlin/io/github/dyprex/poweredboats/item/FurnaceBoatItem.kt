package io.github.dyprex.poweredboats.item

import io.github.dyprex.poweredboats.Constants
import io.github.dyprex.poweredboats.entity.FurnaceBoatEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.vehicle.BoatEntity
import net.minecraft.item.BoatItem
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World

class FurnaceBoatItem(val type: BoatEntity.Type) : BoatItem(false, type, Settings().maxCount(1)) {

    override fun createEntity(
        world: World,
        hitResult: HitResult,
        stack: ItemStack,
        player: PlayerEntity,
    ): FurnaceBoatEntity {
        val boatEntity = FurnaceBoatEntity(world).apply { setPosition(hitResult.pos) }
        if (world is ServerWorld) {
            EntityType.copier<Entity>(world, stack, player).accept(boatEntity)
        }
        return boatEntity
    }

    companion object {
        fun identifierForBoatType(type: BoatEntity.Type): Identifier {
            return Identifier.of(Constants.MOD_ID, "${type}_furnace_boat")
        }
    }
}
