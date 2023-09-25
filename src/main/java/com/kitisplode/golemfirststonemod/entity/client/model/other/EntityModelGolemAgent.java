package com.kitisplode.golemfirststonemod.entity.client.model.other;

import com.kitisplode.golemfirststonemod.entity.entity.golem.other.EntityGolemAgent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class EntityModelGolemAgent extends GeoModel<EntityGolemAgent>
{
	@Override
	public Identifier getModelResource(EntityGolemAgent animatable)
	{
		return animatable.getModelLocation();
	}

	@Override
	public Identifier getTextureResource(EntityGolemAgent animatable)
	{
		return animatable.getTextureLocation();
	}

	@Override
	public Identifier getAnimationResource(EntityGolemAgent animatable)
	{
		return animatable.getAnimationsLocation();
	}

	@Override
	public void setCustomAnimations(EntityGolemAgent animatable, long instanceId, AnimationState<EntityGolemAgent> animationState)
	{
		CoreGeoBone head = getAnimationProcessor().getBone("head");
		if (head != null)
		{
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(MathHelper.clamp(entityData.headPitch(), -20.0f, 20.0f) * MathHelper.RADIANS_PER_DEGREE);
			head.setRotY(MathHelper.clamp(entityData.netHeadYaw(), -20.0f, 20.0f) * MathHelper.RADIANS_PER_DEGREE);
		}

		if (animatable.getThrown())
		{
			CoreGeoBone whole = getAnimationProcessor().getBone("whole");
			if (whole != null)
			{
				whole.setRotX(animatable.getThrowAngle() * MathHelper.RADIANS_PER_DEGREE);
			}
		}

		if (animatable.getSwingingArm())
		{
			float armSwing = animatable.getArmSwing();
			CoreGeoBone arm_right = getAnimationProcessor().getBone("arm_right");
			if (arm_right != null && armSwing != 0.0f)
			{
				arm_right.setRotX(armSwing * MathHelper.RADIANS_PER_DEGREE);
			}
		}
	}
}
