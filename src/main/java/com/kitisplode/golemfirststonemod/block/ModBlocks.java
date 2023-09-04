package com.kitisplode.golemfirststonemod.block;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.block.golem_head.BlockHeadBrick;
import com.kitisplode.golemfirststonemod.block.golem_head.BlockHeadDiorite;
import com.kitisplode.golemfirststonemod.block.golem_head.BlockHeadOak;
import com.kitisplode.golemfirststonemod.block.golem_head.BlockHeadStone;
import com.kitisplode.golemfirststonemod.item.ModItems;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.material.PushReaction;
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
    public static final RegistryObject<Block> BLOCK_HEAD_OAK = registerBlock("block_head_oak",
            () -> new BlockHeadOak(BlockBehaviour.Properties.copy(Blocks.OAK_WOOD)));
    public static final RegistryObject<Block> BLOCK_CORE_OAK = registerBlock("block_core_oak",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WOOD), ConstantInt.of(50)));
    public static final RegistryObject<Block> BLOCK_HEAD_BRICK = registerBlock("block_head_brick",
            () -> new BlockHeadBrick(BlockBehaviour.Properties.copy(Blocks.EMERALD_BLOCK)));
    public static final RegistryObject<Block> BLOCK_CORE_BRICK = registerBlock("block_core_brick",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.MUD_BRICKS), ConstantInt.of(50)));
    public static final RegistryObject<Block> BLOCK_HEAD_DIORITE = registerBlock("block_head_diorite",
            () -> new BlockHeadDiorite(BlockBehaviour.Properties.copy(Blocks.GOLD_BLOCK).noOcclusion()));
    public static final RegistryObject<Block> BLOCK_CORE_DIORITE = registerBlock("block_core_diorite",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.POLISHED_DIORITE), ConstantInt.of(50)));
    public static final RegistryObject<Block> BLOCK_VILLAGER_STONE = registerBlock("block_villager_stone",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.STONE)));
    public static final RegistryObject<Block> BLOCK_VILLAGER_OAK = registerBlock("block_villager_oak",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)));
    public static final RegistryObject<Block> BLOCK_VILLAGER_BRICK = registerBlock("block_villager_brick",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.MUD_BRICKS)));
    public static final RegistryObject<Block> BLOCK_VILLAGER_DIORITE = registerBlock("block_villager_diorite",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIORITE)));
    public static final RegistryObject<Block> BLOCK_BUTTON_COPPER = registerBlock("block_button_copper",
            ModBlocks::copperButton);


    private static ButtonBlock copperButton() {
        return new ButtonBlock(BlockBehaviour.Properties.of().noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY), BlockSetType.STONE, 10, false);
    }

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

    public static void registerRenderLayers()
    {
        ItemBlockRenderTypes.setRenderLayer(ModBlocks.BLOCK_HEAD_DIORITE.get(), RenderType.cutout());
    }
}
