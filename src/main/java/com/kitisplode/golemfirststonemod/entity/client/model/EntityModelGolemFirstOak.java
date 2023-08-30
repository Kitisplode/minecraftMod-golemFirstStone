package com.kitisplode.golemfirststonemod.entity.client.model;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.EntityGolemFirstOak;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class EntityModelGolemFirstOak extends GeoModel<EntityGolemFirstOak>
{
	@Override
	public ResourceLocation getModelResource(EntityGolemFirstOak animatable)
	{
		return new ResourceLocation(GolemFirstStoneMod.MOD_ID, "geo/first_oak.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(EntityGolemFirstOak animatable)
	{
		return new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/first/first_oak.png");
	}

	@Override
	public ResourceLocation getAnimationResource(EntityGolemFirstOak animatable)
	{
		return new ResourceLocation(GolemFirstStoneMod.MOD_ID, "animations/first_oak.animation.json");
	}

	@Override
	public void setCustomAnimations(EntityGolemFirstOak animatable, long instanceId, AnimationState<EntityGolemFirstOak> animationState)
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
