package io.github.dyprex.poweredboats.render.entity

import io.github.dyprex.poweredboats.entity.FurnaceBoatEntity
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry

object EntityRenderClientInitializer : ClientModInitializer {

    override fun onInitializeClient() {
        EntityRendererRegistry.register(FurnaceBoatEntity.TYPE) { context ->
            FurnaceBoatEntityRenderer(context)
        }
    }
}
