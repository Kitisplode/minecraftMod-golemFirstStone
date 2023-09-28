package com.kitisplode.golemfirststonemod.block;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.block.entity.BlockEntityKeyLock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities
{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, GolemFirstStoneMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<BlockEntityKeyLock>> BLOCK_KEY_LOCK =
            BLOCK_ENTITIES.register("block_key_lock", () ->
                    BlockEntityType.Builder.of(BlockEntityKeyLock::new,
                            ModBlocks.BLOCK_KEY_LOCK.get()).build(null));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
