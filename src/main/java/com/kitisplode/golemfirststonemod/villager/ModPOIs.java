package com.kitisplode.golemfirststonemod.villager;

import com.google.common.collect.ImmutableSet;
import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.world.poi.PointOfInterestType;

public class ModPOIs
{

    public static final PointOfInterestType POI_FIRSTCORE_STONE = registerPOI("poi_firstcore_stone", ModBlocks.BLOCK_CORE_STONE);
    public static final PointOfInterestType POI_FIRSTCORE_OAK = registerPOI("poi_firstcore_oak", ModBlocks.BLOCK_CORE_OAK);
    public static final PointOfInterestType POI_FIRSTCORE_BRICK = registerPOI("poi_firstcore_brick", ModBlocks.BLOCK_CORE_BRICK);
    public static final PointOfInterestType POI_FIRSTCORE_DIORITE = registerPOI("poi_firstcore_diorite", ModBlocks.BLOCK_CORE_DIORITE);


    public static PointOfInterestType registerPOI(String name, Block block)
    {
        return PointOfInterestHelper.register(new Identifier(GolemFirstStoneMod.MOD_ID, name),
                1,1, ImmutableSet.copyOf(block.getStateManager().getStates()));
    }

    public static void registerModPOIs()
    {
        GolemFirstStoneMod.LOGGER.debug("Registering Points of Interest for " + GolemFirstStoneMod.MOD_ID);
    }

}
