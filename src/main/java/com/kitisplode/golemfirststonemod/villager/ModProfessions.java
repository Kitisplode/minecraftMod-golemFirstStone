package com.kitisplode.golemfirststonemod.villager;

import com.google.common.collect.ImmutableSet;
import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModProfessions
{
    public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(ForgeRegistries.VILLAGER_PROFESSIONS, GolemFirstStoneMod.MOD_ID);

    public static RegistryObject<VillagerProfession> VILLAGER_STONE = VILLAGER_PROFESSIONS.register("villager_stone", () ->
            new VillagerProfession("villager_stone", holder -> holder.value().equals(ModPOIs.POI_VILLAGER_STONE.get()), holder -> holder.value().equals(ModPOIs.POI_VILLAGER_STONE.get()), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_ARMORER));
    public static RegistryObject<VillagerProfession> VILLAGER_OAK = VILLAGER_PROFESSIONS.register("villager_oak", () ->
            new VillagerProfession("villager_oak", holder -> holder.value().equals(ModPOIs.POI_VILLAGER_OAK.get()), holder -> holder.value().equals(ModPOIs.POI_VILLAGER_OAK.get()), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_ARMORER));
    public static RegistryObject<VillagerProfession> VILLAGER_BRICK = VILLAGER_PROFESSIONS.register("villager_brick", () ->
            new VillagerProfession("villager_brick", holder -> holder.value().equals(ModPOIs.POI_VILLAGER_BRICK.get()), holder -> holder.value().equals(ModPOIs.POI_VILLAGER_BRICK.get()), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_ARMORER));
    public static RegistryObject<VillagerProfession> VILLAGER_DIORITE = VILLAGER_PROFESSIONS.register("villager_diorite", () ->
            new VillagerProfession("villager_diorite", holder -> holder.value().equals(ModPOIs.POI_VILLAGER_DIORITE.get()), holder -> holder.value().equals(ModPOIs.POI_VILLAGER_DIORITE.get()), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_ARMORER));

}
