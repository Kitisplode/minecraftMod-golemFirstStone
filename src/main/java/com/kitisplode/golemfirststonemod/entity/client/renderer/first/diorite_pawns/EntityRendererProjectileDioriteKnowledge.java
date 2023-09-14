package com.kitisplode.golemfirststonemod.entity.client.renderer.first.diorite_pawns;

import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelEffectCube;
import com.kitisplode.golemfirststonemod.entity.client.model.first.diorite_pawns.EntityModelProjectileDioriteKnowledge;
import com.kitisplode.golemfirststonemod.entity.entity.effect.AbstractEntityEffectCube;
import com.kitisplode.golemfirststonemod.entity.entity.projectile.EntityProjectileDioriteKnowledge;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererProjectileDioriteKnowledge extends GeoEntityRenderer<EntityProjectileDioriteKnowledge>
{
	private float currentScale = 1.0f;

	public EntityRendererProjectileDioriteKnowledge(EntityRendererProvider.Context renderManager)
	{
		super(renderManager, new EntityModelProjectileDioriteKnowledge());
	}

	@Override
	public ResourceLocation getTextureLocation(EntityProjectileDioriteKnowledge animatable)
	{
		return animatable.getTexture();
	}

	@Override
	public void render(EntityProjectileDioriteKnowledge entity, float entityYaw, float partialTick, PoseStack poseStack,
					   MultiBufferSource bufferSource, int packedLight)
	{
		poseStack.scale(0.25f, 0.25f, 0.25f);
		VertexConsumer vc = bufferSource.getBuffer(RenderType.entityTranslucentEmissive(this.getTextureLocation(entity)));
		super.animatable = entity;
		defaultRender(poseStack, entity, bufferSource, null, vc, entityYaw, partialTick, packedLight);
	}
}
