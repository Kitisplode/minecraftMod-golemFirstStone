package com.kitisplode.golemfirststonemod.entity.client.model;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.pawn.EntityPawnFirstDiorite;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EntityModelPawnFirstDiorite extends GeoModel<EntityPawnFirstDiorite>
{
	@Override
	public ResourceLocation getModelResource(EntityPawnFirstDiorite animatable)
	{
		int pawnType = animatable.getPawnType();
		return switch (pawnType)
		{
			case 0 -> new ResourceLocation(GolemFirstStoneMod.MOD_ID, "geo/diorite_action.geo.json");
			case 1 -> new ResourceLocation(GolemFirstStoneMod.MOD_ID, "geo/diorite_foresight.geo.json");
			default -> new ResourceLocation(GolemFirstStoneMod.MOD_ID, "geo/diorite_knowledge.geo.json");
		};
	}

	@Override
	public ResourceLocation getTextureResource(EntityPawnFirstDiorite animatable)
	{
		int pawnType = animatable.getPawnType();
		return switch (pawnType)
		{
			case 0 -> new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/diorite_action.png");
			case 1 -> new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/diorite_foresight.png");
			default -> new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/diorite_knowledge.png");
		};
	}

	@Override
	public ResourceLocation getAnimationResource(EntityPawnFirstDiorite animatable)
	{
		return null;
	}
}
