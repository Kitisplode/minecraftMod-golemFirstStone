package com.kitisplode.golemfirststonemod.entity.client.model;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemCobble;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemFirstStone;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class EntityModelGolemCobble extends GeoModel<EntityGolemCobble>
{
	@Override
	public Identifier getModelResource(EntityGolemCobble animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "geo/golem_cobble.geo.json");
	}

	@Override
	public Identifier getTextureResource(EntityGolemCobble animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/golem_cobble.png");
	}

	@Override
	public Identifier getAnimationResource(EntityGolemCobble animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "animations/golem_cobble.animation.json");
	}

	@Override
	public void setCustomAnimations(EntityGolemCobble animatable, long instanceId, AnimationState<EntityGolemCobble> animationState)
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
