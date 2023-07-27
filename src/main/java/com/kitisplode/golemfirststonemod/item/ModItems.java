package com.kitisplode.golemfirststonemod.item;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems
{
    // Create a list of items to be registered
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, GolemFirstStoneMod.MOD_ID);

    // Called to actually register the items list.
    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }
}