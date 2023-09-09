package com.kitisplode.golemfirststonemod.networking.packet;

import com.kitisplode.golemfirststonemod.item.item.IItemSwingUse;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SPacketItemSwingUseTick
{
    public C2SPacketItemSwingUseTick() {}

    public C2SPacketItemSwingUseTick(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> supplier)
    {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() ->
        {
            ServerPlayer player = context.getSender();

            ItemStack itemStack = player.getItemInHand(InteractionHand.MAIN_HAND.MAIN_HAND);
            if (itemStack.getItem() instanceof IItemSwingUse item)
            {
                if (!player.getCooldowns().isOnCooldown(itemStack.getItem()))
                    item.swingTick(player);
            }
        });
        return true;
    }
}
