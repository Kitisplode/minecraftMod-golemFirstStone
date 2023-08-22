package com.kitisplode.golemfirststonemod.block.golem_head;

import com.kitisplode.golemfirststonemod.util.golem_pattern.AbstractGolemPattern;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.WearableCarvedPumpkinBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Predicate;

abstract public class AbstractBlockHead extends WearableCarvedPumpkinBlock
{
    protected ArrayList<AbstractGolemPattern> patternList = new ArrayList<>();
    protected Predicate<BlockState> SPAWN_BLOCK_PREDICATE;

    public AbstractBlockHead(AbstractBlock.Settings settings) {
        super(settings);
        SPAWN_BLOCK_PREDICATE = setupPredicates();
        setupPatterns();
    }

    abstract protected void setupPatterns();

    abstract protected Predicate<BlockState> setupPredicates();

    public boolean canDispense(WorldView world, BlockPos pos)
    {
        return false;
    }

    @Override
    public void onPlaced(World pWorld, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlayer, ItemStack pItemStack)
    {
        this.trySpawnGolem(pWorld, pPos, pPlayer);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        return;
    }

    private boolean trySpawnGolem(World pLevel, BlockPos pPos, Entity pPlayer) {
        for (AbstractGolemPattern currentPattern : patternList)
        {
            // Check each pattern in the pattern list.
            BlockPattern.Result match = currentPattern.CheckForPatternMatch(pLevel, pPos);
            // If there is no match, go to the next pattern.
            if (match == null) continue;
            // Otherwise, try to create the golem there.
            currentPattern.SpawnGolem(pLevel, match, pPos, pPlayer);
            return true;
        }
        return false;
    }
}
