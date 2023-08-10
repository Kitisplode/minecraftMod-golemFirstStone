package com.kitisplode.golemfirststonemod.entity.client.model;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.effect.EntityEffectShieldFirstBrick;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EntityModelShieldFirstBrick extends GeoModel<EntityEffectShieldFirstBrick>
{
	@Override
	public ResourceLocation getModelResource(EntityEffectShieldFirstBrick animatable)
	{
		return new ResourceLocation(GolemFirstStoneMod.MOD_ID, "geo/first_brick_shield.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(EntityEffectShieldFirstBrick animatable)
	{
		return new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/first_brick_shield.png");
	}

	@Override
	public ResourceLocation getAnimationResource(EntityEffectShieldFirstBrick animatable)
	{
		return null;
	}
}
