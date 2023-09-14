package com.kitisplode.golemfirststonemod.entity.client.model.first;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.EntityGolemFirstBrick;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class EntityModelGolemFirstBrick extends GeoModel<EntityGolemFirstBrick>
{
	@Override
	public Identifier getModelResource(EntityGolemFirstBrick animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "geo/first_brick.geo.json");
	}

	@Override
	public Identifier getTextureResource(EntityGolemFirstBrick animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/first/first_brick.png");
	}

	@Override
	public Identifier getAnimationResource(EntityGolemFirstBrick animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "animations/first_brick.animation.json");
	}

	@Override
	public void setCustomAnimations(EntityGolemFirstBrick animatable, long instanceId, AnimationState<EntityGolemFirstBrick> animationState)
	{
		CoreGeoBone head = getAnimationProcessor().getBone("head");
		if (head != null)
		{
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(MathHelper.clamp(entityData.headPitch(), -20.0f, 20.0f) * MathHelper.RADIANS_PER_DEGREE);
			head.setRotY(MathHelper.clamp(entityData.netHeadYaw(), -20.0f, 20.0f) * MathHelper.RADIANS_PER_DEGREE);
		}
	}
}
