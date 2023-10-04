package com.kitisplode.golemfirststonemod.villager;

import com.google.common.collect.ImmutableSet;
import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.block.ModBlocks;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModPOIs
{
    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, GolemFirstStoneMod.MOD_ID);

    public static RegistryObject<PoiType> POI_VILLAGER_STONE = POI_TYPES.register("poi_villager_stone", () -> new PoiType(ImmutableSet.copyOf(
            ModBlocks.BLOCK_VILLAGER_STONE.get().getStateDefinition().getPossibleStates()), 1, 1));
    public static RegistryObject<PoiType> POI_VILLAGER_OAK = POI_TYPES.register("poi_villager_oak", () -> new PoiType(ImmutableSet.copyOf(
            ModBlocks.BLOCK_VILLAGER_OAK.get().getStateDefinition().getPossibleStates()), 1, 1));
    public static RegistryObject<PoiType> POI_VILLAGER_BRICK = POI_TYPES.register("poi_villager_brick", () -> new PoiType(ImmutableSet.copyOf(
            ModBlocks.BLOCK_VILLAGER_BRICK.get().getStateDefinition().getPossibleStates()), 1, 1));
    public static RegistryObject<PoiType> POI_VILLAGER_DIORITE = POI_TYPES.register("poi_villager_diorite", () -> new PoiType(ImmutableSet.copyOf(
            ModBlocks.BLOCK_VILLAGER_DIORITE.get().getStateDefinition().getPossibleStates()), 1, 1));
    public static RegistryObject<PoiType> POI_PATH_GOLEM_PRISON = POI_TYPES.register("poi_path_golem_prison", () -> new PoiType(ImmutableSet.copyOf(
            ModBlocks.BLOCK_PATH_GOLEM_PRISON.get().getStateDefinition().getPossibleStates()), 1, 1));
}
