package com.kitisplode.golemfirststonemod.block.custom;

import com.kitisplode.golemfirststonemod.util.golem_pattern.GolemPatternFirstOak;

public class BlockHeadOak extends AbstractBlockHead
{
    public BlockHeadOak(Settings settings)
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
}
