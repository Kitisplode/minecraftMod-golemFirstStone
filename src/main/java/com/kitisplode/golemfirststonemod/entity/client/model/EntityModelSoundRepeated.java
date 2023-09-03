package com.kitisplode.golemfirststonemod.entity.client.model;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.effect.EntitySoundRepeated;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.EntityGolemFirstStone;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class EntityModelSoundRepeated extends GeoModel<EntitySoundRepeated>
{
	@Override
	public Identifier getModelResource(EntitySoundRepeated animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "geo/first_brick_shield.geo.json");
	}

	@Override
	public Identifier getTextureResource(EntitySoundRepeated animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/first/first_stone_2.png");
	}

	@Override
	public Identifier getAnimationResource(EntitySoundRepeated animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "animations/first_stone_2.animation.json");
	}
}
