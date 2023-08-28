package com.kitisplode.golemfirststonemod.entity.client.renderer;

import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelPawn;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityPawn;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererPawn extends GeoEntityRenderer<EntityPawn>
{
	public EntityRendererPawn(EntityRendererProvider.Context renderManager)
	{
		super(renderManager, new EntityModelPawn());
		this.shadowRadius = 0.4f;
	}

	@Override
	public ResourceLocation getTextureLocation(EntityPawn animatable)
	{
		return animatable.getTextureLocation();
	}

	@Override
	public void render(EntityPawn entity, float entityYaw, float partialTick, PoseStack poseStack,
					   MultiBufferSource bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}
}
