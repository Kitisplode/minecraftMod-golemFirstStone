package com.kitisplode.golemfirststonemod.networking.packet;

import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemGrindstone;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SPacketGrindstoneAttack
{
    public C2SPacketGrindstoneAttack() {}

    public C2SPacketGrindstoneAttack(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() ->
        {
            ServerPlayer player = context.getSender();

            // Make the grindstone attack.
            Entity vehicle = player.getControlledVehicle();
            if (vehicle instanceof EntityGolemGrindstone golem)
            {
                golem.forceAttack();
            }
        });
        return true;
    }
}
