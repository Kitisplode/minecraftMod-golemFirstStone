package com.kitisplode.golemfirststonemod.entity.client.model.story;

import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelWithCustomAnimations;
import com.kitisplode.golemfirststonemod.entity.entity.golem.story.EntityGolemPrison;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.data.EntityModelData;

public class EntityModelGolemPrison extends EntityModelWithCustomAnimations<EntityGolemPrison>
{
    @Override
    public ResourceLocation getModelResource(EntityGolemPrison animatable)
    {
        return animatable.getModelLocation();
    }

    @Override
    public ResourceLocation getTextureResource(EntityGolemPrison animatable)
    {
        return animatable.getTextureLocation();
    }

    @Override
    public ResourceLocation getAnimationResource(EntityGolemPrison animatable)
    {
        return animatable.getAnimationsLocation();
    }

    @Override
    public void setCustomAnimations(EntityGolemPrison animatable, long instanceId, AnimationState<EntityGolemPrison> animationState)
    {
        CoreGeoBone head = getAnimationProcessor().getBone("head");
        if (head != null)
        {
            this.savedBones.add(new SavedBone(head.getRotX(), "head", SavedBone.TYPES.ROTX));
            this.savedBones.add(new SavedBone(head.getRotY(), "head", SavedBone.TYPES.ROTY));
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.updateRotation(entityData.headPitch() * Mth.DEG_TO_RAD, entityData.netHeadYaw() * Mth.DEG_TO_RAD, 0);
        }
        CoreGeoBone light = getAnimationProcessor().getBone("light");
        if (light != null)
        {
            this.savedBones.add(new SavedBone(light.getScaleX(), "light", SavedBone.TYPES.SCX));
            this.savedBones.add(new SavedBone(light.getScaleY(), "light", SavedBone.TYPES.SCY));
            this.savedBones.add(new SavedBone(light.getScaleZ(), "light", SavedBone.TYPES.SCZ));
            if (animatable.getLightOn()) light.updateScale(3f,3f, 8.5f);
            else light.updateScale(0f,0f,0f);
        }
    }
}
