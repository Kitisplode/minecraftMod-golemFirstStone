package com.kitisplode.golemfirststonemod.entity.client.renderer;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelPawnFirstDiorite;
import com.kitisplode.golemfirststonemod.entity.entity.golem.pawn.EntityPawnFirstDiorite;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererPawnFirstDiorite extends GeoEntityRenderer<EntityPawnFirstDiorite>
{
	public EntityRendererPawnFirstDiorite(EntityRendererProvider.Context renderManager)
	{
		super(renderManager, new EntityModelPawnFirstDiorite());
		this.shadowRadius = 0.4f;
	}

	@Override
	public ResourceLocation getTextureLocation(EntityPawnFirstDiorite animatable)
	{
		int pawnType = animatable.getPawnType();
		return switch (pawnType)
		{
			case 0 -> new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/diorite_action.png");
			case 1 -> new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/diorite_foresight.png");
			default -> new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/diorite_knowledge.png");
		};
	}

	@Override
	public void render(EntityPawnFirstDiorite entity, float entityYaw, float partialTick, PoseStack poseStack,
					   MultiBufferSource bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}
}
