package io.github.dyprex.poweredboats.item

import io.github.dyprex.poweredboats.Constants
import io.github.dyprex.poweredboats.SupportedBoatType
import io.github.dyprex.poweredboats.entity.FurnaceBoatEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.registry.RegistryKey
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.world.World
import java.util.function.Supplier

class FurnaceBoatItem(
    private val boatItemSupplier: Supplier<Item>,
    key: RegistryKey<Item>
) : Item(Settings().maxCount(1).registryKey(key)) {

    override fun use(world: World?, user: PlayerEntity?, hand: Hand?): ActionResult {
        val player = user ?: return ActionResult.PASS
        val worldObj = world ?: return ActionResult.PASS
        val stack = player.getStackInHand(hand)
        val position = player.raycast(2.5, 1.0F, true).pos

        if (!worldObj.isClient) {
            val type = FurnaceBoatEntity.TYPES[SupportedBoatType.fromItem(boatItemSupplier.get())]

            val boat = FurnaceBoatEntity(type!!, worldObj, boatItemSupplier).apply {
                setPosition(position)
                yaw = player.yaw
            }

            if (worldObj is ServerWorld) {
                EntityType.copier<Entity>(worldObj, stack, player).accept(boat)
                worldObj.spawnEntity(boat)
                if (!player.isCreative) {
                    stack.decrement(1)
                }
            }
        }

        return ActionResult.SUCCESS
    }

    companion object {
        fun identifierForBoatItem(boatItem: Item): Identifier {
            val id = SupportedBoatType.fromItem(boatItem).name.lowercase()
            return Identifier.of(Constants.MOD_ID, "${id}_furnace_boat")
        }
    }
}
