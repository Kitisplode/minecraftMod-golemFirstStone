package com.kitisplode.golemfirststonemod.entity;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.custom.EntityGolemFirstStone;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities
{
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, GolemFirstStoneMod.MOD_ID);

    public static final RegistryObject<EntityType<EntityGolemFirstStone>> ENTITY_GOLEM_FIRST_STONE =
            ENTITY_TYPES.register("entity_golem_first_stone",
                    () -> EntityType.Builder.of(EntityGolemFirstStone::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .build(new ResourceLocation(GolemFirstStoneMod.MOD_ID, "entity_golem_first_stone").toString()));

    public static void register(IEventBus eventBus)
    {
        ENTITY_TYPES.register(eventBus);
    }
}
