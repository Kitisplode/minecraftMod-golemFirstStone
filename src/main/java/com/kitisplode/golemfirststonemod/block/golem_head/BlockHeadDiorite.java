package com.kitisplode.golemfirststonemod.block.golem_head;

import com.kitisplode.golemfirststonemod.util.golem_pattern.GolemPatternFirstDiorite;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.function.Predicate;

public class BlockHeadDiorite extends AbstractBlockHead
{
    public BlockHeadDiorite(Settings settings)
    {
        super(settings);
    }

    protected void setupPatterns()
    {
        if (patternList.size() == 0)
        {
            patternList.add(new GolemPatternFirstDiorite(SPAWN_BLOCK_PREDICATE));
        }
    }

    protected Predicate<BlockState> setupPredicates()
    {
        return state -> state != null
                && (state.isOf(this));
    }

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        return false;
    }
}
