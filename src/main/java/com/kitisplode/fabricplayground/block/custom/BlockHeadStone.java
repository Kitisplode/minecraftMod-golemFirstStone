package com.kitisplode.fabricplayground.block.custom;

import com.kitisplode.fabricplayground.block.ModBlocks;
import com.kitisplode.fabricplayground.util.golem_pattern.AbstractGolemPattern;
import com.kitisplode.fabricplayground.util.golem_pattern.GolemPatternClay;
import com.kitisplode.fabricplayground.util.golem_pattern.GolemPatternFirstStone;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.WearableCarvedPumpkinBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Predicate;

public class BlockHeadStone extends WearableCarvedPumpkinBlock
{
    private static ArrayList<AbstractGolemPattern> patternList = new ArrayList();
    private static final Predicate<BlockState> SPAWN_BLOCK_PREDICATE = state -> state != null
            && (state.isOf(ModBlocks.BLOCK_HEAD_STONE));

    public BlockHeadStone(AbstractBlock.Settings settings) {
        super(settings);
        if (patternList.size() == 0)
        {
            patternList.add(new GolemPatternFirstStone(SPAWN_BLOCK_PREDICATE));
        }
    }

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
        for (int i = 0; i < patternList.size(); i++)
        {
            AbstractGolemPattern currentPattern = patternList.get(i);
            // Check each pattern in the pattern list.
            BlockPattern.Result match = currentPattern.CheckForPatternMatch(pLevel, pPos);
            // If there is no match, go to the next pattern.
            if (match == null) continue;
            // Otherwise, try to create the golem there.
            Entity golem = currentPattern.SpawnGolem(pLevel, match, pPos, pPlayer);
            return true;
        }
        return false;
    }
}
