package com.kitisplode.golemfirststonemod.block.golem_head;

import com.kitisplode.golemfirststonemod.util.golem_pattern.GolemPatternFirstBrick;
import com.kitisplode.golemfirststonemod.util.golem_pattern.GolemPatternFirstStone;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;

import java.util.function.Predicate;

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
            patternList.add(new GolemPatternFirstBrick(SPAWN_BLOCK_PREDICATE));
        }
    }

    protected Predicate<BlockState> setupPredicates()
    {
        return state -> state != null
                && (state.isOf(this));
    }
}
