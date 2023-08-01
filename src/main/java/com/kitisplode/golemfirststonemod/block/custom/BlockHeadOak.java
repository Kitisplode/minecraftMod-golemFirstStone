package com.kitisplode.golemfirststonemod.block.custom;

import com.kitisplode.golemfirststonemod.block.ModBlocks;
import com.kitisplode.golemfirststonemod.util.golem_pattern.GolemPatternFirstOak;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;

import java.util.function.Predicate;

public class BlockHeadOak extends AbstractBlockHead
{
    public BlockHeadOak(AbstractBlock.Settings settings)
    {
        super(settings);
    }

    protected void setupPatterns()
    {
        if (patternList.size() == 0)
        {
            patternList.add(new GolemPatternFirstOak(SPAWN_BLOCK_PREDICATE));
        }
    }

    protected Predicate<BlockState> setupPredicates()
    {
        return state -> state != null
                && (state.isOf(ModBlocks.BLOCK_HEAD_OAK));
    }
}
