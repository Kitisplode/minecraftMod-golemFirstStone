package com.kitisplode.fabricplayground.item;

import com.kitisplode.fabricplayground.FabricPlaygroundMod;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups
{
    public static final ItemGroup ITEMGROUP_TEST = Registry.register(Registries.ITEM_GROUP,
            new Identifier(FabricPlaygroundMod.MOD_ID, "itemgroup_test"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemgroup.itemgroup_test"))
                    .icon( () -> new ItemStack(ModItems.ITEM_TEST))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.ITEM_TEST);
                    }).build());

    public static void registerItemGroups()
    {
        FabricPlaygroundMod.LOGGER.info("Registering Item Groups for " + FabricPlaygroundMod.MOD_ID);
    }
}
