package com.kitisplode.golemfirststonemod.entity.client.model;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.EntityVillagerDandori;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class EntityModelVillagerDandori extends GeoModel<EntityVillagerDandori>
{
	@Override
	public Identifier getModelResource(EntityVillagerDandori animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "geo/villager_dandori.geo.json");
	}

	@Override
	public Identifier getTextureResource(EntityVillagerDandori animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/villager_dandori.png");
	}

	@Override
	public Identifier getAnimationResource(EntityVillagerDandori animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "animations/villager_dandori.animation.json");
	}

	@Override
	public void setCustomAnimations(EntityVillagerDandori animatable, long instanceId, AnimationState<EntityVillagerDandori> animationState)
	{
		CoreGeoBone head = getAnimationProcessor().getBone("head");
		if (head != null)
		{
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
			head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
		}
	}
}
