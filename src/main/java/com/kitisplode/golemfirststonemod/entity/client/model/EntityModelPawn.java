package com.kitisplode.golemfirststonemod.entity.client.model;

import com.kitisplode.golemfirststonemod.entity.entity.EntityPawn;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EntityModelPawn extends GeoModel<EntityPawn>
{
	@Override
	public ResourceLocation getModelResource(EntityPawn animatable)
	{
		return animatable.getModelLocation();
	}

	@Override
	public ResourceLocation getTextureResource(EntityPawn animatable)
	{
		return animatable.getTextureLocation();
	}

	@Override
	public ResourceLocation getAnimationResource(EntityPawn animatable)
	{
		return null;
	}
}
