package com.kitisplode.golemfirststonemod.entity.client.model;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.effect.AbstractEntityEffectCube;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EntityModelEffectCube extends GeoModel<AbstractEntityEffectCube>
{
	@Override
	public ResourceLocation getModelResource(AbstractEntityEffectCube animatable)
	{
		return new ResourceLocation(GolemFirstStoneMod.MOD_ID, "geo/first_brick_shield.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(AbstractEntityEffectCube animatable)
	{
		return animatable.getTexture();
	}

	@Override
	public ResourceLocation getAnimationResource(AbstractEntityEffectCube animatable)
	{
		return null;
	}
}
