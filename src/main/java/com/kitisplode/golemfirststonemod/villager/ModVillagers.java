package com.kitisplode.golemfirststonemod.villager;

import com.google.common.collect.ImmutableSet;
import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.block.ModBlocks;
import com.kitisplode.golemfirststonemod.item.ModItems;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.villager.VillagerProfessionBuilder;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;

public class ModVillagers
{
//    public static final RegistryKey<PointOfInterestType> POI_KEY_DANDORI = PointOfInterestTypes.of("poi_key_dandori");
//
//    public static final PointOfInterestType POI_DANDORI = PointOfInterestTypes.register(Registries.POINT_OF_INTEREST_TYPE, POI_KEY_DANDORI, PointOfInterestTypes.getStatesOfBlock(ModBlocks.BLOCK_DANDORI), 1,1);
//
//    public static final VillagerProfession VILLAGER_DANDORI = VillagerProfession.register("villager_dandori", POI_KEY_DANDORI, SoundEvents.ENTITY_VILLAGER_WORK_ARMORER);

    public static final PointOfInterestType POI_VILLAGER_STONE = registerPOI("poi_villager_stone", ModBlocks.BLOCK_VILLAGER_STONE);
    public static final VillagerProfession VILLAGER_STONE = registerProfession("villager_stone",
            RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, new Identifier(GolemFirstStoneMod.MOD_ID, "poi_villager_stone")),
            SoundEvents.ENTITY_VILLAGER_WORK_ARMORER);

    public static final PointOfInterestType POI_VILLAGER_OAK = registerPOI("poi_villager_oak", ModBlocks.BLOCK_VILLAGER_OAK);
    public static final VillagerProfession VILLAGER_OAK = registerProfession("villager_oak",
            RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, new Identifier(GolemFirstStoneMod.MOD_ID, "poi_villager_oak")),
            SoundEvents.ENTITY_VILLAGER_WORK_ARMORER);

    public static final PointOfInterestType POI_VILLAGER_BRICK = registerPOI("poi_villager_brick", ModBlocks.BLOCK_VILLAGER_BRICK);
    public static final VillagerProfession VILLAGER_BRICK = registerProfession("villager_brick",
            RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, new Identifier(GolemFirstStoneMod.MOD_ID, "poi_villager_brick")),
            SoundEvents.ENTITY_VILLAGER_WORK_ARMORER);

    public static final PointOfInterestType POI_VILLAGER_DIORITE = registerPOI("poi_villager_diorite", ModBlocks.BLOCK_VILLAGER_DIORITE);
    public static final VillagerProfession VILLAGER_DIORITE = registerProfession("villager_diorite",
            RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, new Identifier(GolemFirstStoneMod.MOD_ID, "poi_villager_diorite")),
            SoundEvents.ENTITY_VILLAGER_WORK_ARMORER);

    public static VillagerProfession registerProfession(String name, RegistryKey<PointOfInterestType> type, SoundEvent sound)
    {
        return Registry.register(Registries.VILLAGER_PROFESSION, new Identifier(GolemFirstStoneMod.MOD_ID, name),
                VillagerProfessionBuilder.create().id(new Identifier(GolemFirstStoneMod.MOD_ID, name)).workstation(type).workSound(sound).build());
    }

    public static PointOfInterestType registerPOI(String name, Block block)
    {
        return PointOfInterestHelper.register(new Identifier(GolemFirstStoneMod.MOD_ID, name),
                1,1, ImmutableSet.copyOf(block.getStateManager().getStates()));
    }

    public static void registerModVillagers()
    {
        GolemFirstStoneMod.LOGGER.debug("Registering Villagers for " + GolemFirstStoneMod.MOD_ID);
    }

    public static void registerModTrades()
    {
        TradeOfferHelper.registerVillagerOffers(VILLAGER_STONE, 1,
                factories -> {
                    factories.add((entity,random) -> new TradeOffer(
                            new ItemStack(Items.EMERALD, 64),
                            new ItemStack(ModBlocks.BLOCK_HEAD_STONE, 1),
                            1, 2, 0.02f
                    ));
                });
        TradeOfferHelper.registerVillagerOffers(VILLAGER_OAK, 1,
                factories -> {
                    factories.add((entity,random) -> new TradeOffer(
                            new ItemStack(Items.EMERALD, 64),
                            new ItemStack(ModBlocks.BLOCK_HEAD_OAK, 1),
                            1, 2, 0.02f
                    ));
                });
        TradeOfferHelper.registerVillagerOffers(VILLAGER_BRICK, 1,
                factories -> {
                    factories.add((entity,random) -> new TradeOffer(
                            new ItemStack(Items.EMERALD, 64),
                            new ItemStack(ModBlocks.BLOCK_HEAD_BRICK, 1),
                            1, 2, 0.02f
                    ));
                });
        TradeOfferHelper.registerVillagerOffers(VILLAGER_DIORITE, 1,
                factories -> {
                    factories.add((entity,random) -> new TradeOffer(
                            new ItemStack(Items.EMERALD, 64),
                            new ItemStack(ModBlocks.BLOCK_HEAD_DIORITE, 1),
                            1, 2, 0.02f
                    ));
                });
    }
}
