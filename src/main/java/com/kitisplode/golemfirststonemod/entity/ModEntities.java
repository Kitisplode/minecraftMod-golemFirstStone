package com.kitisplode.golemfirststonemod.entity;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.client.renderer.*;
import com.kitisplode.golemfirststonemod.entity.entity.EntityVillagerDandori;
import com.kitisplode.golemfirststonemod.entity.entity.effect.EntityEffectCubeDandoriWhistle;
import com.kitisplode.golemfirststonemod.entity.entity.effect.EntityEffectShieldFirstBrick;
import com.kitisplode.golemfirststonemod.entity.entity.effect.EntitySoundRepeated;
import com.kitisplode.golemfirststonemod.entity.entity.golem.*;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.EntityGolemFirstBrick;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.EntityGolemFirstDiorite;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.EntityGolemFirstOak;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.EntityGolemFirstStone;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemCobble;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemGrindstone;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemMossy;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemPlank;
import com.kitisplode.golemfirststonemod.entity.entity.golem.vote.EntityGolemCopper;
import com.kitisplode.golemfirststonemod.entity.entity.golem.vote.EntityGolemTuff;
import com.kitisplode.golemfirststonemod.entity.entity.projectile.EntityProjectileAoEOwnerAware;
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
    public static final EntityType<EntityProjectileAoEOwnerAware> ENTITY_PROJECTILE_FIRST_OAK = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(GolemFirstStoneMod.MOD_ID, "entity_projectile_first_oak"),
            FabricEntityTypeBuilder.<EntityProjectileAoEOwnerAware>create(SpawnGroup.MISC, EntityProjectileAoEOwnerAware::new)
                    .dimensions(EntityDimensions.fixed(0.75f, 0.75f))
                    .trackRangeBlocks(4).trackedUpdateRate(10)
                    .build()
    );
    public static final EntityType<EntityGolemFirstBrick> ENTITY_GOLEM_FIRST_BRICK = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(GolemFirstStoneMod.MOD_ID, "entity_golem_first_brick"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, EntityGolemFirstBrick::new)
                    .dimensions(EntityDimensions.fixed(2.5f, 4.0f))
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

    public static final EntityType<EntityGolemCobble> ENTITY_GOLEM_COBBLE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(GolemFirstStoneMod.MOD_ID, "entity_golem_cobble"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, EntityGolemCobble::new)
                    .dimensions(EntityDimensions.fixed(1.0f, 1.0f))
                    .build()
    );
    public static final EntityType<EntityGolemPlank> ENTITY_GOLEM_PLANK = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(GolemFirstStoneMod.MOD_ID, "entity_golem_plank"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, EntityGolemPlank::new)
                    .dimensions(EntityDimensions.fixed(1.0f, 1.0f))
                    .build()
    );
    public static final EntityType<EntityGolemMossy> ENTITY_GOLEM_MOSSY = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(GolemFirstStoneMod.MOD_ID, "entity_golem_mossy"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, EntityGolemMossy::new)
                    .dimensions(EntityDimensions.fixed(1.0f, 1.0f))
                    .build()
    );
    public static final EntityType<EntityGolemGrindstone> ENTITY_GOLEM_GRINDSTONE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(GolemFirstStoneMod.MOD_ID, "entity_golem_grindstone"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, EntityGolemGrindstone::new)
                    .dimensions(EntityDimensions.fixed(1.0f, 1.0f))
                    .build()
    );

    public static final EntityType<EntityGolemTuff> ENTITY_GOLEM_TUFF = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(GolemFirstStoneMod.MOD_ID, "entity_golem_tuff"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, EntityGolemTuff::new)
                    .dimensions(EntityDimensions.fixed(1.0f, 1.0f))
                    .build()
    );
    public static final EntityType<EntityGolemCopper> ENTITY_GOLEM_COPPER = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(GolemFirstStoneMod.MOD_ID, "entity_golem_copper"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, EntityGolemCopper::new)
                    .dimensions(EntityDimensions.fixed(1.0f, 1.0f))
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


    public static final EntityType<EntitySoundRepeated> ENTITY_SOUND_REPEATED = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(GolemFirstStoneMod.MOD_ID, "entity_sound_repeated"),
            FabricEntityTypeBuilder.<EntitySoundRepeated>create(SpawnGroup.MISC, EntitySoundRepeated::new)
                    .build()
    );

    public static final EntityType<EntityEffectShieldFirstBrick> ENTITY_SHIELD_FIRST_BRICK = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(GolemFirstStoneMod.MOD_ID, "entity_shield_first_brick"),
            FabricEntityTypeBuilder.<EntityEffectShieldFirstBrick>create(SpawnGroup.MISC, EntityEffectShieldFirstBrick::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                    .build()
    );
    public static final EntityType<EntityEffectCubeDandoriWhistle> ENTITY_EFFECT_CUBE_DANDORI_WHISTLE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(GolemFirstStoneMod.MOD_ID, "entity_effect_cube_dandori_whistle"),
            FabricEntityTypeBuilder.<EntityEffectCubeDandoriWhistle>create(SpawnGroup.MISC, EntityEffectCubeDandoriWhistle::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
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
        FabricDefaultAttributeRegistry.register(ENTITY_GOLEM_COBBLE, EntityGolemCobble.setAttributes());
        FabricDefaultAttributeRegistry.register(ENTITY_GOLEM_PLANK, EntityGolemPlank.setAttributes());
        FabricDefaultAttributeRegistry.register(ENTITY_GOLEM_MOSSY, EntityGolemMossy.setAttributes());
        FabricDefaultAttributeRegistry.register(ENTITY_GOLEM_GRINDSTONE, EntityGolemGrindstone.setAttributes());
        FabricDefaultAttributeRegistry.register(ENTITY_GOLEM_TUFF, EntityGolemTuff.setAttributes());
        FabricDefaultAttributeRegistry.register(ENTITY_GOLEM_COPPER, EntityGolemCopper.setAttributes());
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
        EntityRendererRegistry.register(ENTITY_GOLEM_COBBLE, EntityRendererGolemCobble::new);
        EntityRendererRegistry.register(ENTITY_GOLEM_PLANK, EntityRendererGolemPlank::new);
        EntityRendererRegistry.register(ENTITY_GOLEM_MOSSY, EntityRendererGolemMossy::new);
        EntityRendererRegistry.register(ENTITY_GOLEM_GRINDSTONE, EntityRendererGolemGrindstone::new);
        EntityRendererRegistry.register(ENTITY_GOLEM_TUFF, EntityRendererGolemTuff::new);
        EntityRendererRegistry.register(ENTITY_GOLEM_COPPER, EntityRendererGolemCopper::new);

        EntityRendererRegistry.register(ENTITY_SOUND_REPEATED, EntityRendererSoundRepeated::new);
    }
}