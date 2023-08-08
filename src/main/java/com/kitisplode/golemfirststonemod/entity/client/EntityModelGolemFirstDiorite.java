package com.kitisplode.golemfirststonemod.entity.client;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemFirstDiorite;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemFirstStone;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class EntityModelGolemFirstDiorite extends GeoModel<EntityGolemFirstDiorite>
{
	@Override
	public Identifier getModelResource(EntityGolemFirstDiorite animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "geo/first_diorite.geo.json");
	}

	@Override
	public Identifier getTextureResource(EntityGolemFirstDiorite animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/first_diorite.png");
	}

	@Override
	public Identifier getAnimationResource(EntityGolemFirstDiorite animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "animations/first_stone_2.animation.json");
	}

	@Override
	public void setCustomAnimations(EntityGolemFirstDiorite animatable, long instanceId, AnimationState<EntityGolemFirstDiorite> animationState)
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
