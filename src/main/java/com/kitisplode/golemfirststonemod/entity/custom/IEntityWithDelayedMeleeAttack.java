package com.kitisplode.golemfirststonemod.entity.custom;


public interface IEntityWithDelayedMeleeAttack
{
    public int getAttackState();

    public void setAttackState(int pInt);

    public boolean tryAttack();

}
