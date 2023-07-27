package com.kitisplode.golemfirststonemod.block.custom;

import com.kitisplode.golemfirststonemod.block.ModBlocks;
import com.kitisplode.golemfirststonemod.util.golempatterns.AbstractGolemPattern;
import com.kitisplode.golemfirststonemod.util.golempatterns.GolemPatternFirstStone;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.EquipableCarvedPumpkinBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockPattern;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.function.Predicate;

public class BlockHeadStone extends EquipableCarvedPumpkinBlock
{
    private static ArrayList<AbstractGolemPattern> patternList = new ArrayList();
    private static final Predicate<BlockState> SPAWN_BLOCK_PREDICATE = state -> state != null
            && (state.is(ModBlocks.BLOCK_HEAD_STONE.get()));

    public BlockHeadStone(BlockBehaviour.Properties settings) {
        super(settings);
        if (patternList.size() == 0)
        {
            patternList.add(new GolemPatternFirstStone(SPAWN_BLOCK_PREDICATE));
        }
    }

    @Override
    public boolean canSpawnGolem(LevelReader pLevel, BlockPos pPos)
    {
        return false;
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlayer, ItemStack pStack)
    {
        this.trySpawnGolem(pLevel, pPos, pPlayer);
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving)
    {
    }

    private boolean trySpawnGolem(Level pLevel, BlockPos pPos, Entity pPlayer) {
        for (int i = 0; i < patternList.size(); i++)
        {
            AbstractGolemPattern currentPattern = patternList.get(i);
            // Check each pattern in the pattern list.
            BlockPattern.BlockPatternMatch match = currentPattern.CheckForPatternMatch(pLevel, pPos);
            // If there is no match, go to the next pattern.
            if (match == null) continue;
            // Otherwise, try to create the golem there.
            Entity golem = currentPattern.SpawnGolem(pLevel, match, pPos, pPlayer);
            return true;
        }
        return false;
    }
}
