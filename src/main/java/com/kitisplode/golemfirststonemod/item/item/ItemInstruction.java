package com.kitisplode.golemfirststonemod.item.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemInstruction extends Item
{
    private final String tooltip;
    private final int instructionCount;
    public ItemInstruction(Settings settings, String tooltip, int instructionCount)
    {
        super(settings);
        this.tooltip = tooltip;
        this.instructionCount = instructionCount;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
    {
        tooltip.add(Text.translatable(this.tooltip));
    }

    public int getInstructionCount()
    {
        return instructionCount;
    }
}
