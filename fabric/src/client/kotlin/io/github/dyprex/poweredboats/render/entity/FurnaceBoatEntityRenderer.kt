package io.github.dyprex.poweredboats.render.entity

import io.github.dyprex.poweredboats.entity.FurnaceBoatEntity
import net.minecraft.block.Blocks
import net.minecraft.block.FurnaceBlock
import net.minecraft.client.render.OverlayTexture
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.entity.BoatEntityRenderer
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.vehicle.BoatEntity
import net.minecraft.util.math.Direction
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RotationAxis
import org.joml.Quaternionf

// uses BlockRenderManager to render furnace on top of vanilla boat
// models/textures as opposed to using custom models/textures.
// inspired by net.minecraft.client.render.entity.MinecartEntityRenderer
class FurnaceBoatEntityRenderer(ctx: EntityRendererFactory.Context) :
    BoatEntityRenderer(ctx, false) {

    private val blockRenderManager = ctx.blockRenderManager

    override fun render(
        boatEntity: BoatEntity,
        yaw: Float,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
    ) {
        // safe because this renderer only renders FurnaceBoatEntity
        val furnaceBoat = boatEntity as FurnaceBoatEntity

        super.render(furnaceBoat, yaw, tickDelta, matrices, vertexConsumers, light)
        renderBlock(matrices, furnaceBoat, yaw, tickDelta, vertexConsumers, light)
    }

    private fun renderBlock(
        matrices: MatrixStack,
        furnaceBoat: FurnaceBoatEntity,
        yaw: Float,
        tickDelta: Float,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
    ) {
        matrices.push()
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - yaw))
        val h = furnaceBoat.damageWobbleTicks.toFloat() - tickDelta
        var j = furnaceBoat.damageWobbleStrength - tickDelta
        if (j < 0.0f) {
            j = 0.0f
        }
        if (h > 0.0f) {
            val deg = MathHelper.sin(h) * h * j / 10.0f * furnaceBoat.damageWobbleSide.toFloat()
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(deg))
        }
        val k = furnaceBoat.interpolateBubbleWobble(tickDelta)
        if (!MathHelper.approximatelyEquals(k, 0.0f)) {
            val angle = furnaceBoat.interpolateBubbleWobble(tickDelta) * (Math.PI / 180.0).toFloat()
            matrices.multiply(Quaternionf().setAngleAxis(angle, 1.0f, 0.0f, 1.0f))
        }

        matrices.scale(0.75f, 0.75f, 0.75f)
        matrices.translate(-0.5f, 0.3f, 0.17f)
        val furnace =
            Blocks.FURNACE.defaultState
                .with(FurnaceBlock.FACING, Direction.NORTH)
                .with(FurnaceBlock.LIT, furnaceBoat.isLit)
        blockRenderManager.renderBlockAsEntity(
            furnace,
            matrices,
            vertexConsumers,
            light,
            OverlayTexture.DEFAULT_UV,
        )
        matrices.pop()
    }
}
