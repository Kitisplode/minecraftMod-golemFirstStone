package com.kitisplode.golemfirststonemod.networking.packet;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.other.EntityGolemAgent;
import com.kitisplode.golemfirststonemod.menu.InventoryMenuAgent;
import com.kitisplode.golemfirststonemod.menu.InventoryScreenAgent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class S2CPacketAgentScreenOpen
{
    private final int containerId;
    private final int size;
    private final int entityId;

    public S2CPacketAgentScreenOpen(int pContainerId, int pSize, int pEntityId)
    {
        this.containerId = pContainerId;
        this.size = pSize;
        this.entityId = pEntityId;
    }

    public S2CPacketAgentScreenOpen(FriendlyByteBuf buf)
    {
        this.containerId = buf.readUnsignedByte();
        this.size = buf.readVarInt();
        this.entityId = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf)
    {
        buf.writeByte(this.containerId);
        buf.writeVarInt(this.size);
        buf.writeInt(this.entityId);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier)
    {
        final var success = new AtomicBoolean(false);
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() ->
        {
            ClientLevel level = Minecraft.getInstance().level;
            Entity entity = level.getEntity(this.entityId);
            if (entity instanceof EntityGolemAgent agent)
            {
                LocalPlayer localPlayer = Minecraft.getInstance().player;
                SimpleContainer simpleContainer = new SimpleContainer(this.getSize());
                InventoryMenuAgent inventoryMenuAgent = new InventoryMenuAgent(this.getContainerId(), localPlayer.getInventory(), simpleContainer, agent);
                localPlayer.containerMenu = inventoryMenuAgent;
                Minecraft.getInstance().setScreen(new InventoryScreenAgent(inventoryMenuAgent, localPlayer.getInventory(), agent));
            }
            success.set(true);
        });
        supplier.get().setPacketHandled(true);
        return success.get();
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
