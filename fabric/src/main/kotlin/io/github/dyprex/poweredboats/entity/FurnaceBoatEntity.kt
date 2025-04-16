package io.github.dyprex.poweredboats.entity

import io.github.dyprex.poweredboats.Constants
import io.github.dyprex.poweredboats.SupportedBoatType
import io.github.dyprex.poweredboats.config.ConfigInitializer
import net.minecraft.block.Blocks
import net.minecraft.entity.EntityType
import net.minecraft.entity.SpawnGroup
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.vehicle.BoatEntity
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.nbt.NbtCompound
import net.minecraft.particle.ParticleTypes
import net.minecraft.recipe.Ingredient
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import java.util.function.Supplier

class FurnaceBoatEntity(entityType: EntityType<FurnaceBoatEntity>, world: World, supplier: Supplier<Item>) :
    BoatEntity(entityType, world, supplier) {

    private var fuelTicksLeft = 0
    val isLit: Boolean
        get() = dataTracker.get(LIT)
    val boatTypeItem = supplier.get()

    override fun initDataTracker(builder: DataTracker.Builder) {
        super.initDataTracker(builder)
        builder.add(LIT, false)
    }

    override fun tick() {
        super.tick()
        if (!world.isClient && fuelTicksLeft > 0) {
            if (isAccelerating) {
                --fuelTicksLeft
            }
            setLit(fuelTicksLeft > 0)
        }

        if (isLit && random.nextInt(10) == 0) {
            val direction = Vec3d.fromPolar(pitch, yaw).negate().multiply(0.5)
            world.addParticle(
                ParticleTypes.LARGE_SMOKE,
                x + direction.x,
                y + 0.8,
                z + direction.z,
                0.0,
                0.0,
                0.0,
            )
        }
    }

    override fun interact(player: PlayerEntity, hand: Hand): ActionResult {
        val itemStack = player.getStackInHand(Hand.MAIN_HAND)
        return if (ACCEPTABLE_FUEL.test(itemStack)) {
            if (!isLit) {
                if (!world.isClient) {
                    fuelTicksLeft = 6000 // = 5 minutes
                }
                itemStack.decrementUnlessCreative(1, player)
                ActionResult.SUCCESS
            } else ActionResult.FAIL
        } else super.interact(player, hand)
    }

    override fun getVelocityMultiplier(): Float {
        return if (isLit && isAccelerating && world.getBlockState(blockPos).isOf(Blocks.WATER)) {
            1.0f + (ConfigInitializer.activeConfig.boatSpeed / 100)
        } else super.getVelocityMultiplier()
    }

    override fun getPassengerHorizontalOffset(): Float {
        return 0.15F
    }

    override fun getMaxPassengers(): Int {
        return 1
    }

    private fun setLit(lit: Boolean) {
        dataTracker.set(LIT, lit)
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)
        nbt.putInt(FUEL_NBT_TAG, fuelTicksLeft)
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)
        fuelTicksLeft = nbt.getInt(FUEL_NBT_TAG)
    }

    private val isAccelerating
        get() = hasPassengers() && isPaddleMoving(0) && isPaddleMoving(1)

    companion object {
        private const val ID = "furnace_boat"
        private const val FUEL_NBT_TAG = "FuelTicksLeft"

        val TYPES: Map<SupportedBoatType, EntityType<FurnaceBoatEntity>> =
            SupportedBoatType.entries.associateWith { boatType ->
                EntityType.Builder.create(
                    { type, world -> FurnaceBoatEntity(type, world) { boatType.item } },
                    SpawnGroup.MISC
                )
                    .dimensions(1.375F, 0.5625F)
                    .eyeHeight(0.5625F)
                    .maxTrackingRange(10)
                    .build(
                        RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(Constants.MOD_ID, "furnace_boat_${boatType.name.lowercase()}"))
                    )
            }
        private val ACCEPTABLE_FUEL = Ingredient.ofItems(*arrayOf(Items.COAL, Items.CHARCOAL))

        private val LIT: TrackedData<Boolean> =
            DataTracker.registerData(
                FurnaceBoatEntity::class.java,
                TrackedDataHandlerRegistry.BOOLEAN,
            )
    }
}
