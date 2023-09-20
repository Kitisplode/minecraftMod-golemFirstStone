package com.kitisplode.golemfirststonemod.item.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ItemInstruction extends Item
{
    private final String tooltip;
    public ItemInstruction(Properties pProperties, String tooltip)
    {
        super(pProperties);
        this.tooltip = tooltip;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced)
    {
        pTooltipComponents.add(Component.translatable(this.tooltip));
    }
}
