package com.kitisplode.golemfirststonemod.block.golem_head;

import com.kitisplode.golemfirststonemod.util.golem_pattern.GolemPatternFirstBrick;
import net.minecraft.block.BlockState;

import java.util.function.Predicate;

public class BlockHeadBrick extends AbstractBlockHead
{
    public BlockHeadBrick(Settings settings)
    {
        super(settings);
    }

    protected void setupPatterns()
    {
        if (patternList.size() == 0)
        {
            patternList.add(new GolemPatternFirstBrick(SPAWN_BLOCK_PREDICATE));
        }
    }

    protected Predicate<BlockState> setupPredicates()
    {
        return state -> state != null
                && (state.isOf(this));
    }
}
