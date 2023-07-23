package com.kitisplode.fabricplayground.item;

import com.kitisplode.fabricplayground.FabricPlaygroundMod;
import com.kitisplode.fabricplayground.item.custom.ItemWordLife;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems
{
    public static final Item ITEM_TEST = registerItem("item_test", new ItemWordLife(new FabricItemSettings().maxCount(1)));

    private static Item registerItem(String pName, Item pItem)
    {
        return Registry.register(Registries.ITEM, new Identifier(FabricPlaygroundMod.MOD_ID, pName), pItem);
    }

    public static void registerModItems()
    {
        FabricPlaygroundMod.LOGGER.info("Registering Mod Items for " + FabricPlaygroundMod.MOD_ID);
    }
}
