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
}
