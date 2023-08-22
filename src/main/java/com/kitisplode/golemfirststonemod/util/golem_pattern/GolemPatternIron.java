package com.kitisplode.golemfirststonemod.util.golem_pattern;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.function.Predicate;

public class GolemPatternIron extends AbstractGolemPattern
{
	private static final Predicate<BlockState> buildingBlockPredicate = state -> state != null
		&& (state.isOf(Blocks.IRON_BLOCK));

	private static final Predicate<BlockState> airPredicate = state -> state != null
			&& (state.isAir()
			|| !state.isOpaque()
			|| state.isOf(Blocks.SNOW)
	);

	public GolemPatternIron(Predicate<BlockState> pPredicate)
	{
		super(pPredicate);
		spawnPositionOffset = new Vec3i(1,2,0);
		patternList.add(BlockPatternBuilder.start()
			.aisle(
				"~^~",
				"###",
				"~#~"
			)
			.where('^', CachedBlockPosition.matchesBlockState(spawnBlockPredicate))
			.where('#', CachedBlockPosition.matchesBlockState(buildingBlockPredicate))
			.where('~', CachedBlockPosition.matchesBlockState(airPredicate))
			.build());
	}

	@Override
	protected ArrayList<Entity> SpawnGolemForReal(World pLevel, BlockPattern.Result pPatternMatch, BlockPos pPos)
	{
		ArrayList<Entity> golems = new ArrayList<>();
		IronGolemEntity golem = EntityType.IRON_GOLEM.create(pLevel);
		if (golem != null)
		{
			golem.setPlayerCreated(true);
			golems.add(golem);
		}
		return golems;
	}
}
