package com.kitisplode.golemfirststonemod.networking.packet;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.other.EntityGolemAgent;
import com.kitisplode.golemfirststonemod.item.item.IItemSwingUse;
import com.kitisplode.golemfirststonemod.menu.InventoryMenuAgent;
import com.kitisplode.golemfirststonemod.menu.InventoryScreenAgent;
import com.kitisplode.golemfirststonemod.networking.ModMessages;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public class S2CPacketAgentScreenOpen implements FabricPacket
{
  	public static final PacketType<S2CPacketAgentScreenOpen> TYPE = PacketType.create(ModMessages.SCREEN_AGENT_OPEN, S2CPacketAgentScreenOpen::new);
    private final int containerId;
    private final int size;
    private final int entityId;

    public S2CPacketAgentScreenOpen(int pContainerId, int pSize, int pEntityId)
    {
        this.containerId = pContainerId;
        this.size = pSize;
        this.entityId = pEntityId;
    }

    public S2CPacketAgentScreenOpen(PacketByteBuf buf) {
        this.containerId = buf.readUnsignedByte();
        this.size = buf.readVarInt();
        this.entityId = buf.readInt();
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeByte(this.containerId);
        buf.writeVarInt(this.size);
        buf.writeInt(this.entityId);
    }

    @Override
    public PacketType<?> getType()
    {
        return TYPE;
    }

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender)
    {
        S2CPacketAgentScreenOpen packet = new S2CPacketAgentScreenOpen(buf);
        client.execute(() ->
        {
            ClientWorld level = client.world;
            ClientPlayerEntity localPlayer = client.player;
            if (level == null || localPlayer == null) return;
            Entity entity = level.getEntityById(packet.getEntityId());
            if (entity instanceof EntityGolemAgent agent)
            {
                SimpleInventory simpleContainer = new SimpleInventory(packet.getSize());
                InventoryMenuAgent inventoryMenuAgent = new InventoryMenuAgent(packet.getContainerId(), localPlayer.getInventory(), simpleContainer, agent);
                localPlayer.currentScreenHandler = inventoryMenuAgent;
                MinecraftClient.getInstance().setScreen(new InventoryScreenAgent(inventoryMenuAgent, localPlayer.getInventory(), agent));
            }
        });
    }

    public int getContainerId() {
        return this.containerId;
    }

    public int getSize() {
        return this.size;
    }

    public int getEntityId() {
        return this.entityId;
    }
}
