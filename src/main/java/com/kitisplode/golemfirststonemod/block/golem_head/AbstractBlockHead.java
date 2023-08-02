package com.kitisplode.golemfirststonemod.block.golem_head;

import com.kitisplode.golemfirststonemod.util.golempatterns.AbstractGolemPattern;
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

abstract public class AbstractBlockHead extends EquipableCarvedPumpkinBlock
{
    protected ArrayList<AbstractGolemPattern> patternList = new ArrayList();
    protected Predicate<BlockState> SPAWN_BLOCK_PREDICATE;

    public AbstractBlockHead(BlockBehaviour.Properties settings) {
        super(settings);
        SPAWN_BLOCK_PREDICATE = setupPredicates();
        setupPatterns();
    }

    abstract protected void setupPatterns();

    abstract protected Predicate<BlockState> setupPredicates();

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
