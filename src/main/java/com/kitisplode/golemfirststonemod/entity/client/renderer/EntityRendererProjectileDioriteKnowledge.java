package com.kitisplode.golemfirststonemod.entity.client.renderer;

import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelProjectileDioriteKnowledge;
import com.kitisplode.golemfirststonemod.entity.entity.projectile.EntityProjectileDioriteKnowledge;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererProjectileDioriteKnowledge extends GeoEntityRenderer<EntityProjectileDioriteKnowledge>
{
	public EntityRendererProjectileDioriteKnowledge(EntityRendererFactory.Context renderManager)
	{
		super(renderManager, new EntityModelProjectileDioriteKnowledge());
	}

	@Override
	public Identifier getTextureLocation(EntityProjectileDioriteKnowledge animatable)
	{
		return animatable.getTexture();
	}

	@Override
	public void render(EntityProjectileDioriteKnowledge entity, float entityYaw, float partialTick, MatrixStack poseStack,
					   VertexConsumerProvider bufferSource, int packedLight)
	{
		poseStack.scale(0.25f, 0.25f, 0.25f);
		VertexConsumer vc = bufferSource.getBuffer(RenderLayer.getEyes(this.getTextureLocation(entity)));
		super.animatable = entity;
		defaultRender(poseStack, entity, bufferSource, null, vc, entityYaw, partialTick, packedLight);
	}
}
