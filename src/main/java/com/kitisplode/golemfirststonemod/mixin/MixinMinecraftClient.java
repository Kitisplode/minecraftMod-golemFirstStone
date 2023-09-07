package com.kitisplode.golemfirststonemod.mixin;

import com.kitisplode.golemfirststonemod.item.item.IItemSwingUse;
import com.kitisplode.golemfirststonemod.networking.ModMessages;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient
{
    @Shadow
    public HitResult crosshairTarget;

    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Inject(method = "doAttack()Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;", shift = At.Shift.BY, by = 3),
            cancellable = true)
    private void inject_doAttack_swingDandoriItem(CallbackInfoReturnable<Boolean> cir)
    {
        if (this.player == null) return;
        ItemStack itemStack = this.player.getStackInHand(Hand.MAIN_HAND);
        if (itemStack.getItem() instanceof IItemSwingUse itemDandori)
        {
            ClientPlayNetworking.send(ModMessages.ITEM_SWING_USE, PacketByteBufs.create());
            itemDandori.swing(this.player);
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "handleBlockBreaking(Z)V",
            at = @At("HEAD"),
            cancellable = true)
    private void inject_handleBlockBreaking_swingDandoriItem(boolean breaking, CallbackInfo ci)
    {
        if (!breaking) return;
        if (this.player == null) return;
        ItemStack itemStack = this.player.getStackInHand(Hand.MAIN_HAND);
        if (itemStack.getItem() instanceof IItemSwingUse itemDandori)
        {
            ClientPlayNetworking.send(ModMessages.ITEM_SWING_USE_TICK, PacketByteBufs.create());
            itemDandori.swingTick(this.player);
            ci.cancel();
        }
    }
}
