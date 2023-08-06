package com.kitisplode.golemfirststonemod.entity.client;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemFirstStone;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class EntityModelGolemFirstStone extends GeoModel<EntityGolemFirstStone>
{
	@Override
	public ResourceLocation getModelResource(EntityGolemFirstStone animatable)
	{
		return new ResourceLocation(GolemFirstStoneMod.MOD_ID, "geo/first_stone_2.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(EntityGolemFirstStone animatable)
	{
		return new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/first_stone_2.png");
	}

	@Override
	public ResourceLocation getAnimationResource(EntityGolemFirstStone animatable)
	{
		return new ResourceLocation(GolemFirstStoneMod.MOD_ID, "animations/first_stone_2.animation.json");
	}

	@Override
	public void setCustomAnimations(EntityGolemFirstStone animatable, long instanceId, AnimationState<EntityGolemFirstStone> animationState)
	{
		CoreGeoBone head = getAnimationProcessor().getBone("head");
		if (head != null)
		{
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(Mth.clamp(entityData.headPitch(), -20.0f, 20.0f) * Mth.DEG_TO_RAD);
			head.setRotY(Mth.clamp(entityData.netHeadYaw(), -20.0f, 20.0f) * Mth.DEG_TO_RAD);
		}
	}
}
