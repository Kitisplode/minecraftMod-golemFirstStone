package com.kitisplode.golemfirststonemod.mixin.entity;

import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemGrindstone;
import com.kitisplode.golemfirststonemod.networking.ModMessages;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity
{

    public MixinClientPlayerEntity(ClientWorld world, GameProfile profile)
    {
        super(world, profile);
    }

    @Shadow
    public Input input;

    @Inject(method = ("tickMovement"), at = @At("tail"))
    protected void inject_tickMovement(CallbackInfo ci)
    {
        Entity vehicle = this.getControllingVehicle();
        if (this.input.jumping)
        {
            if (vehicle instanceof EntityGolemGrindstone golem)
            {
                ClientPlayNetworking.send(ModMessages.GRINDSTONE_JUMP_ID, PacketByteBufs.create());
            }
        }
    }
}
