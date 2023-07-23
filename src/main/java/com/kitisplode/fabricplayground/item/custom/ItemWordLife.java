package com.kitisplode.fabricplayground.item.custom;

import com.kitisplode.fabricplayground.util.golem_pattern.AbstractGolemPattern;
import com.kitisplode.fabricplayground.util.golem_pattern.GolemPatternClay;
import com.kitisplode.fabricplayground.util.golem_pattern.GolemPatternFirstStone;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.function.Predicate;

public class ItemWordLife extends Item
{
    private static ArrayList<AbstractGolemPattern> patternList = new ArrayList();
    private static final Predicate<BlockState> SPAWN_BLOCK_PREDICATE = state -> state != null
            && (state.isOf(Blocks.CARVED_PUMPKIN) || state.isOf(Blocks.JACK_O_LANTERN));

    public ItemWordLife(Settings pSettings)
    {
        super(pSettings);
        if (patternList.size() == 0)
        {
            patternList.add(new GolemPatternClay(SPAWN_BLOCK_PREDICATE));
            patternList.add(new GolemPatternFirstStone(SPAWN_BLOCK_PREDICATE));
        }
    }

    @Override @NotNull
    public TypedActionResult<ItemStack> use(World pLevel, PlayerEntity pPlayer, Hand pHand)
    {
        BlockHitResult ray = this.raycast(pLevel, pPlayer, RaycastContext.FluidHandling.NONE);
        BlockPos lookPos = ray.getBlockPos();

        if (trySpawnGolem(pLevel, lookPos))
        {
            pPlayer.getStackInHand(pHand).decrement(1);
            return TypedActionResult.success(pPlayer.getStackInHand(pHand), true);
        }

        return TypedActionResult.fail(pPlayer.getStackInHand(pHand));
    }

    private boolean trySpawnGolem(World pLevel, BlockPos pPos) {
        for (int i = 0; i < patternList.size(); i++)
        {
            AbstractGolemPattern currentPattern = patternList.get(i);
            // Check each pattern in the pattern list.
            BlockPattern.Result match = currentPattern.CheckForPatternMatch(pLevel, pPos);
            // If there is no match, go to the next pattern.
            if (match == null) continue;
            // Otherwise, try to create the golem there.
            currentPattern.SpawnGolem(pLevel, match, pPos);
            return true;
        }
        return false;
    }
}
