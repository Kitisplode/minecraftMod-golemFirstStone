package com.kitisplode.golemfirststonemod.entity.client.model;

import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityPawn;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
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

	@Override
	public void setCustomAnimations(EntityPawn animatable, long instanceId, AnimationState<EntityPawn> animationState)
	{
		CoreGeoBone whole = getAnimationProcessor().getBone("whole");
		if (whole != null)
		{
			whole.setRotX(animatable.getThrowAngle() * Mth.DEG_TO_RAD);
		}
	}
}
