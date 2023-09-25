package com.kitisplode.golemfirststonemod.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ShovelItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value= ShovelItem.class)
public interface MixinShovelItemAccessor
{
    @Accessor("PATH_STATES")
    static Map<Block, BlockState> getPathStates()
    {
        throw new AssertionError();
    }
}
