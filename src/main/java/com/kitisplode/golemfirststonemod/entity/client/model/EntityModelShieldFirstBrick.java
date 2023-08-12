package com.kitisplode.golemfirststonemod.entity.client.model;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.effect.AbstractEntityEffectCube;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class EntityModelShieldFirstBrick extends GeoModel<AbstractEntityEffectCube>
{
	@Override
	public Identifier getModelResource(AbstractEntityEffectCube animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "geo/first_brick_shield.geo.json");
	}

	@Override
	public Identifier getTextureResource(AbstractEntityEffectCube animatable)
	{
		return animatable.getTexture();
	}

	@Override
	public Identifier getAnimationResource(AbstractEntityEffectCube animatable)
	{
		return null;
	}
}
