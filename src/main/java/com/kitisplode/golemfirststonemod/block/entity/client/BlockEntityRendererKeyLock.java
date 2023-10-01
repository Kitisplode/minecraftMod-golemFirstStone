package com.kitisplode.golemfirststonemod.block.entity.client;

import com.kitisplode.golemfirststonemod.block.entity.BlockEntityKeyLock;
import com.kitisplode.golemfirststonemod.util.ExtraMath;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import org.joml.Matrix4f;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import javax.annotation.Nullable;

public class BlockEntityRendererKeyLock extends GeoBlockRenderer<BlockEntityKeyLock>
{
    public BlockEntityRendererKeyLock()
    {
        super(new BlockEntityModelKeyLock());
    }

    public void defaultRender(PoseStack poseStack, BlockEntityKeyLock animatable, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer,
                       float yaw, float partialTick, int packedLight)
    {
        Level level = animatable.getLevel();
        int i;
        if (level != null) {
            i = LevelRenderer.getLightColor(level, animatable.getKeyPosition());
        } else {
            i = 15728880;
        }
        if (!animatable.getItem(1).isEmpty())
        {
            this.renderNameTag(animatable, animatable.getItem(1).getHoverName(), poseStack, bufferSource, packedLight);
        }
        super.defaultRender(poseStack, animatable, bufferSource, renderType, buffer, yaw, partialTick, i);
    }

    protected void renderNameTag(BlockEntityKeyLock pEntity, Component pDisplayName, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
        double d0 = Minecraft.getInstance().player.position().distanceToSqr(pEntity.getBackPosition().getCenter());
        if (d0 < Mth.square(0.75)) {
            boolean flag = true;//!pEntity.isDiscrete();
            float f = 0.5f;
            int i = "deadmau5".equals(pDisplayName.getString()) ? -10 : 0;
            pMatrixStack.pushPose();
            pMatrixStack.translate(+0.5F, f, +0.5F);
            pMatrixStack.mulPose(Axis.YP.rotation(-(float) ExtraMath.getYawBetweenPoints(pEntity.getBlockPos().getCenter(), Minecraft.getInstance().player.position()) + (float)(Math.PI)));
            pMatrixStack.scale(-0.025F, -0.025F, 0.025F);
            Matrix4f matrix4f = pMatrixStack.last().pose();
            float f1 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
            int j = (int)(f1 * 255.0F) << 24;
            Font font = Minecraft.getInstance().font;
            float f2 = (float)(-font.width(pDisplayName) / 2);
            font.drawInBatch(pDisplayName, f2, (float)i, 553648127, false, matrix4f, pBuffer, flag ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL, j, pPackedLight);
//            if (flag) {
//                font.drawInBatch(pDisplayName, f2, (float)i, -1, false, matrix4f, pBuffer, Font.DisplayMode.NORMAL, 0, pPackedLight);
//            }

            pMatrixStack.popPose();
        }
    }
}
