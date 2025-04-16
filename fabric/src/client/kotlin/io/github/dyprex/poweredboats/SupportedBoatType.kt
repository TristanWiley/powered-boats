package io.github.dyprex.poweredboats

import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.client.render.entity.model.EntityModelLayers

val SupportedBoatType.modelLayer: EntityModelLayer
    get() = when (this) {
        SupportedBoatType.ACACIA -> EntityModelLayers.ACACIA_BOAT
        SupportedBoatType.BIRCH -> EntityModelLayers.BIRCH_BOAT
        SupportedBoatType.CHERRY -> EntityModelLayers.CHERRY_BOAT
        SupportedBoatType.DARK_OAK -> EntityModelLayers.DARK_OAK_BOAT
        SupportedBoatType.JUNGLE -> EntityModelLayers.JUNGLE_BOAT
        SupportedBoatType.MANGROVE -> EntityModelLayers.MANGROVE_BOAT
        SupportedBoatType.OAK -> EntityModelLayers.OAK_BOAT
        SupportedBoatType.SPRUCE -> EntityModelLayers.SPRUCE_BOAT
    }