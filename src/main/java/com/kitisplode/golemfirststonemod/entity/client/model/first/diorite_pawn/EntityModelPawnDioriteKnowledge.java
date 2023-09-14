package com.kitisplode.golemfirststonemod.entity.client.model.first.diorite_pawn;

import com.kitisplode.golemfirststonemod.entity.entity.golem.first.diorite_pawn.EntityPawnDioriteKnowledge;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class EntityModelPawnDioriteKnowledge extends GeoModel<EntityPawnDioriteKnowledge>
{
	@Override
	public Identifier getModelResource(EntityPawnDioriteKnowledge animatable)
	{
		return animatable.getModelLocation();
	}

	@Override
	public Identifier getTextureResource(EntityPawnDioriteKnowledge animatable)
	{
		return animatable.getTextureLocation();
	}

	@Override
	public Identifier getAnimationResource(EntityPawnDioriteKnowledge animatable)
	{
		return animatable.getAnimationsLocation();
	}

	@Override
	public void setCustomAnimations(EntityPawnDioriteKnowledge animatable, long instanceId, AnimationState<EntityPawnDioriteKnowledge> animationState)
	{
		CoreGeoBone whole = getAnimationProcessor().getBone("whole");
		if (whole != null)
		{
			if (animatable.getThrown())
			{
				whole.setRotX(animatable.getThrowAngle() * MathHelper.RADIANS_PER_DEGREE);
			}
		}
	}
}
