package com.kitisplode.golemfirststonemod.entity.client;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemFirstBrick;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererGolemFirstBrick extends GeoEntityRenderer<EntityGolemFirstBrick>
{
	public EntityRendererGolemFirstBrick(EntityRendererProvider.Context renderManager)
	{
		super(renderManager, new EntityModelGolemFirstBrick());
	}

	@Override
	public ResourceLocation getTextureLocation(EntityGolemFirstBrick animatable)
	{
		return new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/first_brick.png");
	}

	@Override
	public void render(EntityGolemFirstBrick entity, float entityYaw, float partialTick, PoseStack poseStack,
					   MultiBufferSource bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}
}
