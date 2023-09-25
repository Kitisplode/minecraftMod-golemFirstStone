package com.kitisplode.golemfirststonemod.networking;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.networking.packet.C2SPacketGrindstoneAttack;
import com.kitisplode.golemfirststonemod.networking.packet.C2SPacketItemSwingUse;
import com.kitisplode.golemfirststonemod.networking.packet.C2SPacketItemSwingUseTick;
import com.kitisplode.golemfirststonemod.networking.packet.S2CPacketAgentScreenOpen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class ModMessages
{
    public static final Identifier GRINDSTONE_JUMP_ID = new Identifier(GolemFirstStoneMod.MOD_ID, "grindstone_jump");
    public static final Identifier ITEM_SWING_USE = new Identifier(GolemFirstStoneMod.MOD_ID, "item_swing_use");
    public static final Identifier ITEM_SWING_USE_TICK = new Identifier(GolemFirstStoneMod.MOD_ID, "item_swing_use_tick");
    public static final Identifier SCREEN_AGENT_OPEN = new Identifier(GolemFirstStoneMod.MOD_ID, "item_screen_agent_open");

    public static void registerC2SPackets()
    {
        ServerPlayNetworking.registerGlobalReceiver(GRINDSTONE_JUMP_ID, C2SPacketGrindstoneAttack::receive);
        ServerPlayNetworking.registerGlobalReceiver(ITEM_SWING_USE, C2SPacketItemSwingUse::receive);
        ServerPlayNetworking.registerGlobalReceiver(ITEM_SWING_USE_TICK, C2SPacketItemSwingUseTick::receive);
    }

    public static void registerS2CPackets()
    {
        ClientPlayNetworking.registerGlobalReceiver(SCREEN_AGENT_OPEN, S2CPacketAgentScreenOpen::receive);
    }
}
