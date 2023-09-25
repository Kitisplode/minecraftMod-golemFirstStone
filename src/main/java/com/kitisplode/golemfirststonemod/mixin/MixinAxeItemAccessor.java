package com.kitisplode.golemfirststonemod.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.item.AxeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

@Mixin(value= AxeItem.class)
public interface MixinAxeItemAccessor
{
    @Invoker("getStrippedState")
    Optional<BlockState> invoke_getStrippedState(BlockState state);
}
