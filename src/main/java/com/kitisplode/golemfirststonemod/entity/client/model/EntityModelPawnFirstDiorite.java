package com.kitisplode.golemfirststonemod.entity.client.model;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.pawn.EntityPawnFirstDiorite;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class EntityModelPawnFirstDiorite extends GeoModel<EntityPawnFirstDiorite>
{
	@Override
	public Identifier getModelResource(EntityPawnFirstDiorite animatable)
	{
		int pawnType = animatable.getPawnType();
		return switch (pawnType)
		{
			case 0 -> new Identifier(GolemFirstStoneMod.MOD_ID, "geo/diorite_action.geo.json");
			case 1 -> new Identifier(GolemFirstStoneMod.MOD_ID, "geo/diorite_foresight.geo.json");
			default -> new Identifier(GolemFirstStoneMod.MOD_ID, "geo/diorite_knowledge.geo.json");
		};
	}

	@Override
	public Identifier getTextureResource(EntityPawnFirstDiorite animatable)
	{
		return animatable.getTextureLocation();
	}

	@Override
	public Identifier getAnimationResource(EntityPawnFirstDiorite animatable)
	{
		return null;
	}
}
