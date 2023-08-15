package com.kitisplode.golemfirststonemod;

import com.kitisplode.golemfirststonemod.block.ModBlocks;
import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.item.ModItemGroups;
import com.kitisplode.golemfirststonemod.item.ModItems;
import com.kitisplode.golemfirststonemod.sound.ModSounds;
import com.kitisplode.golemfirststonemod.structure.ModStructures;
import com.kitisplode.golemfirststonemod.villager.ModVillagers;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GolemFirstStoneMod implements ModInitializer {
	public static final String MOD_ID = "golemfirststonemod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize()
	{
		LOGGER.info(MOD_ID + " initializing...");
		ModItemGroups.registerItemGroups();
		ModBlocks.registerModBlocks();
		ModItems.registerModItems();
		ModEntities.registerModEntities();
		ModSounds.registerModSounds();

		ModVillagers.registerModVillagers();
		ModVillagers.registerModTrades();

		ServerLifecycleEvents.SERVER_STARTING.register(ModStructures::registerJigsaws);
	}
}