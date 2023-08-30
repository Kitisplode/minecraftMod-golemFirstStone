package com.kitisplode.golemfirststonemod.entity.client.renderer;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelGolemCobble;
import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelGolemMossy;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemCobble;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemMossy;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererGolemMossy extends GeoEntityRenderer<EntityGolemMossy>
{
	public EntityRendererGolemMossy(EntityRendererProvider.Context renderManager)
	{
		super(renderManager, new EntityModelGolemMossy());
		this.shadowRadius = 0.8f;

	}

	@Override
	public ResourceLocation getTextureLocation(EntityGolemMossy animatable)
	{
		return new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/golem_mossy.png");
	}

	@Override
	public void render(EntityGolemMossy entity, float entityYaw, float partialTick, PoseStack poseStack,
					   MultiBufferSource bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}
}
