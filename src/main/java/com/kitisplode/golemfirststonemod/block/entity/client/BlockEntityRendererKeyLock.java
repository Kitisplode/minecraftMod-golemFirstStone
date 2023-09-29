package com.kitisplode.golemfirststonemod.block.entity.client;

import com.kitisplode.golemfirststonemod.block.entity.BlockEntityKeyLock;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
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
        super.defaultRender(poseStack, animatable, bufferSource, renderType, buffer, yaw, partialTick, animatable.getLightForKey());
    }
}
