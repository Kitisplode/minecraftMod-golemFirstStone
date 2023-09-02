package com.kitisplode.golemfirststonemod.entity.client.model;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemGrindstone;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class EntityModelGolemGrindstone extends GeoModel<EntityGolemGrindstone>
{
	@Override
	public Identifier getModelResource(EntityGolemGrindstone animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "geo/golem_grindstone.geo.json");
	}

	@Override
	public Identifier getTextureResource(EntityGolemGrindstone animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/golem_grindstone.png");
	}

	@Override
	public Identifier getAnimationResource(EntityGolemGrindstone animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "animations/golem_grindstone.animation.json");
	}

	@Override
	public void setCustomAnimations(EntityGolemGrindstone animatable, long instanceId, AnimationState<EntityGolemGrindstone> animationState)
	{
		if (animationState.isCurrentAnimationStage("animation.golem_grindstone.idle"))
		{
			CoreGeoBone head = getAnimationProcessor().getBone("head");
			if (head != null)
			{
				EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
				head.setRotX(MathHelper.clamp(entityData.headPitch(), -20.0f, 20.0f) * MathHelper.RADIANS_PER_DEGREE);
				head.setRotY(MathHelper.clamp(entityData.netHeadYaw(), -20.0f, 20.0f) * MathHelper.RADIANS_PER_DEGREE);
			}
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
