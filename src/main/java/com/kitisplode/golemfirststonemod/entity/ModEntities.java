package com.kitisplode.golemfirststonemod.entity;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.client.EntityRendererGolemFirstOak;
import com.kitisplode.golemfirststonemod.entity.client.EntityRendererGolemFirstStone;
import com.kitisplode.golemfirststonemod.entity.custom.EntityGolemFirstOak;
import com.kitisplode.golemfirststonemod.entity.custom.EntityGolemFirstStone;
import com.kitisplode.golemfirststonemod.entity.custom.EntityProjectileFirstOak;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.client.render.entity.ArrowEntityRenderer;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities
{
    public static final EntityType<EntityGolemFirstStone> ENTITY_GOLEM_FIRST_STONE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(GolemFirstStoneMod.MOD_ID, "entity_golem_first_stone"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, EntityGolemFirstStone::new)
                    .dimensions(EntityDimensions.fixed(2.5f, 4.0f))
                    .build()
    );
    public static final EntityType<EntityGolemFirstOak> ENTITY_GOLEM_FIRST_OAK = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(GolemFirstStoneMod.MOD_ID, "entity_golem_first_oak"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, EntityGolemFirstOak::new)
                    .dimensions(EntityDimensions.fixed(2.5f, 4.0f))
                    .trackRangeChunks(10)
                    .build()
    );
    public static final EntityType<EntityProjectileFirstOak> ENTITY_PROJECTILE_FIRST_OAK = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(GolemFirstStoneMod.MOD_ID, "entity_projectile_first_oak"),
            FabricEntityTypeBuilder.<EntityProjectileFirstOak>create(SpawnGroup.MISC, EntityProjectileFirstOak::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                    .trackRangeBlocks(4).trackedUpdateRate(10)
                    .build()
    );

    public static void registerModEntities()
    {
        FabricDefaultAttributeRegistry.register(ENTITY_GOLEM_FIRST_STONE, EntityGolemFirstStone.setAttributes());
        FabricDefaultAttributeRegistry.register(ENTITY_GOLEM_FIRST_OAK, EntityGolemFirstOak.setAttributes());
    }

    public static void registerModEntitiesRenderers()
    {
        EntityRendererRegistry.register(ENTITY_GOLEM_FIRST_STONE, EntityRendererGolemFirstStone::new);
        EntityRendererRegistry.register(ENTITY_GOLEM_FIRST_OAK, EntityRendererGolemFirstOak::new);
        EntityRendererRegistry.register(ENTITY_PROJECTILE_FIRST_OAK, ArrowEntityRenderer::new);
    }
}