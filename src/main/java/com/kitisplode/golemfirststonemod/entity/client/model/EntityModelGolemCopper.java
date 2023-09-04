package com.kitisplode.golemfirststonemod.entity.client.model;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemPlank;
import com.kitisplode.golemfirststonemod.entity.entity.golem.vote.EntityGolemCopper;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class EntityModelGolemCopper extends GeoModel<EntityGolemCopper>
{
	@Override
	public Identifier getModelResource(EntityGolemCopper animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "geo/golem_copper.geo.json");
	}

	@Override
	public Identifier getTextureResource(EntityGolemCopper animatable)
	{
		return animatable.getTexture();
	}

	@Override
	public Identifier getAnimationResource(EntityGolemCopper animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "animations/golem_copper.animation.json");
	}

	@Override
	public void setCustomAnimations(EntityGolemCopper animatable, long instanceId, AnimationState<EntityGolemCopper> animationState)
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
	}
}
