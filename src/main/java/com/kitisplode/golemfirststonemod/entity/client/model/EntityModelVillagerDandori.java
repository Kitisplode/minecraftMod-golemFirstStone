package com.kitisplode.golemfirststonemod.entity.client.model;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.EntityVillagerDandori;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class EntityModelVillagerDandori extends GeoModel<EntityVillagerDandori>
{
	@Override
	public ResourceLocation getModelResource(EntityVillagerDandori animatable)
	{
		return new ResourceLocation(GolemFirstStoneMod.MOD_ID, "geo/villager_dandori.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(EntityVillagerDandori animatable)
	{
		return new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/villager_dandori.png");
	}

	@Override
	public ResourceLocation getAnimationResource(EntityVillagerDandori animatable)
	{
		return new ResourceLocation(GolemFirstStoneMod.MOD_ID, "animations/villager_dandori.animation.json");
	}

	@Override
	public void setCustomAnimations(EntityVillagerDandori animatable, long instanceId, AnimationState<EntityVillagerDandori> animationState)
	{
		CoreGeoBone head = getAnimationProcessor().getBone("head");
		if (head != null)
		{
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}
	}
}
