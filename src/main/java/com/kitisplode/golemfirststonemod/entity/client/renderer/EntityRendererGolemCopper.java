package com.kitisplode.golemfirststonemod.entity.client.renderer;

import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelGolemCopper;
import com.kitisplode.golemfirststonemod.entity.entity.golem.vote.EntityGolemCopper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererGolemCopper extends GeoEntityRenderer<EntityGolemCopper>
{
	public EntityRendererGolemCopper(EntityRendererProvider.Context renderManager)
	{
		super(renderManager, new EntityModelGolemCopper());
		this.shadowRadius = 0.4f;
	}

	@Override
	public ResourceLocation getTextureLocation(EntityGolemCopper animatable)
	{
		return animatable.getTexture();
	}

	@Override
	public void render(EntityGolemCopper entity, float entityYaw, float partialTick, PoseStack poseStack,
					   MultiBufferSource bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}
}
