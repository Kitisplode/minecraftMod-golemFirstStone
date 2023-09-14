package com.kitisplode.golemfirststonemod.entity.client.model.first.diorite_pawn;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.diorite_pawn.EntityPawnDioriteAction;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemCobble;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class EntityModelPawnDioriteAction extends GeoModel<EntityPawnDioriteAction>
{
	@Override
	public Identifier getModelResource(EntityPawnDioriteAction animatable)
	{
		return animatable.getModelLocation();
	}

	@Override
	public Identifier getTextureResource(EntityPawnDioriteAction animatable)
	{
		return animatable.getTextureLocation();
	}

	@Override
	public Identifier getAnimationResource(EntityPawnDioriteAction animatable)
	{
		return animatable.getAnimationsLocation();
	}

	@Override
	public void setCustomAnimations(EntityPawnDioriteAction animatable, long instanceId, AnimationState<EntityPawnDioriteAction> animationState)
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
