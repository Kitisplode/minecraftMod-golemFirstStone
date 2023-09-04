package com.kitisplode.golemfirststonemod.block;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.block.golem_head.BlockHeadBrick;
import com.kitisplode.golemfirststonemod.block.golem_head.BlockHeadDiorite;
import com.kitisplode.golemfirststonemod.block.golem_head.BlockHeadOak;
import com.kitisplode.golemfirststonemod.block.golem_head.BlockHeadStone;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlag;
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
    public static final Block BLOCK_DANDORI = registerBlock("block_dandori", new Block(FabricBlockSettings.copyOf(Blocks.CARTOGRAPHY_TABLE)));
    public static final Block BLOCK_VILLAGER_STONE = registerBlock("block_villager_stone", new Block(FabricBlockSettings.copyOf(Blocks.STONE)));
    public static final Block BLOCK_VILLAGER_OAK = registerBlock("block_villager_oak", new Block(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS)));
    public static final Block BLOCK_VILLAGER_BRICK = registerBlock("block_villager_brick", new Block(FabricBlockSettings.copyOf(Blocks.MUD_BRICKS)));
    public static final Block BLOCK_VILLAGER_DIORITE = registerBlock("block_villager_diorite", new Block(FabricBlockSettings.copyOf(Blocks.DIORITE)));

    public static final Block BLOCK_BUTTON_COPPER = registerBlock("block_button_copper", createCopperButtonBlock());

    public static ButtonBlock createCopperButtonBlock() {
        return new ButtonBlock(AbstractBlock.Settings.create().noCollision().strength(0.5f).pistonBehavior(PistonBehavior.DESTROY), BlockSetType.STONE, 10, false);
    }

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
