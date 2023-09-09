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
            new Identifier(GolemFirstStoneMod.MOD_ID, "golemfirststonemod"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("creativetab.golemfirststonemod"))
                    .icon( () -> new ItemStack(ModItems.ITEM_DANDORI_BANNER))
                    .entries((displayContext, entries) -> {
                        entries.add(ModBlocks.BLOCK_HEAD_STONE);
                        entries.add(ModBlocks.BLOCK_CORE_STONE);
                        entries.add(ModBlocks.BLOCK_HEAD_OAK);
                        entries.add(ModBlocks.BLOCK_CORE_OAK);
                        entries.add(ModBlocks.BLOCK_HEAD_BRICK);
                        entries.add(ModBlocks.BLOCK_CORE_BRICK);
                        entries.add(ModBlocks.BLOCK_HEAD_DIORITE);
                        entries.add(ModBlocks.BLOCK_CORE_DIORITE);
                        entries.add(ModBlocks.BLOCK_VILLAGER_STONE);
                        entries.add(ModBlocks.BLOCK_VILLAGER_OAK);
                        entries.add(ModBlocks.BLOCK_VILLAGER_BRICK);
                        entries.add(ModBlocks.BLOCK_VILLAGER_DIORITE);
                        entries.add(ModBlocks.BLOCK_BUTTON_COPPER);
//                        entries.add(ModBlocks.BLOCK_DANDORI);
//                        entries.add(ModItems.ITEM_SPAWN_PAWN_FIRST_DIORITE);
//                        entries.add(ModItems.ITEM_SPAWN_VILLAGER_DANDORI);
                        entries.add(ModItems.ITEM_SPAWN_GOLEM_COPPER);
                        entries.add(ModItems.ITEM_SPAWN_GOLEM_TUFF);
                        entries.add(ModItems.ITEM_SPAWN_GOLEM_COBBLE);
                        entries.add(ModItems.ITEM_SPAWN_GOLEM_PLANK);
                        entries.add(ModItems.ITEM_SPAWN_GOLEM_MOSSY);
                        entries.add(ModItems.ITEM_SPAWN_GOLEM_GRINDSTONE);

                        entries.add(ModItems.ITEM_DANDORI_CALL);
                        entries.add(ModItems.ITEM_DANDORI_BANNER);
                        entries.add(ModItems.ITEM_DANDORI_ATTACK);
                        entries.add(ModItems.ITEM_DANDORI_DIG);
                        entries.add(ModItems.ITEM_DANDORI_THROW);
                    }).build());

    public static void registerItemGroups()
    {
        GolemFirstStoneMod.LOGGER.info("Registering Item Groups for " + GolemFirstStoneMod.MOD_ID);
    }
}
