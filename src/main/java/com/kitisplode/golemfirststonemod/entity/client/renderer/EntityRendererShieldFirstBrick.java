package com.kitisplode.golemfirststonemod.entity.client.renderer;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelShieldFirstBrick;
import com.kitisplode.golemfirststonemod.entity.entity.effect.EntityEffectShieldFirstBrick;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererShieldFirstBrick extends GeoEntityRenderer<EntityEffectShieldFirstBrick>
{
	private float currentScale = 1.0f;

	public EntityRendererShieldFirstBrick(EntityRendererProvider.Context renderManager)
	{
		super(renderManager, new EntityModelShieldFirstBrick());
	}

	@Override
	public ResourceLocation getTextureLocation(EntityEffectShieldFirstBrick animatable)
	{
		return new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/first_brick_shield.png");
	}

	@Override
	public void render(EntityEffectShieldFirstBrick entity, float entityYaw, float partialTick, PoseStack poseStack,
					   MultiBufferSource bufferSource, int packedLight)
	{
		float fullScale = entity.getScaleH();
		poseStack.scale(fullScale, entity.getScaleY() / 4.0f, fullScale);
		VertexConsumer vc = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(this.getTextureLocation(entity)));
		super.animatable = entity;
		defaultRender(poseStack, entity, bufferSource, null, vc, entityYaw, partialTick, packedLight);
	}
}
