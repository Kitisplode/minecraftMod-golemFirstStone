package com.kitisplode.golemfirststonemod.networking.packet;

import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemGrindstone;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class C2SPacketGrindstoneAttack
{
    private static final String MESSAGE_GRINDSTONE_JUMP = "message.golemfirststonemod.grindstone_jump";

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender)
    {
        // Make the grindstone attack.
        Entity vehicle = player.getControllingVehicle();
        if (vehicle instanceof EntityGolemGrindstone golem)
        {
            golem.forceAttack();
        }
    }
}
