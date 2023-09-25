package com.kitisplode.golemfirststonemod.mixin;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.passive.MooshroomEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = MooshroomEntity.class )
public interface MixinMooshroomEntityAccessor
{
    @Accessor("stewEffect")
    StatusEffect getEffect();

    @Mutable
    @Accessor("stewEffect")
    void setEffect(StatusEffect effect);

    @Accessor("stewEffectDuration")
    int getEffectDuration();

    @Mutable
    @Accessor("stewEffectDuration")
    void setEffectDuration(int duration);
}
