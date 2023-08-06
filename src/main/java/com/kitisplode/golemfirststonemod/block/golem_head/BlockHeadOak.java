package com.kitisplode.golemfirststonemod.block.golem_head;

import com.kitisplode.golemfirststonemod.block.ModBlocks;
import com.kitisplode.golemfirststonemod.util.golempatterns.GolemPatternFirstOak;
import com.kitisplode.golemfirststonemod.util.golempatterns.GolemPatternFirstStone;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

public class BlockHeadOak extends AbstractBlockHead
{
    public BlockHeadOak(Properties settings) {
        super(settings);
    }

    @Override
    protected void setupPatterns()
    {
        patternList.add(new GolemPatternFirstOak(SPAWN_BLOCK_PREDICATE));
    }

    @Override
    protected Predicate<BlockState> setupPredicates()
    {
        return state -> state != null
                && (state.is(this));
    }
}
