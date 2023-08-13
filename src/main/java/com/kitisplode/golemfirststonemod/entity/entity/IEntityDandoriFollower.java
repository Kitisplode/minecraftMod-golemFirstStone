package com.kitisplode.golemfirststonemod.entity.entity;

import net.minecraft.entity.LivingEntity;

public interface IEntityDandoriFollower
{
    public boolean getDandoriState();

    public void setDandoriState(boolean pDandoriState);

    public LivingEntity getOwner();

    public void setOwner(LivingEntity newOwner);

    public boolean isOwner(LivingEntity entity);
}
