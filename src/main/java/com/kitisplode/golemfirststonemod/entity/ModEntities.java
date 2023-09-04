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
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.TippableArrowRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
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
                            .sized(2.5f, 4.0f)
                            .build(new ResourceLocation(GolemFirstStoneMod.MOD_ID, "entity_golem_first_stone").toString()));
    public static final RegistryObject<EntityType<EntityGolemFirstOak>> ENTITY_GOLEM_FIRST_OAK =
            ENTITY_TYPES.register("entity_golem_first_oak",
                    () -> EntityType.Builder.of(EntityGolemFirstOak::new, MobCategory.MISC)
                            .sized(2.5f, 4.0f)
                            .clientTrackingRange(10)
                            .build(new ResourceLocation(GolemFirstStoneMod.MOD_ID, "entity_golem_first_oak").toString()));
    public static final RegistryObject<EntityType<EntityProjectileAoEOwnerAware>> ENTITY_PROJECTILE_FIRST_OAK =
            ENTITY_TYPES.register("entity_projectile_first_oak",
                    () -> EntityType.Builder.<EntityProjectileAoEOwnerAware>of(EntityProjectileAoEOwnerAware::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f).clientTrackingRange(4).updateInterval(20)
                            .build(new ResourceLocation(GolemFirstStoneMod.MOD_ID, "entity_projectile_first_oak").toString()));
    public static final RegistryObject<EntityType<EntityGolemFirstBrick>> ENTITY_GOLEM_FIRST_BRICK =
            ENTITY_TYPES.register("entity_golem_first_brick",
                    () -> EntityType.Builder.of(EntityGolemFirstBrick::new, MobCategory.MISC)
                            .sized(2.5f, 4.0f)
                            .build(new ResourceLocation(GolemFirstStoneMod.MOD_ID, "entity_golem_first_brick").toString()));
    public static final RegistryObject<EntityType<EntityGolemFirstDiorite>> ENTITY_GOLEM_FIRST_DIORITE =
            ENTITY_TYPES.register("entity_golem_first_diorite",
                    () -> EntityType.Builder.of(EntityGolemFirstDiorite::new, MobCategory.MISC)
                            .sized(2.5f, 4.0f)
                            .build(new ResourceLocation(GolemFirstStoneMod.MOD_ID, "entity_golem_first_diorite").toString()));
    public static final RegistryObject<EntityType<EntityPawn>> ENTITY_PAWN_FIRST_DIORITE =
            ENTITY_TYPES.register("entity_pawn_first_diorite",
                    () -> EntityType.Builder.of(EntityPawn::new, MobCategory.MISC)
                            .sized(0.8f, 1.0f)
                            .build(new ResourceLocation(GolemFirstStoneMod.MOD_ID, "entity_pawn_first_diorite").toString()));

    public static final RegistryObject<EntityType<EntityGolemCobble>> ENTITY_GOLEM_COBBLE =
            ENTITY_TYPES.register("entity_golem_cobble",
                    () -> EntityType.Builder.of(EntityGolemCobble::new, MobCategory.MISC)
                            .sized(1.0f, 1.0f)
                            .build(new ResourceLocation(GolemFirstStoneMod.MOD_ID, "entity_golem_cobble").toString()));
    public static final RegistryObject<EntityType<EntityGolemPlank>> ENTITY_GOLEM_PLANK =
            ENTITY_TYPES.register("entity_golem_plank",
                    () -> EntityType.Builder.of(EntityGolemPlank::new, MobCategory.MISC)
                            .sized(1.0f, 1.0f)
                            .build(new ResourceLocation(GolemFirstStoneMod.MOD_ID, "entity_golem_plank").toString()));
    public static final RegistryObject<EntityType<EntityGolemMossy>> ENTITY_GOLEM_MOSSY =
            ENTITY_TYPES.register("entity_golem_mossy",
                    () -> EntityType.Builder.of(EntityGolemMossy::new, MobCategory.MISC)
                            .sized(1.0f, 1.0f)
                            .build(new ResourceLocation(GolemFirstStoneMod.MOD_ID, "entity_golem_mossy").toString()));
    public static final RegistryObject<EntityType<EntityGolemGrindstone>> ENTITY_GOLEM_GRINDSTONE =
            ENTITY_TYPES.register("entity_golem_grindstone",
                    () -> EntityType.Builder.of(EntityGolemGrindstone::new, MobCategory.MISC)
                            .sized(1.0f, 1.0f)
                            .build(new ResourceLocation(GolemFirstStoneMod.MOD_ID, "entity_golem_grindstone").toString()));

    public static final RegistryObject<EntityType<EntityGolemTuff>> ENTITY_GOLEM_TUFF =
            ENTITY_TYPES.register("entity_golem_tuff",
                    () -> EntityType.Builder.of(EntityGolemTuff::new, MobCategory.MISC)
                            .sized(1.0f, 1.0f)
                            .build(new ResourceLocation(GolemFirstStoneMod.MOD_ID, "entity_golem_tuff").toString()));
    public static final RegistryObject<EntityType<EntityGolemCopper>> ENTITY_GOLEM_COPPER =
            ENTITY_TYPES.register("entity_golem_copper",
                    () -> EntityType.Builder.of(EntityGolemCopper::new, MobCategory.MISC)
                            .sized(1.0f, 1.0f)
                            .build(new ResourceLocation(GolemFirstStoneMod.MOD_ID, "entity_golem_copper").toString()));

    public static final RegistryObject<EntityType<EntityPawn>> ENTITY_PAWN_TERRACOTTA =
            ENTITY_TYPES.register("entity_pawn_terracotta",
                    () -> EntityType.Builder.of(EntityPawn::new, MobCategory.MISC)
                            .sized(0.8f, 1.0f)
                            .build(new ResourceLocation(GolemFirstStoneMod.MOD_ID, "entity_pawn_terracotta").toString()));
    public static final RegistryObject<EntityType<EntityVillagerDandori>> ENTITY_VILLAGER_DANDORI =
            ENTITY_TYPES.register("entity_villager_dandori",
                    () -> EntityType.Builder.of(EntityVillagerDandori::new, MobCategory.MISC)
                            .sized(0.8f, 1.0f)
                            .build(new ResourceLocation(GolemFirstStoneMod.MOD_ID, "entity_villager_dandori").toString()));

    public static final RegistryObject<EntityType<EntitySoundRepeated>> ENTITY_SOUND_REPEATED =
            ENTITY_TYPES.register("entity_sound_repeated",
                    () -> EntityType.Builder.<EntitySoundRepeated>of(EntitySoundRepeated::new, MobCategory.MISC)
                            .build(new ResourceLocation(GolemFirstStoneMod.MOD_ID, "entity_sound_repeated").toString()));

    public static final RegistryObject<EntityType<EntityEffectShieldFirstBrick>> ENTITY_SHIELD_FIRST_BRICK =
            ENTITY_TYPES.register("entity_shield_first_brick",
                    () -> EntityType.Builder.<EntityEffectShieldFirstBrick>of(EntityEffectShieldFirstBrick::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .build(new ResourceLocation(GolemFirstStoneMod.MOD_ID, "entity_shield_first_brick").toString()));
    public static final RegistryObject<EntityType<EntityEffectCubeDandoriWhistle>> ENTITY_EFFECT_CUBE_DANDORI_WHISTLE =
            ENTITY_TYPES.register("entity_effect_cube_dandori_whistle",
                    () -> EntityType.Builder.<EntityEffectCubeDandoriWhistle>of(EntityEffectCubeDandoriWhistle::new, MobCategory.MISC)
                            .sized(0.5f, 0.5f)
                            .build(new ResourceLocation(GolemFirstStoneMod.MOD_ID, "entity_effect_cube_dandori_whistle").toString()));

    public static void register(IEventBus eventBus)
    {
        ENTITY_TYPES.register(eventBus);
    }

    public static void registerAttributes(EntityAttributeCreationEvent event)
    {
        event.put(ModEntities.ENTITY_GOLEM_FIRST_STONE.get(), EntityGolemFirstStone.setAttributes());
        event.put(ModEntities.ENTITY_GOLEM_FIRST_OAK.get(), EntityGolemFirstOak.setAttributes());
        event.put(ModEntities.ENTITY_GOLEM_FIRST_BRICK.get(), EntityGolemFirstBrick.setAttributes());
        event.put(ModEntities.ENTITY_GOLEM_FIRST_DIORITE.get(), EntityGolemFirstDiorite.setAttributes());
        event.put(ModEntities.ENTITY_GOLEM_COBBLE.get(), EntityGolemCobble.setAttributes());
        event.put(ModEntities.ENTITY_GOLEM_PLANK.get(), EntityGolemCobble.setAttributes());
        event.put(ModEntities.ENTITY_GOLEM_MOSSY.get(), EntityGolemCobble.setAttributes());
        event.put(ModEntities.ENTITY_GOLEM_GRINDSTONE.get(), EntityGolemGrindstone.setAttributes());
        event.put(ModEntities.ENTITY_GOLEM_TUFF.get(), EntityGolemTuff.setAttributes());
        event.put(ModEntities.ENTITY_GOLEM_COPPER.get(), EntityGolemCopper.setAttributes());
        event.put(ModEntities.ENTITY_PAWN_FIRST_DIORITE.get(), EntityPawn.setAttributes());
        event.put(ModEntities.ENTITY_VILLAGER_DANDORI.get(), EntityVillagerDandori.setAttributes());
        event.put(ModEntities.ENTITY_PAWN_TERRACOTTA.get(), EntityPawn.setAttributes());
    }

    public static void registerRenderers()
    {
        EntityRenderers.register(ModEntities.ENTITY_GOLEM_FIRST_STONE.get(), EntityRendererGolemFirstStone::new);
        EntityRenderers.register(ModEntities.ENTITY_GOLEM_FIRST_OAK.get(), EntityRendererGolemFirstOak::new);
        EntityRenderers.register(ModEntities.ENTITY_PROJECTILE_FIRST_OAK.get(), TippableArrowRenderer::new);
        EntityRenderers.register(ModEntities.ENTITY_GOLEM_FIRST_BRICK.get(), EntityRendererGolemFirstBrick::new);
        EntityRenderers.register(ModEntities.ENTITY_GOLEM_FIRST_DIORITE.get(), EntityRendererGolemFirstDiorite::new);
        EntityRenderers.register(ModEntities.ENTITY_GOLEM_COBBLE.get(), EntityRendererGolemCobble::new);
        EntityRenderers.register(ModEntities.ENTITY_GOLEM_PLANK.get(), EntityRendererGolemPlank::new);
        EntityRenderers.register(ModEntities.ENTITY_GOLEM_MOSSY.get(), EntityRendererGolemMossy::new);
        EntityRenderers.register(ModEntities.ENTITY_GOLEM_GRINDSTONE.get(), EntityRendererGolemGrindstone::new);
        EntityRenderers.register(ModEntities.ENTITY_GOLEM_TUFF.get(), EntityRendererGolemTuff::new);
        EntityRenderers.register(ModEntities.ENTITY_GOLEM_COPPER.get(), EntityRendererGolemCopper::new);
        EntityRenderers.register(ModEntities.ENTITY_PAWN_FIRST_DIORITE.get(), EntityRendererPawn::new);
        EntityRenderers.register(ModEntities.ENTITY_PAWN_TERRACOTTA.get(), EntityRendererPawn::new);
        EntityRenderers.register(ModEntities.ENTITY_VILLAGER_DANDORI.get(), EntityRendererVillagerDandori::new);
        EntityRenderers.register(ModEntities.ENTITY_SOUND_REPEATED.get(), EntityRendererSoundRepeated::new);
        EntityRenderers.register(ModEntities.ENTITY_SHIELD_FIRST_BRICK.get(), EntityRendererShieldFirstBrick::new);
        EntityRenderers.register(ModEntities.ENTITY_EFFECT_CUBE_DANDORI_WHISTLE.get(), EntityRendererShieldFirstBrick::new);
    }
}
