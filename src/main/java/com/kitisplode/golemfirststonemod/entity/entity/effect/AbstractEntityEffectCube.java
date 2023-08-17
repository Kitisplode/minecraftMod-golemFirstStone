package com.kitisplode.golemfirststonemod.entity.entity.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;

abstract public class AbstractEntityEffectCube extends Entity implements GeoEntity
{
    protected float scaleH = 1.0f;
    protected float scaleY = 1.0f;

    public AbstractEntityEffectCube(EntityType<?> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
    }

    public float getScaleH()
    {
        return this.scaleH;
    }

    public float getScaleY()
    {
        return this.scaleY;
    }

    abstract public ResourceLocation getTexture();
}
