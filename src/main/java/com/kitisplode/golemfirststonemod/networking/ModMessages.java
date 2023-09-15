package com.kitisplode.golemfirststonemod.networking;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.networking.packet.C2SPacketGrindstoneAttack;
import com.kitisplode.golemfirststonemod.networking.packet.C2SPacketItemSwingUse;
import com.kitisplode.golemfirststonemod.networking.packet.C2SPacketItemSwingUseTick;
import com.kitisplode.golemfirststonemod.networking.packet.S2CPacketAgentScreenOpen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages
{
    private static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id()
    {
        return packetId++;
    }

    public static void register()
    {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(GolemFirstStoneMod.MOD_ID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(C2SPacketGrindstoneAttack.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(C2SPacketGrindstoneAttack::new)
                .encoder(C2SPacketGrindstoneAttack::toBytes)
                .consumerMainThread(C2SPacketGrindstoneAttack::handle)
                .add();


        net.messageBuilder(C2SPacketItemSwingUse.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(C2SPacketItemSwingUse::new)
                .encoder(C2SPacketItemSwingUse::toBytes)
                .consumerMainThread(C2SPacketItemSwingUse::handle)
                .add();


        net.messageBuilder(C2SPacketItemSwingUseTick.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(C2SPacketItemSwingUseTick::new)
                .encoder(C2SPacketItemSwingUseTick::toBytes)
                .consumerMainThread(C2SPacketItemSwingUseTick::handle)
                .add();

        net.messageBuilder(S2CPacketAgentScreenOpen.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(S2CPacketAgentScreenOpen::new)
                .encoder(S2CPacketAgentScreenOpen::toBytes)
                .consumerMainThread(S2CPacketAgentScreenOpen::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message)
    {
        INSTANCE.sendToServer(message);
    }
    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player)
    {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

}
