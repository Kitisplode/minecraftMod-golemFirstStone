package com.kitisplode.golemfirststonemod.entity.client.model.legends;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemPlank;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class EntityModelGolemPlank extends GeoModel<EntityGolemPlank>
{
	@Override
	public Identifier getModelResource(EntityGolemPlank animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "geo/golem_plank.geo.json");
	}

	@Override
	public Identifier getTextureResource(EntityGolemPlank animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/golem_plank.png");
	}

	@Override
	public Identifier getAnimationResource(EntityGolemPlank animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "animations/golem_plank.animation.json");
	}

	@Override
	public void setCustomAnimations(EntityGolemPlank animatable, long instanceId, AnimationState<EntityGolemPlank> animationState)
	{
		CoreGeoBone head = getAnimationProcessor().getBone("head");
		if (head != null)
		{
			EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(MathHelper.clamp(entityData.headPitch(), -20.0f, 20.0f) * MathHelper.RADIANS_PER_DEGREE);
			head.setRotY(MathHelper.clamp(entityData.netHeadYaw(), -20.0f, 20.0f) * MathHelper.RADIANS_PER_DEGREE);
		}

		if (animatable.getThrown())
		{
			CoreGeoBone whole = getAnimationProcessor().getBone("whole");
			if (whole != null)
			{
				whole.setRotX(animatable.getThrowAngle() * MathHelper.RADIANS_PER_DEGREE);
			}
		}
	}
}
