package com.kitisplode.golemfirststonemod.entity.client.renderer;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelShieldFirstBrick;
import com.kitisplode.golemfirststonemod.entity.entity.effect.EntityEffectShieldFirstBrick;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererShieldFirstBrick extends GeoEntityRenderer<EntityEffectShieldFirstBrick>
{
	public EntityRendererShieldFirstBrick(EntityRendererFactory.Context renderManager)
	{
		super(renderManager, new EntityModelShieldFirstBrick());
	}

	@Override
	public Identifier getTextureLocation(EntityEffectShieldFirstBrick animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/first_brick_shield.png");
	}

	@Override
	public void render(EntityEffectShieldFirstBrick entity, float entityYaw, float partialTick, MatrixStack poseStack,
					   VertexConsumerProvider bufferSource, int packedLight)
	{
		float fullScale = entity.getScaleH();
		poseStack.scale(fullScale, entity.getScaleY() / 4.0f, fullScale);
		VertexConsumer vc = bufferSource.getBuffer(RenderLayer.getEntityTranslucentEmissive(this.getTextureLocation(entity)));
		super.animatable = entity;
		defaultRender(poseStack, entity, bufferSource, null, vc, entityYaw, partialTick, packedLight);
	}
}
