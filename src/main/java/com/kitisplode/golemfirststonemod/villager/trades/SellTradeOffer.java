package com.kitisplode.golemfirststonemod.villager.trades;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.Block;

public class SellTradeOffer implements VillagerTrades.ItemListing {
    private final ItemStack itemStack;
    private final int emeraldCost;
    private final int numberOfItems;
    private final int maxUses;
    private final int villagerXp;
    private final float priceMultiplier;

    public SellTradeOffer(Block p_35765_, int p_35766_, int p_35767_, int p_35768_, int p_35769_) {
        this(new ItemStack(p_35765_), p_35766_, p_35767_, p_35768_, p_35769_);
    }

    public SellTradeOffer(Item pItem, int cost, int count, int experience) {
        this(new ItemStack(pItem), cost, count, 12, experience);
    }

    public SellTradeOffer(Item p_35746_, int p_35747_, int p_35748_, int p_35749_, int p_35750_) {
        this(new ItemStack(p_35746_), p_35747_, p_35748_, p_35749_, p_35750_);
    }

    public SellTradeOffer(ItemStack p_35752_, int p_35753_, int p_35754_, int p_35755_, int p_35756_) {
        this(p_35752_, p_35753_, p_35754_, p_35755_, p_35756_, 0.05F);
    }

    public SellTradeOffer(ItemStack p_35758_, int p_35759_, int p_35760_, int p_35761_, int p_35762_, float p_35763_) {
        this.itemStack = p_35758_;
        this.emeraldCost = p_35759_;
        this.numberOfItems = p_35760_;
        this.maxUses = p_35761_;
        this.villagerXp = p_35762_;
        this.priceMultiplier = p_35763_;
    }

    public MerchantOffer getOffer(Entity p_219699_, RandomSource p_219700_) {
        return new MerchantOffer(new ItemStack(Items.EMERALD, this.emeraldCost), new ItemStack(this.itemStack.getItem(), this.numberOfItems), this.maxUses, this.villagerXp, this.priceMultiplier);
    }
}