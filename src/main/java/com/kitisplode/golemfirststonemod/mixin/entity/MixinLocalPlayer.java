package com.kitisplode.golemfirststonemod.mixin.entity;

import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemGrindstone;
import com.kitisplode.golemfirststonemod.networking.ModMessages;
import com.kitisplode.golemfirststonemod.networking.packet.C2SPacketGrindstoneAttack;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.lwjgl.system.CallbackI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LocalPlayer.class)
public abstract class MixinLocalPlayer extends AbstractClientPlayer
{
    public MixinLocalPlayer(ClientLevel pClientLevel, GameProfile pGameProfile)
    {
        super(pClientLevel, pGameProfile);
    }

    @Shadow
    public Input input;

    @Inject(method = ("aiStep"), at = @At("tail"))
    protected void inject_aiStep(CallbackInfo ci)
    {
        Entity vehicle = this.getControlledVehicle();
        if (this.input.jumping)
        {
            if (vehicle instanceof EntityGolemGrindstone golem)
            {
                ModMessages.sendToServer(new C2SPacketGrindstoneAttack());
            }
        }
    }
}
