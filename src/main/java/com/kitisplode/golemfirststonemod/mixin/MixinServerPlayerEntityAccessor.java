package com.kitisplode.golemfirststonemod.mixin;

import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = ServerPlayerEntity.class)
public interface MixinServerPlayerEntityAccessor
{
    @Invoker("incrementScreenHandlerSyncId")
    void invoke_incrementScreenHandlerSyncId();

    @Invoker("onScreenHandlerOpened")
    void invoke_onScreenHandlerOpened(ScreenHandler screenHandler);

    @Accessor("screenHandlerSyncId")
    int getScreenHandlerSyncId();
}
