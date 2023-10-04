package com.kitisplode.golemfirststonemod.util;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags
{
    public static class Blocks
    {
        public static final TagKey<Block> AGENT_CAN_INTERACT = tag("agent_can_interact");

        private static TagKey<Block> tag(String name)
        {
            return BlockTags.create(new ResourceLocation(GolemFirstStoneMod.MOD_ID, name));
        }
    }

    public static class Items {

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(new ResourceLocation(GolemFirstStoneMod.MOD_ID, name));
        }
    }

    public static class POIs
    {
        public static final TagKey<PoiType> PATH_GOLEM_PRISON = tag("path_golem_prison");

        private static TagKey<PoiType> tag(String name)
        {
            return TagKey.create(Registries.POINT_OF_INTEREST_TYPE, new ResourceLocation(GolemFirstStoneMod.MOD_ID, name));
        }
    }
}
