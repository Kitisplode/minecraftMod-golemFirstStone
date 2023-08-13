package com.kitisplode.golemfirststonemod.util.golem_pattern;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class GolemPatternSnow extends AbstractGolemPattern
{
	private static final Predicate<BlockState> buildingBlockPredicate = state -> state != null
		&& (state.isOf(Blocks.SNOW_BLOCK));

	public GolemPatternSnow(Predicate<BlockState> pPredicate)
	{
		super(pPredicate);
		spawnPositionOffset = new Vec3i(0,2,0);
		patternList.add(BlockPatternBuilder.start()
			.aisle(
				"^",
				"#",
				"#"
			)
			.where('^', CachedBlockPosition.matchesBlockState(spawnBlockPredicate))
			.where('#', CachedBlockPosition.matchesBlockState(buildingBlockPredicate))
			.build());
	}

	@Override
	protected Entity SpawnGolemForReal(World pLevel, BlockPattern.Result pPatternMatch, BlockPos pPos)
	{
		SnowGolemEntity golem = EntityType.SNOW_GOLEM.create(pLevel);
		if (golem != null)
		{
//			golem.setPlayerCreated(true);
		}
		return golem;
	}
}
