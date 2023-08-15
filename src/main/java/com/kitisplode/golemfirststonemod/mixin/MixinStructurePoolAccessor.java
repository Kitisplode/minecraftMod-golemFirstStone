package com.kitisplode.golemfirststonemod.mixin;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

// Code copied from https://github.com/finallion/VillagersPlus-FABRIC/blob/f646582b2f44a32d1f184c47f0f2544573c6c225/src/main/java/com/finallion/villagersplus/mixin/StructurePoolAccessor.java
// TODO: ask permission to use this code
@Mixin(StructurePool.class)
public interface MixinStructurePoolAccessor
{
    @Accessor("elementCounts")
    List<Pair<StructurePoolElement, Integer>> getRawTemplates();

    @Mutable
    @Accessor("elementCounts")
    void setRawTemplates(List<Pair<StructurePoolElement, Integer>> elementCounts);

    @Accessor("elements")
    ObjectArrayList<StructurePoolElement> getTemplates();
}
