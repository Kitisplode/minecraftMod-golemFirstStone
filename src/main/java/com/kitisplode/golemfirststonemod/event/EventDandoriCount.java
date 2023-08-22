package com.kitisplode.golemfirststonemod.event;

import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDandoriCount;
import com.kitisplode.golemfirststonemod.mixin.entity.MixinPlayerEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class EventDandoriCount implements ServerTickEvents.StartTick
{
    @Override
    public void onStartTick(MinecraftServer server)
    {
        // Recalculate each player's dandori count as necessary.
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList())
        {
            IEntityWithDandoriCount dandoriPlayer = (IEntityWithDandoriCount) player;

            // Recount the dandori if necessary.
            dandoriPlayer.recountDandori();
        }
    }
}
