package io.github.dyprex.poweredboats.entity

import net.fabricmc.api.ModInitializer
import net.minecraft.entity.EntityType
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry

object EntityInitializer : ModInitializer {

    val furnaceBoatEntityType: EntityType<FurnaceBoatEntity> =
        Registry.register(
            Registries.ENTITY_TYPE,
            FurnaceBoatEntity.IDENTIFIER,
            FurnaceBoatEntity.TYPE,
        )

    override fun onInitialize() {}
}
