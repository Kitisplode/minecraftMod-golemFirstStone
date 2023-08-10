package com.kitisplode.golemfirststonemod.event;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemFirstBrick;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemFirstDiorite;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemFirstOak;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemFirstStone;
import com.kitisplode.golemfirststonemod.entity.entity.golem.pawn.EntityPawnFirstDiorite;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GolemFirstStoneMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents
{
    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent event)
    {
        event.put(ModEntities.ENTITY_GOLEM_FIRST_STONE.get(), EntityGolemFirstStone.setAttributes());
        event.put(ModEntities.ENTITY_GOLEM_FIRST_OAK.get(), EntityGolemFirstOak.setAttributes());
        event.put(ModEntities.ENTITY_GOLEM_FIRST_BRICK.get(), EntityGolemFirstBrick.setAttributes());
        event.put(ModEntities.ENTITY_GOLEM_FIRST_DIORITE.get(), EntityGolemFirstDiorite.setAttributes());
        event.put(ModEntities.ENTITY_PAWN_FIRST_DIORITE.get(), EntityPawnFirstDiorite.setAttributes());
    }
}
