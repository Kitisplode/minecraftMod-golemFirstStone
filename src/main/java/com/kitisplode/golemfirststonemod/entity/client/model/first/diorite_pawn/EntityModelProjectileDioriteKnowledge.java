package com.kitisplode.golemfirststonemod.entity.client.model;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.effect.AbstractEntityEffectCube;
import com.kitisplode.golemfirststonemod.entity.entity.projectile.EntityProjectileDioriteKnowledge;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class EntityModelProjectileDioriteKnowledge extends GeoModel<EntityProjectileDioriteKnowledge>
{
	@Override
	public Identifier getModelResource(EntityProjectileDioriteKnowledge animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "geo/first_brick_shield.geo.json");
	}

	@Override
	public Identifier getTextureResource(EntityProjectileDioriteKnowledge animatable)
	{
		return animatable.getTexture();
	}

	@Override
	public Identifier getAnimationResource(EntityProjectileDioriteKnowledge animatable)
	{
		return null;
	}
}
