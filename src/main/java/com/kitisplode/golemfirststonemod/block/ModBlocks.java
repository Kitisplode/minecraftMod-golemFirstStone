package com.kitisplode.golemfirststonemod.block;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.block.golem_head.BlockHeadBrick;
import com.kitisplode.golemfirststonemod.block.golem_head.BlockHeadDiorite;
import com.kitisplode.golemfirststonemod.block.golem_head.BlockHeadOak;
import com.kitisplode.golemfirststonemod.block.golem_head.BlockHeadStone;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.client.render.RenderLayer;
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
    public static final Block BLOCK_HEAD_BRICK = registerBlock("block_head_brick", new BlockHeadBrick(FabricBlockSettings.copyOf(Blocks.EMERALD_BLOCK)));
    public static final Block BLOCK_CORE_BRICK = registerBlock("block_core_brick", new ExperienceDroppingBlock(FabricBlockSettings.copyOf(Blocks.MUD_BRICKS), ConstantIntProvider.create(50)));
    public static final Block BLOCK_HEAD_DIORITE = registerBlock("block_head_diorite", new BlockHeadDiorite(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK).nonOpaque()));
    public static final Block BLOCK_CORE_DIORITE = registerBlock("block_core_diorite", new ExperienceDroppingBlock(FabricBlockSettings.copyOf(Blocks.POLISHED_DIORITE), ConstantIntProvider.create(50)));

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

    public static void registerTransparentBlocks()
    {
        BlockRenderLayerMap.INSTANCE.putBlock(BLOCK_HEAD_DIORITE, RenderLayer.getCutout());
    }
}
