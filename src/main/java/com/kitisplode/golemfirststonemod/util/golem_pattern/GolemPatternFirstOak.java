package com.kitisplode.golemfirststonemod.util.golem_pattern;

import com.kitisplode.golemfirststonemod.block.ModBlocks;
import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.entity.custom.EntityGolemFirstOak;
import com.kitisplode.golemfirststonemod.entity.custom.EntityGolemFirstStone;
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

public class GolemPatternFirstOak extends AbstractGolemPattern
{
	private static final Predicate<BlockState> buildingBlockPredicate = state -> state != null
		&& (state.isOf(Blocks.OAK_PLANKS)
		|| state.isOf(Blocks.OAK_LOG)
		|| state.isOf(Blocks.STRIPPED_OAK_LOG)
		|| state.isOf(Blocks.STRIPPED_OAK_WOOD)
		|| state.isOf(Blocks.OAK_WOOD));

	private static final Predicate<BlockState> airPredicate = state -> state != null
			&& (state.isAir()
			|| !state.isOpaque()
	);

	private static final Predicate<BlockState> corePredicate = state -> state != null
			&& (state.isOf(ModBlocks.BLOCK_CORE_OAK)
	);

	public GolemPatternFirstOak(Predicate<BlockState> pPredicate)
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
		EntityGolemFirstOak golem = ModEntities.ENTITY_GOLEM_FIRST_OAK.create(pLevel);
		if (golem != null)
		{
			golem.setPlayerCreated(true);
		}
		return golem;
	}
}
