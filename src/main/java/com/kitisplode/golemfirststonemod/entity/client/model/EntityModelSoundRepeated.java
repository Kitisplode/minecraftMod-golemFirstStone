package com.kitisplode.golemfirststonemod.entity.client.model;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.effect.EntitySoundRepeated;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EntityModelSoundRepeated extends GeoModel<EntitySoundRepeated>
{
	@Override
	public ResourceLocation getModelResource(EntitySoundRepeated animatable)
	{
		return new ResourceLocation(GolemFirstStoneMod.MOD_ID, "geo/first_brick_shield.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(EntitySoundRepeated animatable)
	{
		return new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/first/first_stone_2.png");
	}

	@Override
	public ResourceLocation getAnimationResource(EntitySoundRepeated animatable)
	{
		return new ResourceLocation(GolemFirstStoneMod.MOD_ID, "animations/first_stone_2.animation.json");
	}
}
