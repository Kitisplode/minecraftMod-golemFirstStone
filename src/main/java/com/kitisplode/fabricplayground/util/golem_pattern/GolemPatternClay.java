package com.kitisplode.fabricplayground.util.golem_pattern;

import com.kitisplode.fabricplayground.entity.ModEntities;
import com.kitisplode.fabricplayground.entity.custom.EntityGolemClay;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.block.BlockStatePredicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class GolemPatternClay extends AbstractGolemPattern
{
    public GolemPatternClay(Predicate<BlockState> pPredicate)
    {
        super(pPredicate);
        spawnPositionOffset = new Vec3i(0,1,0);
        patternList.add(BlockPatternBuilder.start()
                .aisle(
                        "^",
                        "#")
                .where('^', CachedBlockPosition.matchesBlockState(spawnBlockPredicate))
                .where('#', CachedBlockPosition.matchesBlockState(BlockStatePredicate.forBlock(Blocks.CLAY)))
                .build());
    }

    @Override
    protected Entity SpawnGolemForReal(World pLevel, BlockPattern.Result pPatternMatch, BlockPos pPos)
    {
        EntityGolemClay golem = ModEntities.ENTITY_GOLEM_CLAY.create(pLevel);
        if (golem != null)
        {
            positionGolem(pLevel,
                    pPatternMatch,
                    pPatternMatch.translate(spawnPositionOffset.getX(),
                                    spawnPositionOffset.getY(),
                                    spawnPositionOffset.getZ())
                            .getBlockPos(),
                    golem);
        }
        return golem;
    }
}
