package com.kitisplode.golemfirststonemod.event;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.client.HudDandoriCount;
import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDandoriCount;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GolemFirstStoneMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents
{
    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent event)
    {
        ModEntities.registerAttributes(event);
    }
}
