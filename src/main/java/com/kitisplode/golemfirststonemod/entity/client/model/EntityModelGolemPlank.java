package com.kitisplode.golemfirststonemod.entity.client.model;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemMossy;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemPlank;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class EntityModelGolemPlank extends GeoModel<EntityGolemPlank>
{
	@Override
	public ResourceLocation getModelResource(EntityGolemPlank animatable)
	{
		return new ResourceLocation(GolemFirstStoneMod.MOD_ID, "geo/golem_plank.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(EntityGolemPlank animatable)
	{
		return new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/golem_plank.png");
	}

	@Override
	public ResourceLocation getAnimationResource(EntityGolemPlank animatable)
	{
		return new ResourceLocation(GolemFirstStoneMod.MOD_ID, "animations/golem_plank.animation.json");
	}

	@Override
	public void setCustomAnimations(EntityGolemPlank animatable, long instanceId, AnimationState<EntityGolemPlank> animationState)
	{
		CoreGeoBone head = getAnimationProcessor().getBone("head");
		if (head != null)
		{
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(Mth.clamp(entityData.headPitch(), -20.0f, 20.0f) * Mth.DEG_TO_RAD);
			head.setRotY(Mth.clamp(entityData.netHeadYaw(), -20.0f, 20.0f) * Mth.DEG_TO_RAD);
		}

		CoreGeoBone whole = getAnimationProcessor().getBone("whole");
		if (whole != null)
		{
			whole.setRotX(animatable.getThrowAngle() * Mth.DEG_TO_RAD);
		}
	}
}
