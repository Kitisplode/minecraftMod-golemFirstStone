package com.kitisplode.golemfirststonemod.structure;

import com.kitisplode.golemfirststonemod.mixin.MixinStructurePoolAccessor;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.ArrayList;
import java.util.List;

public class ModStructures {
    private static final ResourceKey<StructureProcessorList> EMPTY_PROCESSOR_LIST_KEY = ResourceKey.create(Registries.PROCESSOR_LIST, new ResourceLocation("minecraft", "empty"));
    private static final ResourceLocation plainsPoolLocation = new ResourceLocation("minecraft:village/plains/town_centers");
    private static final ResourceLocation savannaPoolLocation = new ResourceLocation("minecraft:village/savanna/town_centers");
    private static final ResourceLocation desertPoolLocation = new ResourceLocation("minecraft:village/desert/town_centers");
    private static final ResourceLocation snowyPoolLocation = new ResourceLocation("minecraft:village/snowy/town_centers");

    public static void registerJigsaws(MinecraftServer server) {
        Registry<StructureTemplatePool> templatePoolRegistry = server.registryAccess().registry(Registries.TEMPLATE_POOL).orElseThrow();
        Registry<StructureProcessorList> processorListRegistry = server.registryAccess().registry(Registries.PROCESSOR_LIST).orElseThrow();

        addBuildingToPool(templatePoolRegistry, processorListRegistry, plainsPoolLocation, "golemfirststonemod:village/villager_stone_2", 50);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, savannaPoolLocation, "golemfirststonemod:village/villager_oak_2", 50);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, desertPoolLocation, "golemfirststonemod:village/villager_brick_2", 50);
        addBuildingToPool(templatePoolRegistry, processorListRegistry, snowyPoolLocation, "golemfirststonemod:village/villager_diorite_2", 50);
    }

    public static void addBuildingToPool(Registry<StructureTemplatePool> templatePoolRegistry, Registry<StructureProcessorList> processorListRegistry, ResourceLocation poolRL, String nbtPieceRL, int weight) {
        Holder<StructureProcessorList> processorList = processorListRegistry.getHolderOrThrow(EMPTY_PROCESSOR_LIST_KEY);

        StructureTemplatePool pool = templatePoolRegistry.get(poolRL);
        if (pool == null) return;

        SinglePoolElement piece = SinglePoolElement.single(nbtPieceRL, processorList).apply(StructureTemplatePool.Projection.RIGID);

        for (int i = 0; i < weight; i++) {
            ((MixinStructurePoolAccessor) pool).getTemplates().add(piece);
        }

        List<Pair<StructurePoolElement, Integer>> listOfPieceEntries = new ArrayList<>(((MixinStructurePoolAccessor) pool).getRawTemplates());
        listOfPieceEntries.add(new Pair<>(piece, weight));
        ((MixinStructurePoolAccessor) pool).setRawTemplates(listOfPieceEntries);
    }
}
