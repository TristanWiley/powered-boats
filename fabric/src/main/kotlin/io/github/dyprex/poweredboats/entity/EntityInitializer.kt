package io.github.dyprex.poweredboats.entity

import io.github.dyprex.poweredboats.Constants
import net.fabricmc.api.ModInitializer
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier

object EntityInitializer : ModInitializer {
    override fun onInitialize() {
        FurnaceBoatEntity.TYPES.forEach { (type, entityType) ->
            Registry.register(
                Registries.ENTITY_TYPE,
                Identifier.of(Constants.MOD_ID, "furnace_boat_${type.name.lowercase()}"),
                entityType
            )
        }
    }
}
