package com.kitisplode.golemfirststonemod.entity.entity.interfaces;

public interface IEntitySummoner
{
    int getSummonState();

    void setSummonState(int pInt);

    boolean trySummon(int summonState);

    default boolean isReadyToSummon()
    {
        return true;
    }
}
