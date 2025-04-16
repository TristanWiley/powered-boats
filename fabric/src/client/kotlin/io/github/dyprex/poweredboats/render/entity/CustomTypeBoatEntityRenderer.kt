package io.github.dyprex.poweredboats.render.entity

import io.github.dyprex.poweredboats.SupportedBoatType
import io.github.dyprex.poweredboats.modelLayer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.model.Model
import net.minecraft.client.model.Model.SinglePartModel
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.AbstractBoatEntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.model.BoatEntityModel
import net.minecraft.client.render.entity.model.EntityModel
import net.minecraft.client.render.entity.model.EntityModelLayers
import net.minecraft.client.render.entity.state.BoatEntityRenderState
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.Identifier


@Environment(EnvType.CLIENT)
open class CustomTypeBoatEntityRenderer(
    private val ctx: EntityRendererFactory.Context
) : AbstractBoatEntityRenderer(ctx) {

    private val waterMaskModel: Model = SinglePartModel(
        ctx.getPart(EntityModelLayers.BOAT)
    ) { _: Identifier? -> RenderLayer.getWaterMask() }

    private var boatType: SupportedBoatType? = null
    private var model: EntityModel<BoatEntityRenderState>? = null
    private var texture: Identifier? = null

    fun setBoatType(boatType: SupportedBoatType) {
        this.boatType = boatType

        // Rebuild model and texture based on new boat type
        val modelLayer = boatType.modelLayer
        this.model = BoatEntityModel(ctx.getPart(modelLayer))

        // Build the texture path from the modelLayer ID
        this.texture = modelLayer.id().withPath { path ->
            "textures/entity/$path.png"
        }
    }

    override fun getModel(): EntityModel<BoatEntityRenderState> {
        return model ?: throw IllegalStateException("Boat type not set!")
    }

    override fun getRenderLayer(): RenderLayer {
        return model?.getLayer(texture) ?: throw IllegalStateException("Boat type not set!")
    }

    override fun renderWaterMask(
        state: BoatEntityRenderState,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int
    ) {
        if (!state.submergedInWater) {
            waterMaskModel.render(
                matrices,
                vertexConsumers.getBuffer(waterMaskModel.getLayer(texture)),
                light,
                OverlayTexture.DEFAULT_UV
            )
        }
    }
}
