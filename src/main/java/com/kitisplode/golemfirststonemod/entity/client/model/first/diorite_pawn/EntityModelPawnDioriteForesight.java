package com.kitisplode.golemfirststonemod.entity.client.model.first.diorite_pawn;

import com.kitisplode.golemfirststonemod.entity.entity.golem.first.diorite_pawn.EntityPawnDioriteAction;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.diorite_pawn.EntityPawnDioriteForesight;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class EntityModelPawnDioriteForesight extends GeoModel<EntityPawnDioriteForesight>
{
	@Override
	public Identifier getModelResource(EntityPawnDioriteForesight animatable)
	{
		return animatable.getModelLocation();
	}

	@Override
	public Identifier getTextureResource(EntityPawnDioriteForesight animatable)
	{
		return animatable.getTextureLocation();
	}

	@Override
	public Identifier getAnimationResource(EntityPawnDioriteForesight animatable)
	{
		return animatable.getAnimationsLocation();
	}

	@Override
	public void setCustomAnimations(EntityPawnDioriteForesight animatable, long instanceId, AnimationState<EntityPawnDioriteForesight> animationState)
	{
		CoreGeoBone whole = getAnimationProcessor().getBone("whole");
		if (whole != null)
		{
			if (animatable.getThrown())
			{
				whole.setRotX(animatable.getThrowAngle() * MathHelper.RADIANS_PER_DEGREE);
			}
			whole.setPosY((float)animatable.floatAmount);
		}
	}
}
