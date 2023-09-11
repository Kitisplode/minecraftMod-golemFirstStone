package com.kitisplode.golemfirststonemod.entity.client.renderer.legends;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.client.model.legends.EntityModelGolemPlank;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemPlank;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererGolemPlank extends GeoEntityRenderer<EntityGolemPlank>
{
	public EntityRendererGolemPlank(EntityRendererProvider.Context renderManager)
	{
		super(renderManager, new EntityModelGolemPlank());
		this.shadowRadius = 0.8f;
	}

	@Override
	public ResourceLocation getTextureLocation(EntityGolemPlank animatable)
	{
		return new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/golem_plank.png");
	}

	@Override
	public void render(EntityGolemPlank entity, float entityYaw, float partialTick, PoseStack poseStack,
					   MultiBufferSource bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}
}
