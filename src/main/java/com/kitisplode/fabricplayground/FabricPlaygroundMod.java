package com.kitisplode.fabricplayground;

import com.kitisplode.fabricplayground.block.ModBlocks;
import com.kitisplode.fabricplayground.entity.ModEntities;
import com.kitisplode.fabricplayground.item.ModItemGroups;
import com.kitisplode.fabricplayground.item.ModItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FabricPlaygroundMod implements ModInitializer {
	public static final String MOD_ID = "fabricplayground";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize()
	{
		LOGGER.info(MOD_ID + " initializing...");
		ModItemGroups.registerItemGroups();
		ModBlocks.registerModBlocks();
		ModItems.registerModItems();
		ModEntities.registerModEntities();
	}
}