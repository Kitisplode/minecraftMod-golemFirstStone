package com.kitisplode.golemfirststonemod.entity.client.model;

import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.model.GeoModel;

import java.util.ArrayList;

public abstract class EntityModelWithCustomAnimations<T extends GeoAnimatable>  extends GeoModel<T>
{
    protected ArrayList<SavedBone> savedBones = new ArrayList<>();
    public void resetCustomAnimations()
    {
        for (SavedBone bone : savedBones)
        {
            bone.setBone();
        }
        savedBones.clear();
    }

    //==================================================================================================================
    protected class SavedBone
    {
        public enum TYPES {ROTX,ROTY,ROTZ, TLX,TLY,TLZ, SCX,SCY,SCZ};
        private final float amount;
        private final String boneName;
        private final TYPES type;
        public SavedBone(float amount, String bone, TYPES type)
        {
            this.amount = amount;
            this.boneName = bone;
            this.type = type;
        }
        public void setBone()
        {
            CoreGeoBone bone = getAnimationProcessor().getBone(this.boneName);
            if (bone != null)
            {
                switch (type)
                {
                    case ROTX -> bone.setRotX(amount);
                    case ROTY -> bone.setRotY(amount);
                    case ROTZ -> bone.setRotZ(amount);
                    case TLX -> bone.setPosX(amount);
                    case TLY -> bone.setPosY(amount);
                    case TLZ -> bone.setPosZ(amount);
                    case SCX -> bone.setScaleX(amount);
                    case SCY -> bone.setScaleY(amount);
                    case SCZ -> bone.setScaleZ(amount);
                }
            }
        }
    }
}
