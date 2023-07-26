package com.kitisplode.golemfirststonemod.item;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems
{
    private static Item registerItem(String pName, Item pItem)
    {
        return Registry.register(Registries.ITEM, new Identifier(GolemFirstStoneMod.MOD_ID, pName), pItem);
    }

    public static void registerModItems()
    {
        GolemFirstStoneMod.LOGGER.info("Registering Mod Items for " + GolemFirstStoneMod.MOD_ID);
    }
}
