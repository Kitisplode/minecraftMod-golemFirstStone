package com.kitisplode.golemfirststonemod.entity.client.renderer;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelGolemFirstOak;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemFirstOak;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererGolemFirstOak extends GeoEntityRenderer<EntityGolemFirstOak>
{
	public EntityRendererGolemFirstOak(EntityRendererProvider.Context renderManager)
	{
		super(renderManager, new EntityModelGolemFirstOak());
		this.shadowRadius = 1.25f;
	}

	@Override
	public ResourceLocation getTextureLocation(EntityGolemFirstOak animatable)
	{
		return new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/first/first_oak.png");
	}

	@Override
	public void render(EntityGolemFirstOak entity, float entityYaw, float partialTick, PoseStack poseStack,
					   MultiBufferSource bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}
}
