package com.kitisplode.golemfirststonemod.entity.client.model.first;

import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelWithCustomAnimations;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.EntityGolemFirstDiorite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.data.EntityModelData;

public class EntityModelGolemFirstDiorite extends EntityModelWithCustomAnimations<EntityGolemFirstDiorite>
{
	@Override
	public ResourceLocation getModelResource(EntityGolemFirstDiorite animatable)
	{
		return animatable.getModelLocation();
	}

	@Override
	public ResourceLocation getTextureResource(EntityGolemFirstDiorite animatable)
	{
		return animatable.getTextureLocation();
	}

	@Override
	public ResourceLocation getAnimationResource(EntityGolemFirstDiorite animatable)
	{
		return animatable.getAnimationsLocation();
	}

	@Override
	public void setCustomAnimations(EntityGolemFirstDiorite animatable, long instanceId, AnimationState<EntityGolemFirstDiorite> animationState)
	{
		CoreGeoBone head = getAnimationProcessor().getBone("head");
		if (head != null)
		{
			if (animationState.isCurrentAnimationStage("animation.first_diorite.idle"))
			{
				EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
				this.savedBones.add(new SavedBone(head.getRotX(), "head", SavedBone.TYPES.ROTX));
				this.savedBones.add(new SavedBone(head.getRotY(), "head", SavedBone.TYPES.ROTY));
				this.savedBones.add(new SavedBone(head.getRotZ(), "head", SavedBone.TYPES.ROTZ));
				head.setRotX(Mth.clamp(entityData.headPitch(), -20.0f, 20.0f) * Mth.DEG_TO_RAD);
				head.setRotY(Mth.clamp(entityData.netHeadYaw(), -20.0f, 20.0f) * Mth.DEG_TO_RAD);
				head.setRotZ(0);
			}
		}
	}
}
