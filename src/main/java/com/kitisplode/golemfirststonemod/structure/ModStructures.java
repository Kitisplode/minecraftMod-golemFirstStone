package com.kitisplode.golemfirststonemod.structure;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.mixin.MixinStructurePoolAccessor;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.structure.JigsawStructure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.ArrayList;
import java.util.List;

// Code copied from https://github.com/finallion/VillagersPlus-FABRIC/blob/f646582b2f44a32d1f184c47f0f2544573c6c225/src/main/java/com/finallion/villagersplus/mixin/StructurePoolAccessor.java
public class ModStructures
{
    private static final RegistryKey<StructureProcessorList> EMPTY_PROCESSOR_LIST_KEY = RegistryKey.of(RegistryKeys.PROCESSOR_LIST, new Identifier("minecraft", "empty"));
    private static final Identifier plainsPoolLocation = new Identifier("minecraft:village/plains/town_centers");
    private static final Identifier savannaPoolLocation = new Identifier("minecraft:village/savanna/town_centers");
    private static final Identifier desertPoolLocation = new Identifier("minecraft:village/desert/town_centers");
    private static final Identifier snowyPoolLocation = new Identifier("minecraft:village/snowy/town_centers");

    public static void increaseJigsawSize()
    {
        JigsawStructure.CODEC = Codecs.validate(
                RecordCodecBuilder.mapCodec(
                        instance -> instance.group(
                                JigsawStructure.configCodecBuilder(instance),
                                RecordCodecBuilder.of(structure -> structure.startPool, StructurePool.REGISTRY_CODEC.fieldOf("start_pool")),
                                RecordCodecBuilder.of(structure -> structure.startJigsawName, Identifier.CODEC.optionalFieldOf("start_jigsaw_name")),
                                RecordCodecBuilder.of(structure -> structure.size, Codec.intRange(0, 8).fieldOf("size")),
                                RecordCodecBuilder.of(structure -> structure.startHeight, HeightProvider.CODEC.fieldOf("start_height")),
                                RecordCodecBuilder.of(structure -> structure.useExpansionHack, Codec.BOOL.fieldOf("use_expansion_hack")),
                                RecordCodecBuilder.of(structure -> structure.projectStartToHeightmap, Heightmap.Type.CODEC.optionalFieldOf("project_start_to_heightmap")),
                                RecordCodecBuilder.of(structure -> structure.maxDistanceFromCenter, Codec.intRange(1, 128).fieldOf("max_distance_from_center"))
                        ).apply(instance, JigsawStructure::new)),
                JigsawStructure::validate).codec();

//        StructureType.JIGSAW = StructureType.register("jigsaw", JigsawStructure.CODEC);
    }

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
