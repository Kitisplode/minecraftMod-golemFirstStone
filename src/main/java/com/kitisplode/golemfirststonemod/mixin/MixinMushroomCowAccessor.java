package com.kitisplode.golemfirststonemod.mixin;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.animal.MushroomCow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;


@Mixin(value = MushroomCow.class)
public interface MixinMushroomCowAccessor
{
    @Accessor("effect")
    MobEffect getEffect();

    @Mutable
    @Accessor("effect")
    void setEffect(MobEffect effect);

    @Accessor("effectDuration")
    int getEffectDuration();

    @Mutable
    @Accessor("effectDuration")
    void setEffectDuration(int duration);
}
