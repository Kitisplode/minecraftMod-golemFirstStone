package com.kitisplode.golemfirststonemod.entity.client.model;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.EntityGolemFirstDiorite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class EntityModelGolemFirstDiorite extends GeoModel<EntityGolemFirstDiorite>
{
	@Override
	public ResourceLocation getModelResource(EntityGolemFirstDiorite animatable)
	{
		return new ResourceLocation(GolemFirstStoneMod.MOD_ID, "geo/first_diorite.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(EntityGolemFirstDiorite animatable)
	{
		return new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/first/first_diorite.png");
	}

	@Override
	public ResourceLocation getAnimationResource(EntityGolemFirstDiorite animatable)
	{
		return new ResourceLocation(GolemFirstStoneMod.MOD_ID, "animations/first_diorite.animation.json");
	}

	@Override
	public void setCustomAnimations(EntityGolemFirstDiorite animatable, long instanceId, AnimationState<EntityGolemFirstDiorite> animationState)
	{
		CoreGeoBone head = getAnimationProcessor().getBone("head");
		if (head != null)
		{
			if (animationState.isCurrentAnimationStage("animation.first_diorite.idle"))
			{
				EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
				head.setRotX(Mth.clamp(entityData.headPitch(), -20.0f, 20.0f) * Mth.DEG_TO_RAD);
				head.setRotY(Mth.clamp(entityData.netHeadYaw(), -20.0f, 20.0f) * Mth.DEG_TO_RAD);
				head.setRotZ(0);
			}
		}
	}
}
