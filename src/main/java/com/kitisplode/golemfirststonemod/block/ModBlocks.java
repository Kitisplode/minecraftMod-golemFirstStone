package com.kitisplode.golemfirststonemod.block;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.block.golem_head.BlockHeadOak;
import com.kitisplode.golemfirststonemod.block.golem_head.BlockHeadStone;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.ConstantIntProvider;

public class ModBlocks
{
    public static final Block BLOCK_HEAD_STONE = registerBlock("block_head_stone", new BlockHeadStone(FabricBlockSettings.copyOf(Blocks.STONE)));
    public static final Block BLOCK_CORE_STONE = registerBlock("block_core_stone", new ExperienceDroppingBlock(FabricBlockSettings.copyOf(Blocks.STONE), ConstantIntProvider.create(50)));
    public static final Block BLOCK_HEAD_OAK = registerBlock("block_head_oak", new BlockHeadOak(FabricBlockSettings.copyOf(Blocks.OAK_WOOD)));
    public static final Block BLOCK_CORE_OAK = registerBlock("block_core_oak", new ExperienceDroppingBlock(FabricBlockSettings.copyOf(Blocks.OAK_WOOD), ConstantIntProvider.create(50)));

    private static Block registerBlock(String pName, Block pBlock)
    {
        registerBlockItem(pName, pBlock);
        return Registry.register(Registries.BLOCK, new Identifier(GolemFirstStoneMod.MOD_ID, pName), pBlock);
    }

    private static Item registerBlockItem(String pName, Block pBlock)
    {
        return Registry.register(Registries.ITEM, new Identifier(GolemFirstStoneMod.MOD_ID, pName), new BlockItem(pBlock, new FabricItemSettings()));
    }

    public static void registerModBlocks()
    {
        GolemFirstStoneMod.LOGGER.info("Registering ModBlocks for " + GolemFirstStoneMod.MOD_ID);
    }
}
