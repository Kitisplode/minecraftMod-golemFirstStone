package com.kitisplode.golemfirststonemod.entity;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.client.renderer.*;
import com.kitisplode.golemfirststonemod.entity.entity.EntityVillagerDandori;
import com.kitisplode.golemfirststonemod.entity.entity.effect.EntityEffectCubeDandoriWhistle;
import com.kitisplode.golemfirststonemod.entity.entity.effect.EntityEffectShieldFirstBrick;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemFirstBrick;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemFirstDiorite;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemFirstOak;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemFirstStone;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityPawn;
import com.kitisplode.golemfirststonemod.entity.entity.projectile.EntityProjectileFirstOak;
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
    public static final EntityType<EntityGolemFirstBrick> ENTITY_GOLEM_FIRST_BRICK = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(GolemFirstStoneMod.MOD_ID, "entity_golem_first_brick"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, EntityGolemFirstBrick::new)
                    .dimensions(EntityDimensions.fixed(2.5f, 4.0f))
                    .build()
    );
    public static final EntityType<EntityEffectShieldFirstBrick> ENTITY_SHIELD_FIRST_BRICK = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(GolemFirstStoneMod.MOD_ID, "entity_shield_first_brick"),
            FabricEntityTypeBuilder.<EntityEffectShieldFirstBrick>create(SpawnGroup.MISC, EntityEffectShieldFirstBrick::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                    .build()
    );
    public static final EntityType<EntityGolemFirstDiorite> ENTITY_GOLEM_FIRST_DIORITE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(GolemFirstStoneMod.MOD_ID, "entity_golem_first_diorite"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, EntityGolemFirstDiorite::new)
                    .dimensions(EntityDimensions.fixed(2.5f, 4.0f))
                    .build()
    );
    public static final EntityType<EntityPawn> ENTITY_PAWN_FIRST_DIORITE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(GolemFirstStoneMod.MOD_ID, "entity_pawn_first_diorite"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, EntityPawn::new)
                    .dimensions(EntityDimensions.fixed(0.8f, 1.0f))
                    .build()
    );
    public static final EntityType<EntityEffectCubeDandoriWhistle> ENTITY_EFFECT_CUBE_DANDORI_WHISTLE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(GolemFirstStoneMod.MOD_ID, "entity_effect_cube_dandori_whistle"),
            FabricEntityTypeBuilder.<EntityEffectCubeDandoriWhistle>create(SpawnGroup.MISC, EntityEffectCubeDandoriWhistle::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                    .build()
    );

    public static final EntityType<EntityPawn> ENTITY_PAWN_TERRACOTTA = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(GolemFirstStoneMod.MOD_ID, "entity_pawn_terracotta"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, EntityPawn::new)
                    .dimensions(EntityDimensions.fixed(0.8f, 1.0f))
                    .build()
    );
    public static final EntityType<EntityVillagerDandori> ENTITY_VILLAGER_DANDORI = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(GolemFirstStoneMod.MOD_ID, "entity_villager_dandori"),
            FabricEntityTypeBuilder.<EntityVillagerDandori>create(SpawnGroup.MISC, EntityVillagerDandori::new)
                    .dimensions(EntityDimensions.fixed(0.8f, 1.0f))
                    .build()
    );

    public static void registerModEntities()
    {
        FabricDefaultAttributeRegistry.register(ENTITY_GOLEM_FIRST_STONE, EntityGolemFirstStone.setAttributes());
        FabricDefaultAttributeRegistry.register(ENTITY_GOLEM_FIRST_OAK, EntityGolemFirstOak.setAttributes());
        FabricDefaultAttributeRegistry.register(ENTITY_GOLEM_FIRST_BRICK, EntityGolemFirstBrick.setAttributes());
        FabricDefaultAttributeRegistry.register(ENTITY_GOLEM_FIRST_DIORITE, EntityGolemFirstDiorite.setAttributes());
        FabricDefaultAttributeRegistry.register(ENTITY_PAWN_FIRST_DIORITE, EntityPawn.setAttributes());
        FabricDefaultAttributeRegistry.register(ENTITY_PAWN_TERRACOTTA, EntityPawn.setAttributes());
        FabricDefaultAttributeRegistry.register(ENTITY_VILLAGER_DANDORI, EntityVillagerDandori.setAttributes());
    }

    public static void registerModEntitiesRenderers()
    {
        EntityRendererRegistry.register(ENTITY_GOLEM_FIRST_STONE, EntityRendererGolemFirstStone::new);
        EntityRendererRegistry.register(ENTITY_GOLEM_FIRST_OAK, EntityRendererGolemFirstOak::new);
        EntityRendererRegistry.register(ENTITY_PROJECTILE_FIRST_OAK, ArrowEntityRenderer::new);
        EntityRendererRegistry.register(ENTITY_GOLEM_FIRST_BRICK, EntityRendererGolemFirstBrick::new);
        EntityRendererRegistry.register(ENTITY_SHIELD_FIRST_BRICK, EntityRendererShieldFirstBrick::new);
        EntityRendererRegistry.register(ENTITY_GOLEM_FIRST_DIORITE, EntityRendererGolemFirstDiorite::new);
        EntityRendererRegistry.register(ENTITY_PAWN_FIRST_DIORITE, EntityRendererPawnFirstDiorite::new);
        EntityRendererRegistry.register(ENTITY_PAWN_TERRACOTTA, EntityRendererPawnFirstDiorite::new);
        EntityRendererRegistry.register(ENTITY_EFFECT_CUBE_DANDORI_WHISTLE, EntityRendererShieldFirstBrick::new);
        EntityRendererRegistry.register(ENTITY_VILLAGER_DANDORI, EntityRendererVillagerDandori::new);
    }
}