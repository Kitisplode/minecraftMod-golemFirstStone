package com.kitisplode.golemfirststonemod.mixin.block;

import com.kitisplode.golemfirststonemod.util.golem_pattern.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.ArrayList;
import java.util.function.Predicate;

@Mixin(value = CarvedPumpkinBlock.class)
public abstract class MixinCarvedPumpkinBlock extends HorizontalFacingBlock
{
    private ArrayList<AbstractGolemPattern> patternList = new ArrayList<>();

    protected MixinCarvedPumpkinBlock(Settings settings)
    {
        super(settings);
    }

    @Override
    public void onPlaced(World pWorld, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlayer, ItemStack pItemStack)
    {
        if (patternList.isEmpty())
        {
            Predicate<BlockState> spawnBlocKPredicate = blockState -> blockState != null
                    && (blockState.isOf(Blocks.CARVED_PUMPKIN)
                    || blockState.isOf(Blocks.JACK_O_LANTERN));
            patternList.add(new GolemPatternIron(spawnBlocKPredicate));
            patternList.add(new GolemPatternSnow(spawnBlocKPredicate));
            patternList.add(new GolemPatternTerracotta(spawnBlocKPredicate));
            patternList.add(new GolemPatternTerracottaBlue(spawnBlocKPredicate));
            patternList.add(new GolemPatternTerracottaPink(spawnBlocKPredicate));
            patternList.add(new GolemPatternTerracottaYellow(spawnBlocKPredicate));
            patternList.add(new GolemPatternCobble(spawnBlocKPredicate));
            patternList.add(new GolemPatternPlank(spawnBlocKPredicate));
            patternList.add(new GolemPatternMossy(spawnBlocKPredicate));
        }
        trySpawnGolem(pWorld, pPos, pPlayer);
    }

    @Override
    @Overwrite
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        return;
    }

    @Overwrite
    public boolean canDispense(WorldView world, BlockPos pos)
    {
        return false;
    }

    public boolean trySpawnGolem(World pLevel, BlockPos pPos, Entity pPlayer) {
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
