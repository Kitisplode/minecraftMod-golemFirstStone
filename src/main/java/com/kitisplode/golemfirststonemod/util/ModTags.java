package com.kitisplode.golemfirststonemod.util;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {
    public static class Blocks
    {
        public static final TagKey<Block> AGENT_CAN_INTERACT = createTag("agent_can_interact");

        private static TagKey<Block> createTag(String name)
        {
            return TagKey.of(RegistryKeys.BLOCK, new Identifier(GolemFirstStoneMod.MOD_ID, name));
        }
    }

    public static class Items
    {
        private static TagKey<Item> createTag(String name)
        {
            return TagKey.of(RegistryKeys.ITEM, new Identifier(GolemFirstStoneMod.MOD_ID, name));
        }
    }
}