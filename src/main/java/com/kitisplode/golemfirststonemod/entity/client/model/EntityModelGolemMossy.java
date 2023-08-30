package com.kitisplode.golemfirststonemod.entity.client.model;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemCobble;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemMossy;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class EntityModelGolemMossy extends GeoModel<EntityGolemMossy>
{
	@Override
	public ResourceLocation getModelResource(EntityGolemMossy animatable)
	{
		return new ResourceLocation(GolemFirstStoneMod.MOD_ID, "geo/golem_mossy.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(EntityGolemMossy animatable)
	{
		return new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/golem_mossy.png");
	}

	@Override
	public ResourceLocation getAnimationResource(EntityGolemMossy animatable)
	{
		return new ResourceLocation(GolemFirstStoneMod.MOD_ID, "animations/golem_mossy.animation.json");
	}

	@Override
	public void setCustomAnimations(EntityGolemMossy animatable, long instanceId, AnimationState<EntityGolemMossy> animationState)
	{
		if (animationState.isCurrentAnimationStage("animation.golem_mossy.idle"))
		{
			CoreGeoBone head = getAnimationProcessor().getBone("head");
			if (head != null)
			{
				EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
				head.setRotX(Mth.clamp(entityData.headPitch(), -20.0f, 20.0f) * Mth.DEG_TO_RAD);
				head.setRotY(Mth.clamp(entityData.netHeadYaw(), -20.0f, 20.0f) * Mth.DEG_TO_RAD);
			}
		}

		CoreGeoBone whole = getAnimationProcessor().getBone("whole");
		if (whole != null)
		{
			whole.setRotX(animatable.getThrowAngle() * Mth.DEG_TO_RAD);
		}
	}
}
