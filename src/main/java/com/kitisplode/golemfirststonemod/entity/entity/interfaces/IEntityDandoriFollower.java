package com.kitisplode.golemfirststonemod.entity.entity.interfaces;

import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;

public interface IEntityDandoriFollower
{
    byte ENTITY_EVENT_DANDORI_START = 8;

    public boolean getDandoriState();

    public void setDandoriState(boolean pDandoriState);

    public LivingEntity getOwner();

    public void setOwner(LivingEntity newOwner);

    public boolean isOwner(LivingEntity entity);
}
