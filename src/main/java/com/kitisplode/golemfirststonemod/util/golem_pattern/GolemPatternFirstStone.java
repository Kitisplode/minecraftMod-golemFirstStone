package com.kitisplode.golemfirststonemod.util.golem_pattern;

import com.kitisplode.golemfirststonemod.block.ModBlocks;
import com.kitisplode.golemfirststonemod.entity.ModEntities;
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

public class GolemPatternFirstStone extends AbstractGolemPattern
{
	private static final Predicate<BlockState> buildingBlockPredicate = state -> state != null
		&& (state.isOf(Blocks.STONE)
		|| state.isOf(Blocks.MOSSY_COBBLESTONE)
		|| state.isOf(Blocks.COBBLESTONE)
		|| state.isOf(Blocks.STONE_BRICKS)
		|| state.isOf(Blocks.CRACKED_STONE_BRICKS)
		|| state.isOf(Blocks.CHISELED_STONE_BRICKS)
		|| state.isOf(Blocks.MOSSY_STONE_BRICKS)
		|| state.isOf(Blocks.ANDESITE));

	private static final Predicate<BlockState> airPredicate = state -> state != null
			&& (state.isAir()
			|| !state.isOpaque()
	);

	private static final Predicate<BlockState> corePredicate = state -> state != null
			&& (state.isOf(ModBlocks.BLOCK_CORE_STONE)
	);

	public GolemPatternFirstStone(Predicate<BlockState> pPredicate)
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
		EntityGolemFirstStone golem = ModEntities.ENTITY_GOLEM_FIRST_STONE.create(pLevel);
		if (golem != null)
		{
			golem.setPlayerCreated(true);
		}
		return golem;
	}
}
