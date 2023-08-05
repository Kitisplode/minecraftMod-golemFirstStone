package com.kitisplode.golemfirststonemod.entity.client;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.effect.EntityEffectShieldFirstBrick;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class EntityModelShieldFirstBrick extends GeoModel<EntityEffectShieldFirstBrick>
{
	@Override
	public Identifier getModelResource(EntityEffectShieldFirstBrick animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "geo/first_brick_shield.geo.json");
	}

	@Override
	public Identifier getTextureResource(EntityEffectShieldFirstBrick animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/first_brick_shield.png");
	}

	@Override
	public Identifier getAnimationResource(EntityEffectShieldFirstBrick animatable)
	{
		return null;
	}
}
