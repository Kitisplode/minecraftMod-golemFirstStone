package com.kitisplode.golemfirststonemod.block.golem_head;

import com.kitisplode.golemfirststonemod.block.ModBlocks;
import com.kitisplode.golemfirststonemod.util.golempatterns.GolemPatternFirstStone;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

public class BlockHeadStone extends AbstractBlockHead
{
    public BlockHeadStone(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Override
    protected void setupPatterns()
    {
        patternList.add(new GolemPatternFirstStone(SPAWN_BLOCK_PREDICATE));
    }

    @Override
    protected Predicate<BlockState> setupPredicates()
    {
        return state -> state != null
                && (state.is(ModBlocks.BLOCK_HEAD_STONE.get()));
    }
}
