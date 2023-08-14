package com.kitisplode.golemfirststonemod.villager;

import com.google.common.collect.ImmutableSet;
import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.block.ModBlocks;
import com.kitisplode.golemfirststonemod.item.ModItems;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.object.builder.v1.villager.VillagerProfessionBuilder;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;

public class ModVillagers
{
//    public static final RegistryKey<PointOfInterestType> POI_KEY_DANDORI = PointOfInterestTypes.of("poi_key_dandori");
//
//    public static final PointOfInterestType POI_DANDORI = PointOfInterestTypes.register(Registries.POINT_OF_INTEREST_TYPE, POI_KEY_DANDORI, PointOfInterestTypes.getStatesOfBlock(ModBlocks.BLOCK_DANDORI), 1,1);
//
//    public static final VillagerProfession VILLAGER_DANDORI = VillagerProfession.register("villager_dandori", POI_KEY_DANDORI, SoundEvents.ENTITY_VILLAGER_WORK_ARMORER);

    public static final PointOfInterestType POI_DANDORI = registerPOI("poi_dandori", ModBlocks.BLOCK_DANDORI);
    public static final VillagerProfession VILLAGER_DANDORI = registerProfession("villager_dandori",
            RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, new Identifier(GolemFirstStoneMod.MOD_ID, "poi_dandori")),
            SoundEvents.ENTITY_VILLAGER_WORK_ARMORER);

    public static VillagerProfession registerProfession(String name, RegistryKey<PointOfInterestType> type, SoundEvent sound)
    {
        return Registry.register(Registries.VILLAGER_PROFESSION, new Identifier(GolemFirstStoneMod.MOD_ID, name),
                VillagerProfessionBuilder.create().id(new Identifier(GolemFirstStoneMod.MOD_ID, name)).workstation(type).workSound(sound).build());
    }

    public static PointOfInterestType registerPOI(String name, Block block)
    {
        return PointOfInterestHelper.register(new Identifier(GolemFirstStoneMod.MOD_ID, name),
                1,1, ImmutableSet.copyOf(block.getStateManager().getStates()));
    }

    public static void registerModVillagers()
    {
        GolemFirstStoneMod.LOGGER.debug("Registering Villagers for " + GolemFirstStoneMod.MOD_ID);
    }

    public static void registerModTrades()
    {
        TradeOfferHelper.registerVillagerOffers(VILLAGER_DANDORI, 1,
                factories -> {
                    factories.add((entity,random) -> new TradeOffer(
                            new ItemStack(Items.EMERALD, 3),
                            new ItemStack(ModItems.ITEM_DANDORI_CALL, 1),
                            6, 2, 0.02f
                    ));
                });
    }
}
