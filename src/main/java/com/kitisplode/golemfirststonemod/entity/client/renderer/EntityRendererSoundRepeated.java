package com.kitisplode.golemfirststonemod.entity.client.renderer;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelSoundRepeated;
import com.kitisplode.golemfirststonemod.entity.entity.effect.EntitySoundRepeated;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererSoundRepeated extends GeoEntityRenderer<EntitySoundRepeated>
{
	public EntityRendererSoundRepeated(EntityRendererProvider.Context renderManager)
	{
		super(renderManager, new EntityModelSoundRepeated());
	}

	@Override
	public ResourceLocation getTextureLocation(EntitySoundRepeated animatable)
	{
		return new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/first/first_stone_2.png");
	}

	@Override
	public void render(EntitySoundRepeated entity, float entityYaw, float partialTick, PoseStack poseStack,
					   MultiBufferSource bufferSource, int packedLight)
	{

	}
}
