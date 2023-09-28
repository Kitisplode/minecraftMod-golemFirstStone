package com.kitisplode.golemfirststonemod.entity.client.model.legends;

import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelWithCustomAnimations;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemMossy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.data.EntityModelData;

public class EntityModelGolemMossy extends EntityModelWithCustomAnimations<EntityGolemMossy>
{
	@Override
	public ResourceLocation getModelResource(EntityGolemMossy animatable)
	{
		return animatable.getModelLocation();
	}

	@Override
	public ResourceLocation getTextureResource(EntityGolemMossy animatable)
	{
		return animatable.getTextureLocation();
	}

	@Override
	public ResourceLocation getAnimationResource(EntityGolemMossy animatable)
	{
		return animatable.getAnimationsLocation();
	}

	@Override
	public void setCustomAnimations(EntityGolemMossy animatable, long instanceId, AnimationState<EntityGolemMossy> animationState)
	{
		if (animationState.isCurrentAnimationStage("animation.golem_mossy.idle"))
		{
			CoreGeoBone head = getAnimationProcessor().getBone("head");
			if (head != null)
			{
				EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
				this.savedBones.add(new SavedBone(head.getRotX(), "head", SavedBone.TYPES.ROTX));
				this.savedBones.add(new SavedBone(head.getRotY(), "head", SavedBone.TYPES.ROTY));
				head.setRotX(Mth.clamp(entityData.headPitch(), -20.0f, 20.0f) * Mth.DEG_TO_RAD);
				head.setRotY(Mth.clamp(entityData.netHeadYaw(), -20.0f, 20.0f) * Mth.DEG_TO_RAD);
			}
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
	}
}
