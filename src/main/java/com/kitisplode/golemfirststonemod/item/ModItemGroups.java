package com.kitisplode.golemfirststonemod.item;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.block.ModBlocks;
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
            new Identifier(GolemFirstStoneMod.MOD_ID, "itemgroup_test"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemgroup.itemgroup_test"))
                    .icon( () -> new ItemStack(ModBlocks.BLOCK_HEAD_STONE))
                    .entries((displayContext, entries) -> {
                        entries.add(ModBlocks.BLOCK_HEAD_STONE);
                        entries.add(ModBlocks.BLOCK_CORE_STONE);
                        entries.add(ModBlocks.BLOCK_HEAD_OAK);
                        entries.add(ModBlocks.BLOCK_CORE_OAK);
                    }).build());

    public static void registerItemGroups()
    {
        GolemFirstStoneMod.LOGGER.info("Registering Item Groups for " + GolemFirstStoneMod.MOD_ID);
    }
}
