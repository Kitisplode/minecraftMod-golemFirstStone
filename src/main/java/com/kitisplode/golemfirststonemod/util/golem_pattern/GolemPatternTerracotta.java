package com.kitisplode.golemfirststonemod.util.golem_pattern;

import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.entity.entity.EntityPawn;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPatternBuilder;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.function.Predicate;

public class GolemPatternTerracotta extends AbstractGolemPattern
{
	private static final Predicate<BlockState> buildingBlockPredicate = state -> state != null
		&& (state.isOf(Blocks.TERRACOTTA));

	public GolemPatternTerracotta(Predicate<BlockState> pPredicate)
	{
		super(pPredicate);
		spawnPositionOffset = new Vec3i(0,1,0);
		patternList.add(BlockPatternBuilder.start()
			.aisle(
				"^",
				"#"
			)
			.where('^', CachedBlockPosition.matchesBlockState(spawnBlockPredicate))
			.where('#', CachedBlockPosition.matchesBlockState(buildingBlockPredicate))
			.build());
	}

	@Override
	protected ArrayList<Entity> SpawnGolemForReal(World pLevel, BlockPattern.Result pPatternMatch, BlockPos pPos)
	{
		ArrayList<Entity> golems = new ArrayList<>();
		for (int i = 0; i < 3; i++)
		{
			EntityPawn golem = ModEntities.ENTITY_PAWN_FIRST_DIORITE.create(pLevel);
			if (golem != null)
			{
				golem.setPlayerCreated(true);
				golem.setPawnTypePik();
				golems.add(golem);
			}
		}
		return golems;
	}
}
