package com.kitisplode.golemfirststonemod.mixin;

import net.minecraft.world.entity.decoration.ItemFrame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ItemFrame.class)
public interface MixinItemFrameAccessor
{
    @Accessor("fixed")
    boolean getFixed();
}
