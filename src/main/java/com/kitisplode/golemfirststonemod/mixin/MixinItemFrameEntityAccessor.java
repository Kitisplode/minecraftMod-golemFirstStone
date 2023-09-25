package com.kitisplode.golemfirststonemod.mixin;

import net.minecraft.entity.decoration.ItemFrameEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ItemFrameEntity.class)
public interface MixinItemFrameEntityAccessor
{
    @Accessor("fixed")
    boolean getFixed();
}
