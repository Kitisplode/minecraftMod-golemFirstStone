package com.kitisplode.golemfirststonemod.item;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.ModEntities;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems
{
    // Create a list of items to be registered
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GolemFirstStoneMod.MOD_ID);

    public static final RegistryObject<Item> ITEM_SPAWN_PAWN_FIRST_DIORITE = ITEMS.register("item_spawn_pawn_first_diorite",
            () -> new ForgeSpawnEggItem(ModEntities.ENTITY_PAWN_FIRST_DIORITE, 0xDFDFDF, 0xEBEBEB,
                    new Item.Properties()));

    // Called to actually register the items list.
    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }
}