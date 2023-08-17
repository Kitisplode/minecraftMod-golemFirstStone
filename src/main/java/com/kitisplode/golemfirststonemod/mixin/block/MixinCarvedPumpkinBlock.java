package com.kitisplode.golemfirststonemod.mixin.block;

import com.kitisplode.golemfirststonemod.util.golempatterns.AbstractGolemPattern;
import com.kitisplode.golemfirststonemod.util.golempatterns.GolemPatternIron;
import com.kitisplode.golemfirststonemod.util.golempatterns.GolemPatternSnow;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.function.Predicate;

@Mixin(value = CarvedPumpkinBlock.class)
public abstract class MixinCarvedPumpkinBlock extends HorizontalDirectionalBlock
{

    private ArrayList<AbstractGolemPattern> patternList = new ArrayList<>();

    protected MixinCarvedPumpkinBlock(BlockBehaviour.Properties settings)
    {
        super(settings);
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack)
    {
        if (patternList.isEmpty())
        {
            Predicate<BlockState> spawnBlocKPredicate = blockState -> blockState != null
                    && (blockState.is(Blocks.CARVED_PUMPKIN)
                    || blockState.is(Blocks.JACK_O_LANTERN));
            patternList.add(new GolemPatternIron(spawnBlocKPredicate));
            patternList.add(new GolemPatternSnow(spawnBlocKPredicate));
        }
        trySpawnGolem(pLevel, pPos, pPlacer);
    }

    @Override
    @Overwrite
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving)
    {
    }

    @Overwrite
    public boolean canSpawnGolem(LevelReader world, BlockPos pos)
    {
        return false;
    }

    public boolean trySpawnGolem(Level pLevel, BlockPos pPos, Entity pPlayer) {
        for (AbstractGolemPattern currentPattern : patternList)
        {
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
