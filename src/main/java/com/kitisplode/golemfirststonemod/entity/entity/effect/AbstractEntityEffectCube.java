package com.kitisplode.golemfirststonemod.entity.entity.effect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;

abstract public class AbstractEntityEffectCube extends Entity implements GeoEntity
{
    protected float scaleH = 1.0f;
    protected float scaleY = 1.0f;


    public AbstractEntityEffectCube(EntityType<?> type, World world)
    {
        super(type, world);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt)
    {
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt)
    {
    }

    public float getScaleH()
    {
        return this.scaleH;
    }

    public float getScaleY()
    {
        return this.scaleY;
    }

    abstract public Identifier getTexture();
}
