package com.kitisplode.golemfirststonemod.structure;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.mixin.MixinStructurePoolAccessor;
import com.mojang.datafixers.util.Pair;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.processor.StructureProcessorList;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

// Code copied from https://github.com/finallion/VillagersPlus-FABRIC/blob/f646582b2f44a32d1f184c47f0f2544573c6c225/src/main/java/com/finallion/villagersplus/mixin/StructurePoolAccessor.java
// TODO: ask permission to use this code
public class ModStructures
{
    private static final RegistryKey<StructureProcessorList> EMPTY_PROCESSOR_LIST_KEY = RegistryKey.of(RegistryKeys.PROCESSOR_LIST, new Identifier("minecraft", "empty"));
    private static final Identifier plainsPoolLocation = new Identifier("minecraft:village/plains/town_centers");
    private static final Identifier savannaPoolLocation = new Identifier("minecraft:village/savanna/town_centers");
    private static final Identifier desertPoolLocation = new Identifier("minecraft:village/desert/town_centers");
    private static final Identifier snowyPoolLocation = new Identifier("minecraft:village/snowy/town_centers");

    public static void registerJigsaws(MinecraftServer server)
    {
        Registry<StructurePool> templatePoolRegistry = server.getRegistryManager().get(RegistryKeys.TEMPLATE_POOL);
        Registry<StructureProcessorList> processorListRegistry = server.getRegistryManager().get(RegistryKeys.PROCESSOR_LIST);

        addBuildingToPool(templatePoolRegistry, processorListRegistry, plainsPoolLocation, "golemfirststonemod:village/villager_stone_2", 50);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, savannaPoolLocation, "golemfirststonemod:village/villager_oak_2", 50);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, desertPoolLocation, "golemfirststonemod:village/villager_brick_2", 50);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, snowyPoolLocation, "golemfirststonemod:village/villager_diorite_2", 50);

        GolemFirstStoneMod.LOGGER.info("register village structures");
    }

    private static void addBuildingToPool(Registry<StructurePool> templatePoolRegistry, Registry<StructureProcessorList> processorListRegistry,
                                         Identifier poolRL, String nbtPieceRL, int weight)
    {
        RegistryEntry<StructureProcessorList> processorList = processorListRegistry.entryOf(EMPTY_PROCESSOR_LIST_KEY);
        StructurePool pool = templatePoolRegistry.get(poolRL);
        if (pool == null) return;
        SinglePoolElement piece = SinglePoolElement.ofProcessedSingle(nbtPieceRL, processorList).apply(StructurePool.Projection.RIGID);
        for (int i = 0; i < weight; i++)
        {
            ((MixinStructurePoolAccessor) pool).getTemplates().add(piece);
        }
        List<Pair<StructurePoolElement, Integer>> listOfPieceEntries = new ArrayList<>(((MixinStructurePoolAccessor) pool).getRawTemplates());
        listOfPieceEntries.add(new Pair<>(piece, weight));
        ((MixinStructurePoolAccessor) pool).setRawTemplates(listOfPieceEntries);
    }
}
