package com.kitisplode.golemfirststonemod.entity.entity.interfaces;

import net.minecraft.world.entity.LivingEntity;

public interface IEntityDandoriFollower
{
    byte ENTITY_EVENT_DANDORI_START = 8;

    boolean getDandoriState();

    void setDandoriState(boolean pDandoriState);

    LivingEntity getOwner();

    void setOwner(LivingEntity newOwner);

    boolean isOwner(LivingEntity entity);

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
}
