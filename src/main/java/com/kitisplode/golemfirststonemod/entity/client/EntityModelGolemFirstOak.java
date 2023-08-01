package com.kitisplode.golemfirststonemod.entity.client;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.custom.EntityGolemFirstOak;
import com.kitisplode.golemfirststonemod.entity.custom.EntityGolemFirstStone;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class EntityModelGolemFirstOak extends GeoModel<EntityGolemFirstOak>
{
	@Override
	public Identifier getModelResource(EntityGolemFirstOak animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "geo/first_oak.geo.json");
	}

	@Override
	public Identifier getTextureResource(EntityGolemFirstOak animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/first_oak.png");
	}

	@Override
	public Identifier getAnimationResource(EntityGolemFirstOak animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "animations/first_oak.animation.json");
	}

	@Override
	public void setCustomAnimations(EntityGolemFirstOak animatable, long instanceId, AnimationState<EntityGolemFirstOak> animationState)
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
