package io.github.dyprex.poweredboats

import io.github.dyprex.poweredboats.entity.FurnaceBoatEntity
import io.github.dyprex.poweredboats.render.entity.FurnaceBoatEntityRenderer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry

object PoweredBoatsClientInitializer : ClientModInitializer {

    override fun onInitializeClient() {
        EntityRendererRegistry.register(FurnaceBoatEntity.TYPE) { context ->
            FurnaceBoatEntityRenderer(context)
        }
    }
}
