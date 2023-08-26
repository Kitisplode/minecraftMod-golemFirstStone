package com.kitisplode.golemfirststonemod.entity.client.model;

import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemFirstStone;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityPawn;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class EntityModelPawnFirstDiorite extends GeoModel<EntityPawn>
{
	@Override
	public Identifier getModelResource(EntityPawn animatable)
	{
		return animatable.getModelLocation();
	}

	@Override
	public Identifier getTextureResource(EntityPawn animatable)
	{
		return animatable.getTextureLocation();
	}

	@Override
	public Identifier getAnimationResource(EntityPawn animatable)
	{
		return null;
	}

	@Override
	public void setCustomAnimations(EntityPawn animatable, long instanceId, AnimationState<EntityPawn> animationState)
	{
		CoreGeoBone whole = getAnimationProcessor().getBone("whole");
		if (whole != null)
		{
//			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			whole.setRotX(animatable.thrownAngle * MathHelper.RADIANS_PER_DEGREE);
		}
	}
}
