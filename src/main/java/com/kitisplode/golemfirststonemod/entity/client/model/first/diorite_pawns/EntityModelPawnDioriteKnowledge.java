package com.kitisplode.golemfirststonemod.entity.client.model.first.diorite_pawns;

import com.kitisplode.golemfirststonemod.entity.entity.golem.first.diorite_pawns.EntityPawnDioriteAction;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.diorite_pawns.EntityPawnDioriteKnowledge;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class EntityModelPawnDioriteKnowledge extends GeoModel<EntityPawnDioriteKnowledge>
{
	@Override
	public ResourceLocation getModelResource(EntityPawnDioriteKnowledge animatable)
	{
		return animatable.getModelLocation();
	}

	@Override
	public ResourceLocation getTextureResource(EntityPawnDioriteKnowledge animatable)
	{
		return animatable.getTextureLocation();
	}

	@Override
	public ResourceLocation getAnimationResource(EntityPawnDioriteKnowledge animatable)
	{
		return animatable.getAnimationsLocation();
	}

	@Override
	public void setCustomAnimations(EntityPawnDioriteKnowledge animatable, long instanceId, AnimationState<EntityPawnDioriteKnowledge> animationState)
	{
		if (animatable.getThrown())
		{
			CoreGeoBone whole = getAnimationProcessor().getBone("whole");
			if (whole != null)
			{
				whole.setRotX(animatable.getThrowAngle() * Mth.DEG_TO_RAD);
			}
		}
	}
}
