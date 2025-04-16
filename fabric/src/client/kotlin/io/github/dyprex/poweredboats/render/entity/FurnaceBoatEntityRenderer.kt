package io.github.dyprex.poweredboats.render.entity

import io.github.dyprex.poweredboats.SupportedBoatType
import io.github.dyprex.poweredboats.entity.FurnaceBoatEntity
import net.minecraft.block.Blocks
import net.minecraft.block.FurnaceBlock
import net.minecraft.client.render.Frustum
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.state.BoatEntityRenderState
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.vehicle.AbstractBoatEntity
import net.minecraft.item.Items
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RotationAxis
import org.joml.Quaternionf

// uses BlockRenderManager to render furnace on top of vanilla boat
// models/textures as opposed to using custom models/textures.
// inspired by net.minecraft.client.render.entity.MinecartEntityRenderer
class FurnaceBoatEntityRenderer(ctx: EntityRendererFactory.Context) :
    CustomTypeBoatEntityRenderer(ctx) {
    private val blockRenderManager = ctx.blockRenderManager
    private var entity: FurnaceBoatEntity? = null

    override fun shouldRender(
        entity: AbstractBoatEntity?,
        frustum: Frustum?,
        x: Double,
        y: Double,
        z: Double,
    ): Boolean {
        this.entity = entity as FurnaceBoatEntity
        setBoatType(SupportedBoatType.fromItem(this.entity?.boatTypeItem ?: Items.OAK_BOAT))
        return super.shouldRender(entity, frustum, x, y, z)
    }

    override fun render(
        boatEntityRenderState: BoatEntityRenderState?,
        matrixStack: MatrixStack?,
        vertexConsumerProvider: VertexConsumerProvider?,
        light: Int,
    ) {
        super.render(boatEntityRenderState, matrixStack, vertexConsumerProvider, light)

        if (matrixStack == null || boatEntityRenderState == null || vertexConsumerProvider == null)
            return
        renderBlock(boatEntityRenderState, matrixStack, vertexConsumerProvider, light)
    }

    private fun renderBlock(
        boatEntityRenderState: BoatEntityRenderState,
        matrixStack: MatrixStack,
        vertexConsumerProvider: VertexConsumerProvider,
        light: Int
    ) {
        matrixStack.push()
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - boatEntityRenderState.yaw))
        val h = boatEntityRenderState.damageWobbleTicks
        var j = boatEntityRenderState.damageWobbleStrength
        if (j < 0.0f) {
            j = 0.0f
        }
        if (h > 0.0f) {
            val deg = MathHelper.sin(h) * h * j / 10.0f * boatEntityRenderState.damageWobbleSide.toFloat()
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(deg))
        }
        val k = boatEntityRenderState.bubbleWobble
        if (!MathHelper.approximatelyEquals(k, 0.0f)) {
            val angle = boatEntityRenderState.bubbleWobble * (Math.PI / 180.0).toFloat()
            matrixStack.multiply(Quaternionf().setAngleAxis(angle, 1.0f, 0.0f, 1.0f))
        }

        matrixStack.scale(0.75f, 0.75f, 0.75f)
        matrixStack.translate(-0.5f, 0.3f, 0.17f)
        val isLit = entity?.isLit ?: false
        val furnace =
            Blocks.FURNACE.defaultState
                .with(FurnaceBlock.FACING, Direction.NORTH)
                .with(FurnaceBlock.LIT, isLit) // TODO: Make this determine if it should be lit
        blockRenderManager.renderBlockAsEntity(
            furnace,
            matrixStack,
            vertexConsumerProvider,
            light,
            OverlayTexture.DEFAULT_UV,
        )
        matrixStack.pop()
    }
}
