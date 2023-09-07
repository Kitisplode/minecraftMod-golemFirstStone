package com.kitisplode.golemfirststonemod.networking.packet;

import com.kitisplode.golemfirststonemod.item.item.IItemSwingUse;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

public class C2SPacketItemSwingUseTick
{
    private static final String MESSAGE_ITEM_SWING_USE_TICK = "message.golemfirststonemod.item_swing_use_tick";

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender)
    {
        ItemStack itemStack = player.getStackInHand(Hand.MAIN_HAND);
        if (itemStack.getItem() instanceof IItemSwingUse item)
        {
            item.swingTick(player);
        }
    }
}
