package com.kitisplode.golemfirststonemod.entity.entity.interfaces;

import net.minecraft.entity.ItemEntity;

public interface IEntityTargetsItems
{
    ItemEntity getItemTarget();
    void setItemTarget(ItemEntity itemEntity);
    default boolean canTargetItem(ItemEntity itemEntity)
    {
        return true;
    }
    void loot(ItemEntity item);

}
