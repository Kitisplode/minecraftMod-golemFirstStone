package com.kitisplode.golemfirststonemod.entity.client.model.dungeons;

import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelWithCustomAnimations;
import com.kitisplode.golemfirststonemod.entity.entity.golem.dungeons.EntityGolemKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.data.EntityModelData;

public class EntityModelGolemKey extends EntityModelWithCustomAnimations<EntityGolemKey>
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
            this.savedBones.add(new SavedBone(head.getRotX(), "head", SavedBone.TYPES.ROTX));
            this.savedBones.add(new SavedBone(head.getRotY(), "head", SavedBone.TYPES.ROTY));
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.updateRotation(entityData.headPitch() * Mth.DEG_TO_RAD, entityData.netHeadYaw() * Mth.DEG_TO_RAD, 0);
        }

        if (animatable.getThrown())
        {
            CoreGeoBone whole = getAnimationProcessor().getBone("whole");
            if (whole != null)
            {
                this.savedBones.add(new SavedBone(whole.getRotX(), "whole", SavedBone.TYPES.ROTX));
                whole.setRotX(animatable.getThrowAngle() * Mth.DEG_TO_RAD);
            }
        }
    }
}
