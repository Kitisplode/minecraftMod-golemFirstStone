package com.kitisplode.golemfirststonemod.entity.client.renderer;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelGolemFirstStone;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.EntityGolemFirstStone;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererGolemFirstStone extends GeoEntityRenderer<EntityGolemFirstStone>
{
	public EntityRendererGolemFirstStone(EntityRendererProvider.Context renderManager)
	{
		super(renderManager, new EntityModelGolemFirstStone());
		this.shadowRadius = 1.25f;
	}

	@Override
	public ResourceLocation getTextureLocation(EntityGolemFirstStone animatable)
	{
		return new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/first/first_stone_2.png");
	}

	@Override
	public void render(EntityGolemFirstStone entity, float entityYaw, float partialTick, PoseStack poseStack,
					   MultiBufferSource bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}
}
