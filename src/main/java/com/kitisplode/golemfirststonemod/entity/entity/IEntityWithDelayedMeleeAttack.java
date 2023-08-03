package com.kitisplode.golemfirststonemod.entity.entity;


public interface IEntityWithDelayedMeleeAttack
{
    public int getAttackState();

    public void setAttackState(int pInt);

    public boolean tryAttack();

}
