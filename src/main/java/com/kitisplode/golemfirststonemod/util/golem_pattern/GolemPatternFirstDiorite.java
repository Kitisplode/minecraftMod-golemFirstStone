package com.kitisplode.golemfirststonemod.util.golem_pattern;

import com.kitisplode.golemfirststonemod.block.ModBlocks;
import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemFirstDiorite;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemFirstStone;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class GolemPatternFirstDiorite extends AbstractGolemPattern
{
	private static final Predicate<BlockState> buildingBlockPredicate = state -> state != null
		&& (state.isOf(Blocks.DIORITE)
		|| state.isOf(Blocks.POLISHED_DIORITE));

	private static final Predicate<BlockState> airPredicate = state -> state != null
			&& (state.isAir()
			|| !state.isOpaque()
	);

	private static final Predicate<BlockState> corePredicate = state -> state != null
			&& (state.isOf(ModBlocks.BLOCK_CORE_DIORITE)
	);

	public GolemPatternFirstDiorite(Predicate<BlockState> pPredicate)
	{
		super(pPredicate);
		spawnPositionOffset = new Vec3i(1,2,1);
		patternList.add(BlockPatternBuilder.start()
			.aisle(
				"###",
				"#^#",
				"###",
				"~~~"
			)
			.aisle(
				"###",
				"#*#",
				"###",
				"#~#"
			)
			.aisle(
				"###",
				"###",
				"###",
				"~~~"
			)
			.where('^', CachedBlockPosition.matchesBlockState(spawnBlockPredicate))
			.where('#', CachedBlockPosition.matchesBlockState(buildingBlockPredicate))
			.where('~', CachedBlockPosition.matchesBlockState(airPredicate))
			.where('*', CachedBlockPosition.matchesBlockState(corePredicate))
			.build());
		// Mirrored version
		patternList.add(BlockPatternBuilder.start()
			.aisle(
				"###",
				"###",
				"###",
				"~#~"
			)
			.aisle(
				"###",
				"^*#",
				"###",
				"~~~"
			)
			.aisle(
				"###",
				"###",
				"###",
				"~#~"
			)
			.where('^', CachedBlockPosition.matchesBlockState(spawnBlockPredicate))
			.where('#', CachedBlockPosition.matchesBlockState(buildingBlockPredicate))
			.where('~', CachedBlockPosition.matchesBlockState(airPredicate))
			.where('*', CachedBlockPosition.matchesBlockState(corePredicate))
			.build());
	}

	@Override
	protected Entity SpawnGolemForReal(World pLevel, BlockPattern.Result pPatternMatch, BlockPos pPos)
	{
		EntityGolemFirstDiorite golem = ModEntities.ENTITY_GOLEM_FIRST_DIORITE.create(pLevel);
		if (golem != null)
		{
			golem.setPlayerCreated(true);
		}
		return golem;
	}
}
