package com.kitisplode.golemfirststonemod.util.golempatterns;

import com.kitisplode.golemfirststonemod.block.ModBlocks;
import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemFirstStone;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;

import java.util.ArrayList;
import java.util.function.Predicate;

public class GolemPatternIron extends AbstractGolemPattern
{
	private static final Predicate<BlockState> buildingBlockPredicate = state -> state != null
		&& (state.is(Blocks.IRON_BLOCK));

	private static final Predicate<BlockState> airPredicate = state -> state != null
			&& (state.isAir()
			|| !state.canOcclude()
			|| state.is(Blocks.SNOW)
	);

	public GolemPatternIron(Predicate<BlockState> pPredicate)
	{
		super(pPredicate);
		spawnPositionOffset = new Vec3i(1,2,1);
		patternList.add(BlockPatternBuilder.start()
			.aisle(
				"~^~",
				"###",
				"~#~"
			)
			.where('^', BlockInWorld.hasState(spawnBlockPredicate))
			.where('#', BlockInWorld.hasState(buildingBlockPredicate))
			.where('~', BlockInWorld.hasState(airPredicate))
			.build());
	}

	@Override
	protected ArrayList<Entity> SpawnGolemForReal(Level pLevel, BlockPattern.BlockPatternMatch pPatternMatch, BlockPos pPos)
	{
		ArrayList<Entity> golems = new ArrayList<>();
		IronGolem golem = EntityType.IRON_GOLEM.create(pLevel);
		if (golem != null)
		{
			golem.setPlayerCreated(true);
			golems.add(golem);
		}
		return golems;
	}
}
