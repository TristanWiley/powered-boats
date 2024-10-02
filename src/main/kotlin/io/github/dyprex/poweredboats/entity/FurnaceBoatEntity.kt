package io.github.dyprex.poweredboats.entity

import io.github.dyprex.poweredboats.Constants
import io.github.dyprex.poweredboats.PoweredBoatsInitializer
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
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World

class FurnaceBoatEntity(entityType: EntityType<FurnaceBoatEntity>, world: World) :
    BoatEntity(entityType, world) {

    private var fuelTicksLeft = 0
    val isLit: Boolean
        get() = dataTracker.get(LIT)

    constructor(world: World) : this(PoweredBoatsInitializer.furnaceBoatEntityType, world)

    override fun initDataTracker(builder: DataTracker.Builder) {
        super.initDataTracker(builder)
        builder.add(LIT, false)
    }

    override fun tick() {
        super.tick()
        if (!world.isClient && fuelTicksLeft > 0) {
            // TODO: only reduce fuel if moving or mounted
            --fuelTicksLeft
            setLit(fuelTicksLeft > 0)
        }

        // TODO: only add fire/smoke particles if moving or mounted
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
                ActionResult.success(world.isClient)
            } else ActionResult.FAIL
        } else super.interact(player, hand)
    }

    override fun getVelocityMultiplier(): Float {
        return if (isLit && world.getBlockState(blockPos).isOf(Blocks.WATER)) {
            1.04F
        } else super.getVelocityMultiplier()
    }

    override fun getPassengerHorizontalOffset(): Float {
        return 0.15F
    }

    override fun getMaxPassengers(): Int {
        return 1
    }

    override fun asItem(): Item {
        return PoweredBoatsInitializer.furnaceBoatItems.first { it.type == variant }
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

    companion object {
        private const val ID = "furnace_boat"
        private const val FUEL_NBT_TAG = "FuelTicksLeft"

        val IDENTIFIER: Identifier = Identifier.of(Constants.MOD_ID, ID)
        val TYPE: EntityType<FurnaceBoatEntity> =
            EntityType.Builder.create(::FurnaceBoatEntity, SpawnGroup.MISC)
                // see net.minecraft.entity.EntityType.BOAT
                .dimensions(1.375F, 0.5625F)
                .eyeHeight(0.5625F)
                .maxTrackingRange(10)
                .build(ID)
        private val ACCEPTABLE_FUEL = Ingredient.ofItems(*arrayOf(Items.COAL, Items.CHARCOAL))

        private val LIT: TrackedData<Boolean> =
            DataTracker.registerData(
                FurnaceBoatEntity::class.java,
                TrackedDataHandlerRegistry.BOOLEAN,
            )
    }
}
