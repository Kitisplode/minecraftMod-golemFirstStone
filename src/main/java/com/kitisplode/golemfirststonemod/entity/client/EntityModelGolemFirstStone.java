package com.kitisplode.golemfirststonemod.entity.client;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.custom.EntityGolemFirstStone;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class EntityModelGolemFirstStone extends GeoModel<EntityGolemFirstStone>
{
	@Override
	public Identifier getModelResource(EntityGolemFirstStone animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "geo/first_stone_2.geo.json");
	}

	@Override
	public Identifier getTextureResource(EntityGolemFirstStone animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/first_stone_2.png");
	}

	@Override
	public Identifier getAnimationResource(EntityGolemFirstStone animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "animations/first_stone_2.animation.json");
	}

	@Override
	public void setCustomAnimations(EntityGolemFirstStone animatable, long instanceId, AnimationState<EntityGolemFirstStone> animationState)
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
