package com.kitisplode.golemfirststonemod.util.golempatterns;

import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityPawn;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;

import java.util.ArrayList;
import java.util.function.Predicate;

public class GolemPatternTerracottaPink extends AbstractGolemPattern
{
	private static final Predicate<BlockState> buildingBlockPredicate = state -> state != null
		&& (state.is(Blocks.PINK_TERRACOTTA));

	public GolemPatternTerracottaPink(Predicate<BlockState> pPredicate)
	{
		super(pPredicate);
		spawnPositionOffset = new Vec3i(0,2,0);
		patternList.add(BlockPatternBuilder.start()
			.aisle(
				"^",
				"#",
				"#"
			)
			.where('^', BlockInWorld.hasState(spawnBlockPredicate))
			.where('#', BlockInWorld.hasState(buildingBlockPredicate))
			.build());
	}

	@Override
	protected ArrayList<Entity> SpawnGolemForReal(Level pLevel, BlockPattern.BlockPatternMatch pPatternMatch, BlockPos pPos)
	{
		ArrayList<Entity> golems = new ArrayList<>();
		for (int i = 0; i < 3; i++)
		{
			EntityPawn golem = ModEntities.ENTITY_PAWN_TERRACOTTA.get().create(pLevel);
			if (golem != null)
			{
				golem.setPlayerCreated(true);
				golem.setPawnType(EntityPawn.PAWN_TYPES.PIK_PINK.ordinal());
				golems.add(golem);
			}
		}
		return golems;
	}
}
