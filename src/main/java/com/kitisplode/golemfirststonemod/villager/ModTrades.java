package com.kitisplode.golemfirststonemod.villager;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.block.ModBlocks;
import com.kitisplode.golemfirststonemod.villager.trades.SellTradeOffer;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = GolemFirstStoneMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModTrades
{
    @SubscribeEvent
    public static void registerTrades(VillagerTradesEvent event)
    {
        Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
        if (event.getType() == ModProfessions.VILLAGER_STONE.get())
        {
            trades.get(1).add(new SellTradeOffer(ModBlocks.BLOCK_HEAD_STONE.get().asItem(), 64, 1, 25));
        }
        else if (event.getType() == ModProfessions.VILLAGER_OAK.get())
        {
            trades.get(1).add(new SellTradeOffer(ModBlocks.BLOCK_HEAD_OAK.get().asItem(), 64, 1, 25));
        }
        else if (event.getType() == ModProfessions.VILLAGER_BRICK.get())
        {
            trades.get(1).add(new SellTradeOffer(ModBlocks.BLOCK_HEAD_BRICK.get().asItem(), 64, 1, 25));
        }
        else if (event.getType() == ModProfessions.VILLAGER_DIORITE.get())
        {
            trades.get(1).add(new SellTradeOffer(ModBlocks.BLOCK_HEAD_DIORITE.get().asItem(), 64, 1, 25));
        }
    }
}
