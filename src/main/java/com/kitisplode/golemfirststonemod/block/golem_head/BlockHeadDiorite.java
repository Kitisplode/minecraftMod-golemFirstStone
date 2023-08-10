package com.kitisplode.golemfirststonemod.block.golem_head;

import com.kitisplode.golemfirststonemod.util.golempatterns.GolemPatternFirstDiorite;
import com.kitisplode.golemfirststonemod.util.golempatterns.GolemPatternFirstStone;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

public class BlockHeadDiorite extends AbstractBlockHead
{
    public BlockHeadDiorite(Properties settings) {
        super(settings);
    }

    @Override
    protected void setupPatterns()
    {
        patternList.add(new GolemPatternFirstDiorite(SPAWN_BLOCK_PREDICATE));
    }

    @Override
    protected Predicate<BlockState> setupPredicates()
    {
        return state -> state != null
                && (state.is(this));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return true;
    }

    @Override
    public boolean skipRendering(BlockState pState, BlockState pAdjacentBlockState, Direction pSide)
    {
        return false;
    }
}
