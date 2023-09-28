package com.kitisplode.golemfirststonemod.entity.client.model.other;

import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelWithCustomAnimations;
import com.kitisplode.golemfirststonemod.entity.entity.golem.other.EntityGolemAgent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.data.EntityModelData;

public class EntityModelGolemAgent extends EntityModelWithCustomAnimations<EntityGolemAgent>
{
    @Override
    public ResourceLocation getModelResource(EntityGolemAgent animatable)
    {
        return animatable.getModelLocation();
    }

    @Override
    public ResourceLocation getTextureResource(EntityGolemAgent animatable)
    {
        return animatable.getTextureLocation();
    }

    @Override
    public ResourceLocation getAnimationResource(EntityGolemAgent animatable)
    {
        return animatable.getAnimationsLocation();
    }

    @Override
    public void setCustomAnimations(EntityGolemAgent animatable, long instanceId, AnimationState<EntityGolemAgent> animationState)
    {
        CoreGeoBone head = getAnimationProcessor().getBone("head");
        if (head != null)
        {
            this.savedBones.add(new SavedBone(head.getRotX(), "head", SavedBone.TYPES.ROTX));
            this.savedBones.add(new SavedBone(head.getRotY(), "head", SavedBone.TYPES.ROTY));
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
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

        if (animatable.getSwingingArm())
        {
            float armSwing = animatable.getArmSwing();
            CoreGeoBone arm_right = getAnimationProcessor().getBone("arm_right");
            if (arm_right != null)
            {
                this.savedBones.add(new SavedBone(arm_right.getRotX(), "arm_right", SavedBone.TYPES.ROTX));
                if (armSwing != 0.0f) arm_right.setRotX(armSwing * Mth.DEG_TO_RAD);
            }
        }
    }
}
