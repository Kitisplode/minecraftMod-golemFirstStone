package com.kitisplode.golemfirststonemod.entity.client.model.first;

import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelWithCustomAnimations;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.EntityGolemFirstStone;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.data.EntityModelData;

public class EntityModelGolemFirstStone extends EntityModelWithCustomAnimations<EntityGolemFirstStone>
{
	@Override
	public ResourceLocation getModelResource(EntityGolemFirstStone animatable)
	{
		return animatable.getModelLocation();
	}

	@Override
	public ResourceLocation getTextureResource(EntityGolemFirstStone animatable)
	{
		return animatable.getTextureLocation();
	}

	@Override
	public ResourceLocation getAnimationResource(EntityGolemFirstStone animatable)
	{
		return animatable.getAnimationsLocation();
	}

	@Override
	public void setCustomAnimations(EntityGolemFirstStone animatable, long instanceId, AnimationState<EntityGolemFirstStone> animationState)
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
}
