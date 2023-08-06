package com.kitisplode.golemfirststonemod.util.golempatterns;

import com.kitisplode.golemfirststonemod.block.ModBlocks;
import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemFirstBrick;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemFirstStone;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;

import java.util.function.Predicate;

public class GolemPatternFirstBrick extends AbstractGolemPattern
{
	private static final Predicate<BlockState> buildingBlockPredicate = state -> state != null
		&& (state.is(Blocks.BRICKS)
		|| state.is(Blocks.MUD_BRICKS)
		|| state.is(Blocks.NETHER_BRICKS)
		|| state.is(Blocks.RED_NETHER_BRICKS)
		|| state.is(Blocks.CRACKED_NETHER_BRICKS)
		|| state.is(Blocks.GRANITE)
		|| state.is(Blocks.POLISHED_GRANITE));

	private static final Predicate<BlockState> airPredicate = state -> state != null
			&& (state.isAir()
			|| !state.canOcclude()
	);

	private static final Predicate<BlockState> corePredicate = state -> state != null
			&& (state.is(ModBlocks.BLOCK_CORE_BRICK.get())
	);

	public GolemPatternFirstBrick(Predicate<BlockState> pPredicate)
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
			.where('^', BlockInWorld.hasState(spawnBlockPredicate))
			.where('#', BlockInWorld.hasState(buildingBlockPredicate))
			.where('~', BlockInWorld.hasState(airPredicate))
			.where('*', BlockInWorld.hasState(corePredicate))
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
			.where('^', BlockInWorld.hasState(spawnBlockPredicate))
			.where('#', BlockInWorld.hasState(buildingBlockPredicate))
			.where('~', BlockInWorld.hasState(airPredicate))
			.where('*', BlockInWorld.hasState(corePredicate))
			.build());
	}

	@Override
	protected Entity SpawnGolemForReal(Level pLevel, BlockPattern.BlockPatternMatch pPatternMatch, BlockPos pPos)
	{
		EntityGolemFirstBrick golem = ModEntities.ENTITY_GOLEM_FIRST_BRICK.get().create(pLevel);
		if (golem != null)
		{
			golem.setPlayerCreated(true);
		}
		return golem;
	}
}
