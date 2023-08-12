package com.kitisplode.golemfirststonemod.entity.client.renderer;

import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelShieldFirstBrick;
import com.kitisplode.golemfirststonemod.entity.entity.effect.AbstractEntityEffectCube;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererShieldFirstBrick extends GeoEntityRenderer<AbstractEntityEffectCube>
{
	public EntityRendererShieldFirstBrick(EntityRendererFactory.Context renderManager)
	{
		super(renderManager, new EntityModelShieldFirstBrick());
	}

	@Override
	public Identifier getTextureLocation(AbstractEntityEffectCube animatable)
	{
		return animatable.getTexture();
	}

	@Override
	public void render(AbstractEntityEffectCube entity, float entityYaw, float partialTick, MatrixStack poseStack,
					   VertexConsumerProvider bufferSource, int packedLight)
	{
		float fullScale = entity.getScaleH();
		poseStack.scale(fullScale, entity.getScaleY(), fullScale);
		VertexConsumer vc = bufferSource.getBuffer(RenderLayer.getEntityTranslucentEmissive(this.getTextureLocation(entity)));
		super.animatable = entity;
		defaultRender(poseStack, entity, bufferSource, null, vc, entityYaw, partialTick, packedLight);
	}
}
