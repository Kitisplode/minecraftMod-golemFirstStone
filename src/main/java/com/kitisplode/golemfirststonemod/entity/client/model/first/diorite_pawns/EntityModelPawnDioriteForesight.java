package com.kitisplode.golemfirststonemod.entity.client.model.first.diorite_pawns;

import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelWithCustomAnimations;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.diorite_pawns.EntityPawnDioriteForesight;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;

public class EntityModelPawnDioriteForesight extends EntityModelWithCustomAnimations<EntityPawnDioriteForesight>
{
	@Override
	public ResourceLocation getModelResource(EntityPawnDioriteForesight animatable)
	{
		return animatable.getModelLocation();
	}

	@Override
	public ResourceLocation getTextureResource(EntityPawnDioriteForesight animatable)
	{
		return animatable.getTextureLocation();
	}

	@Override
	public ResourceLocation getAnimationResource(EntityPawnDioriteForesight animatable)
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
				this.savedBones.add(new SavedBone(whole.getRotX(), "whole", SavedBone.TYPES.ROTX));
				whole.setRotX(animatable.getThrowAngle() * Mth.DEG_TO_RAD);
			}
			whole.setPosY((float)animatable.floatAmount);
		}

	}
}
