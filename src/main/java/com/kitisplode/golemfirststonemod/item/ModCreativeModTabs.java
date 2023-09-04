package com.kitisplode.golemfirststonemod.item;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModTabs
{
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, GolemFirstStoneMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> MOD_PLAYGROUND_TAB = CREATIVE_MODE_TABS.register("golemfirststonemod",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.BLOCK_HEAD_STONE.get()))  // Set the tab's icon
                    .title(Component.translatable("creativetab.golemfirststonemod"))        // Fetch the tab name translation
                    .displayItems((pParameters, pOutput) -> {                                    // List included items
                        // Blocks
                        pOutput.accept(ModBlocks.BLOCK_HEAD_STONE.get());
                        pOutput.accept(ModBlocks.BLOCK_CORE_STONE.get());
                        pOutput.accept(ModBlocks.BLOCK_HEAD_OAK.get());
                        pOutput.accept(ModBlocks.BLOCK_CORE_OAK.get());
                        pOutput.accept(ModBlocks.BLOCK_HEAD_BRICK.get());
                        pOutput.accept(ModBlocks.BLOCK_CORE_BRICK.get());
                        pOutput.accept(ModBlocks.BLOCK_HEAD_DIORITE.get());
                        pOutput.accept(ModBlocks.BLOCK_CORE_DIORITE.get());
                        pOutput.accept(ModBlocks.BLOCK_VILLAGER_STONE.get());
                        pOutput.accept(ModBlocks.BLOCK_VILLAGER_OAK.get());
                        pOutput.accept(ModBlocks.BLOCK_VILLAGER_BRICK.get());
                        pOutput.accept(ModBlocks.BLOCK_VILLAGER_DIORITE.get());
                        pOutput.accept(ModBlocks.BLOCK_BUTTON_COPPER.get());
                        // Items
//                        pOutput.accept(ModItems.ITEM_SPAWN_PAWN_FIRST_DIORITE.get());
//                        pOutput.accept(ModItems.ITEM_SPAWN_VILLAGER_DANDORI.get());
                        pOutput.accept(ModItems.ITEM_DANDORI_CALL.get());
                        pOutput.accept(ModItems.ITEM_DANDORI_ATTACK.get());
                        pOutput.accept(ModItems.ITEM_DANDORI_DIG.get());
                        pOutput.accept(ModItems.ITEM_DANDORI_THROW.get());

                    })
                    .build());

    public static void register(IEventBus eventBus)
    {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}