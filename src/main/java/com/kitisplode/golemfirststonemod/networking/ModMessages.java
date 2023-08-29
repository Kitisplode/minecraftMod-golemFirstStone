package com.kitisplode.golemfirststonemod.networking;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.networking.packet.C2SPacketGrindstoneAttack;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class ModMessages
{
    public static final Identifier GRINDSTONE_JUMP_ID = new Identifier(GolemFirstStoneMod.MOD_ID, "grindstone_jump");

    public static void registerC2SPackets()
    {
        ServerPlayNetworking.registerGlobalReceiver(GRINDSTONE_JUMP_ID, C2SPacketGrindstoneAttack::receive);
    }

    public static void registerS2CPackets()
    {

    }
}
