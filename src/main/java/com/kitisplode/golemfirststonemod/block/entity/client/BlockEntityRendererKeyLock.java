package com.kitisplode.golemfirststonemod.block.entity.client;

import com.kitisplode.golemfirststonemod.block.entity.BlockEntityKeyLock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.Level;
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
        super.defaultRender(poseStack, animatable, bufferSource, renderType, buffer, yaw, partialTick, i);
    }
}
