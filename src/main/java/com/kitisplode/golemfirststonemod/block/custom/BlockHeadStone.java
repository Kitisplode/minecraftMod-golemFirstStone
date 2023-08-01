package com.kitisplode.golemfirststonemod.block.custom;

import com.kitisplode.golemfirststonemod.util.golem_pattern.GolemPatternFirstStone;
import net.minecraft.block.AbstractBlock;

public class BlockHeadStone extends AbstractBlockHead
{
    public BlockHeadStone(AbstractBlock.Settings settings)
    {
        super(settings);
    }

    protected void setupPatterns()
    {
        if (patternList.size() == 0)
        {
            patternList.add(new GolemPatternFirstStone(SPAWN_BLOCK_PREDICATE));
        }
    }
}
