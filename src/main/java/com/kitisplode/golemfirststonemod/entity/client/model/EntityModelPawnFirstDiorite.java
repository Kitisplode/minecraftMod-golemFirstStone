package com.kitisplode.golemfirststonemod.entity.client.model;

import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityPawn;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class EntityModelPawnFirstDiorite extends GeoModel<EntityPawn>
{
	@Override
	public Identifier getModelResource(EntityPawn animatable)
	{
		return animatable.getModelLocation();
	}

	@Override
	public Identifier getTextureResource(EntityPawn animatable)
	{
		return animatable.getTextureLocation();
	}

	@Override
	public Identifier getAnimationResource(EntityPawn animatable)
	{
		return null;
	}
}
