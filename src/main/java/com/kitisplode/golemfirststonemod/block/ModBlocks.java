package com.kitisplode.golemfirststonemod.block;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.block.custom.BlockHeadStone;
import com.kitisplode.golemfirststonemod.item.ModItems;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks
{
    // Set up the list for blocks to be registered.
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, GolemFirstStoneMod.MOD_ID);

    public static final RegistryObject<Block> BLOCK_HEAD_STONE = registerBlock("block_head_stone",
            () -> new BlockHeadStone(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> BLOCK_CORE_STONE = registerBlock("block_core_stone",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.STONE), ConstantInt.of(50)));


    // Private helper method to register each of the blocks' corresponding block items.
    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block)
    {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    // Private helper method to register a block's corresponding item.
    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block)
    {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    // Called for registering the blocks.
    public static void register(IEventBus eventBus)
    {
        BLOCKS.register(eventBus);
    }
}
