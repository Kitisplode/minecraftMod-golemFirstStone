package com.kitisplode.golemfirststonemod.event;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDandoriCount;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CommonEvents
{
    @SubscribeEvent
    public static void onServerTickEvent (TickEvent.ServerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START) return;
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers())
        {
            IEntityWithDandoriCount dandoriPlayer = (IEntityWithDandoriCount) player;
            dandoriPlayer.recountDandori();
        }
    }
}
