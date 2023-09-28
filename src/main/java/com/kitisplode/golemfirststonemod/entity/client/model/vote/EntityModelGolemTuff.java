package com.kitisplode.golemfirststonemod.entity.client.model.vote;

import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelWithCustomAnimations;
import com.kitisplode.golemfirststonemod.entity.entity.golem.vote.EntityGolemTuff;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.data.EntityModelData;

public class EntityModelGolemTuff extends EntityModelWithCustomAnimations<EntityGolemTuff>
{
	@Override
	public ResourceLocation getModelResource(EntityGolemTuff animatable)
	{
		return animatable.getModelLocation();
	}

	@Override
	public ResourceLocation getTextureResource(EntityGolemTuff animatable)
	{
		return animatable.getTextureLocation();
	}

	@Override
	public ResourceLocation getAnimationResource(EntityGolemTuff animatable)
	{
		return animatable.getAnimationsLocation();
	}

	@Override
	public void setCustomAnimations(EntityGolemTuff animatable, long instanceId, AnimationState<EntityGolemTuff> animationState)
	{
		CoreGeoBone head = getAnimationProcessor().getBone("torso");
		if (head != null)
		{
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			this.savedBones.add(new SavedBone(head.getRotX(), "head", SavedBone.TYPES.ROTX));
			this.savedBones.add(new SavedBone(head.getRotY(), "head", SavedBone.TYPES.ROTY));
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

		if (animatable.getThrown())
		{
			CoreGeoBone whole = getAnimationProcessor().getBone("whole");
			if (whole != null)
			{
				this.savedBones.add(new SavedBone(whole.getRotX(), "whole", SavedBone.TYPES.ROTX));
				whole.setRotX(animatable.getThrowAngle() * Mth.DEG_TO_RAD);
			}
		}

		if (animatable.isHoldingItem())
		{
			CoreGeoBone arm_left = getAnimationProcessor().getBone("arm_left");
			if (arm_left != null)
			{
				this.savedBones.add(new SavedBone(arm_left.getRotX(), "arm_left", SavedBone.TYPES.ROTX));
				arm_left.setRotX(50.5f * Mth.DEG_TO_RAD);
			}
			CoreGeoBone arm_right = getAnimationProcessor().getBone("arm_right");
			if (arm_right != null)
			{
				this.savedBones.add(new SavedBone(arm_right.getRotX(), "arm_right", SavedBone.TYPES.ROTX));
				arm_right.setRotX(50.5f * Mth.DEG_TO_RAD);
			}
			CoreGeoBone cloak_out = getAnimationProcessor().getBone("cloak_out");
			if (cloak_out != null)
			{
				this.savedBones.add(new SavedBone(cloak_out.getScaleZ(), "cloak_out", SavedBone.TYPES.SCZ));
				cloak_out.setScaleZ(1.0f);
			}
		}
		else
		{
			if (animationState.isCurrentAnimationStage("animation.golem_tuff.idle")
					|| animationState.isCurrentAnimationStage("animation.golem_tuff.sit"))
			{
				CoreGeoBone arm_left = getAnimationProcessor().getBone("arm_left");
				if (arm_left != null)
				{
					this.savedBones.add(new SavedBone(arm_left.getRotX(), "arm_left", SavedBone.TYPES.ROTX));
					arm_left.setRotX(0f * Mth.DEG_TO_RAD);
				}
				CoreGeoBone arm_right = getAnimationProcessor().getBone("arm_right");
				if (arm_right != null)
				{
					this.savedBones.add(new SavedBone(arm_right.getRotX(), "arm_right", SavedBone.TYPES.ROTX));
					arm_right.setRotX(0f * Mth.DEG_TO_RAD);
				}
			}
			CoreGeoBone cloak_out = getAnimationProcessor().getBone("cloak_out");
			if (cloak_out != null)
			{
				this.savedBones.add(new SavedBone(cloak_out.getScaleZ(), "cloak_out", SavedBone.TYPES.SCZ));
				cloak_out.setScaleZ(0.0f);
			}
		}
	}
}
