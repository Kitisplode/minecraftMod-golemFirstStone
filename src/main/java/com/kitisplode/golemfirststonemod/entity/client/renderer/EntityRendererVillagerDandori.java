package com.kitisplode.golemfirststonemod.entity.client.renderer;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelVillagerDandori;
import com.kitisplode.golemfirststonemod.entity.entity.EntityVillagerDandori;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererVillagerDandori extends GeoEntityRenderer<EntityVillagerDandori>
{
	public EntityRendererVillagerDandori(EntityRendererProvider.Context renderManager)
	{
		super(renderManager, new EntityModelVillagerDandori());
		this.shadowRadius = 0.4f;
	}

	@Override
	public ResourceLocation getTextureLocation(EntityVillagerDandori animatable)
	{
		return new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/villager_dandori.png");
	}

	@Override
	public void render(EntityVillagerDandori entity, float entityYaw, float partialTick, PoseStack poseStack,
					   MultiBufferSource bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}
}
