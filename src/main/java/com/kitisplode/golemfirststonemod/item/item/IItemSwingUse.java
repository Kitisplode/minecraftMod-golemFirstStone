package com.kitisplode.golemfirststonemod.item.item;

import net.minecraft.entity.player.PlayerEntity;

public interface IItemSwingUse
{
    default void swing(PlayerEntity player) {}

    default void swingTick(PlayerEntity player) {}
}
