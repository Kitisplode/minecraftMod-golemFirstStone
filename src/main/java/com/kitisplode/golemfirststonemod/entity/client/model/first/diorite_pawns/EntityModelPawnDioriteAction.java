package com.kitisplode.golemfirststonemod.entity.client.model.first.diorite_pawns;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.diorite_pawns.EntityPawnDioriteAction;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemCobble;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class EntityModelPawnDioriteAction extends GeoModel<EntityPawnDioriteAction>
{
	@Override
	public ResourceLocation getModelResource(EntityPawnDioriteAction animatable)
	{
		return animatable.getModelLocation();
	}

	@Override
	public ResourceLocation getTextureResource(EntityPawnDioriteAction animatable)
	{
		return animatable.getTextureLocation();
	}

	@Override
	public ResourceLocation getAnimationResource(EntityPawnDioriteAction animatable)
	{
		return animatable.getAnimationsLocation();
	}

	@Override
	public void setCustomAnimations(EntityPawnDioriteAction animatable, long instanceId, AnimationState<EntityPawnDioriteAction> animationState)
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
