package com.kitisplode.golemfirststonemod.entity.client.renderer;

import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelEffectCube;
import com.kitisplode.golemfirststonemod.entity.entity.effect.AbstractEntityEffectCube;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererEffectCube extends GeoEntityRenderer<AbstractEntityEffectCube>
{
	private float currentScale = 1.0f;

	public EntityRendererEffectCube(EntityRendererProvider.Context renderManager)
	{
		super(renderManager, new EntityModelEffectCube());
	}

	@Override
	public ResourceLocation getTextureLocation(AbstractEntityEffectCube animatable)
	{
		return animatable.getTexture();
	}

	@Override
	public void render(AbstractEntityEffectCube entity, float entityYaw, float partialTick, PoseStack poseStack,
					   MultiBufferSource bufferSource, int packedLight)
	{
		float fullScale = entity.getScaleH();
		poseStack.scale(fullScale, entity.getScaleY(), fullScale);
		VertexConsumer vc = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(this.getTextureLocation(entity)));
		super.animatable = entity;
		defaultRender(poseStack, entity, bufferSource, null, vc, entityYaw, partialTick, packedLight);
	}
}
