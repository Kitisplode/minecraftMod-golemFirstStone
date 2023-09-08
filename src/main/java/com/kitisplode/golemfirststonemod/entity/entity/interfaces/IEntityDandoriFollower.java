package com.kitisplode.golemfirststonemod.entity.entity.interfaces;

import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IEntityDandoriFollower
{
    byte ENTITY_EVENT_DANDORI_START = 8;

    public boolean getDandoriState();

    public void setDandoriState(boolean pDandoriState);

    public LivingEntity getOwner();

    public void setOwner(LivingEntity newOwner);

    public boolean isImmobile();

    default boolean isThrowable()
    {
        return false;
    }

    default boolean getThrown()
    {
        return false;
    }

    default void setThrown(boolean pThrown)
    {
        return;
    }

    default float getThrowAngle()
    {
        return 0.0f;
    }

    void setDeployPosition(BlockPos bp);
    BlockPos getDeployPosition();
}
