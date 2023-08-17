package com.kitisplode.golemfirststonemod.entity.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface IEntityDandoriFollower
{
    byte ENTITY_EVENT_DANDORI_START = 8;

    boolean getDandoriState();

    void setDandoriState(boolean pDandoriState);

    LivingEntity getOwner();

    void setOwner(LivingEntity newOwner);

    boolean isOwner(LivingEntity entity);

    Level level();

    double getX();
    double getY();
    double getZ();

    default void addDandoriParticles()
    {
        level().addParticle(ParticleTypes.NOTE,
                getX(), getY(), getZ(),
                0,1,0);
    }
}
