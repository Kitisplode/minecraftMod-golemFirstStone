package com.kitisplode.golemfirststonemod.entity.client.model.dungeons;

import com.kitisplode.golemfirststonemod.entity.entity.golem.dungeons.EntityGolemKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class EntityModelGolemKey extends GeoModel<EntityGolemKey>
{
    @Override
    public ResourceLocation getModelResource(EntityGolemKey animatable)
    {
        return animatable.getModelLocation();
    }

    @Override
    public ResourceLocation getTextureResource(EntityGolemKey animatable)
    {
        return animatable.getTextureLocation();
    }

    @Override
    public ResourceLocation getAnimationResource(EntityGolemKey animatable)
    {
        return animatable.getAnimationsLocation();
    }

    @Override
    public void setCustomAnimations(EntityGolemKey animatable, long instanceId, AnimationState<EntityGolemKey> animationState)
    {
        CoreGeoBone head = getAnimationProcessor().getBone("head");
        if (head != null)
        {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }

        if (animatable.getThrown())
        {
            CoreGeoBone whole = getAnimationProcessor().getBone("whole");
            if (whole != null)
            {
                whole.setRotX(animatable.getThrowAngle() * Mth.DEG_TO_RAD);
            }
        }
    }
}
