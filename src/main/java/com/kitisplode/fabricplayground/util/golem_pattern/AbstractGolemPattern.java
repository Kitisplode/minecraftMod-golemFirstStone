package com.kitisplode.fabricplayground.util.golem_pattern;

import com.kitisplode.fabricplayground.FabricPlaygroundMod;
import com.kitisplode.fabricplayground.util.ExtraMath;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Predicate;

abstract public class AbstractGolemPattern
{
    protected ArrayList<BlockPattern> patternList = new ArrayList();
    protected Predicate<BlockState> spawnBlockPredicate;

    protected Vec3i spawnPositionOffset = new Vec3i(0,0,0);

    public AbstractGolemPattern(Predicate<BlockState> pPredicate)
    {
        spawnBlockPredicate = pPredicate;

        // Override to add patterns.
    }

    // Checks to see if the pattern is matched, and returns null if not, a PatternMatch otherwise.
    @Nullable
    public BlockPattern.Result CheckForPatternMatch(World pLevel, BlockPos pPos)
    {
        // Check to see if any of the patterns match.
        for (int i = 0; i < patternList.size(); i++)
        {
            BlockPattern currentPattern = patternList.get(i);
            // If somehow the current pattern is null, skip to the next pattern.
            if (currentPattern == null) continue;
            // Actually check the pattern now.
            BlockPattern.Result match = currentPattern.searchAround(pLevel, pPos);
            // If we got a match, return it.
            if (match != null) return match;
        }
        // If we didn't get any matches, return null.
        return null;
    }

    public Entity SpawnGolem(World pLevel, BlockPattern.Result pPatternMatch, BlockPos pPos, Entity pPlayer)
    {
        clearPatternBlocks(pLevel, pPatternMatch);

        // Spawn the golem.
        Entity pGolem = SpawnGolemForReal(pLevel, pPatternMatch, pPos);
        if (pGolem != null)
        {
            BlockPos spawnPosition = pPatternMatch.translate(spawnPositionOffset.getX(),
                            spawnPositionOffset.getY(),
                            spawnPositionOffset.getZ())
                    .getBlockPos();
            positionGolem(pLevel,
                    spawnPosition,
                    (float)ExtraMath.getYawBetweenPoints(spawnPosition.toCenterPos(), pPlayer.getPos()),
                    pGolem);
        }

        updatePatternBlocks(pLevel, pPatternMatch);
        return pGolem;
    }

    // Intended to be overridden to actually spawn the golem.
    protected abstract Entity SpawnGolemForReal(World pLevel, BlockPattern.Result pPatternMatch, BlockPos pPos);

    private void positionGolem(World pLevel, BlockPos pPos, float pYaw, Entity pGolem)
    {
//        FabricPlaygroundMod.LOGGER.info("pYaw " + pYaw);
        if (pGolem == null) return;
        pGolem.refreshPositionAndAngles((double)pPos.getX() + 0.5D, (double)pPos.getY() + 0.05D, (double)pPos.getZ() + 0.5D, pYaw, 0.0F);
        pLevel.spawnEntity(pGolem);

        for(ServerPlayerEntity serverplayer : pLevel.getNonSpectatingEntities(ServerPlayerEntity.class, pGolem.getBoundingBox().expand(5.0D))) {
            Criteria.SUMMONED_ENTITY.trigger(serverplayer, pGolem);
        }
    }

    private void clearPatternBlocks(World pLevel, BlockPattern.Result pPatternMatch)
    {
        for(int i = 0; i < pPatternMatch.getWidth(); ++i) {
            for(int j = 0; j < pPatternMatch.getHeight(); ++j) {
                for(int k = 0; k < pPatternMatch.getHeight(); ++k)
                {
                    CachedBlockPosition blockinworld = pPatternMatch.translate(i, j, k);
                    pLevel.setBlockState(blockinworld.getBlockPos(), Blocks.AIR.getDefaultState(), Block.NOTIFY_LISTENERS);
                    pLevel.syncWorldEvent(2001, blockinworld.getBlockPos(), Block.getRawIdFromState(blockinworld.getBlockState()));
                }
            }
        }
    }

    private void updatePatternBlocks(World pLevel, BlockPattern.Result pPatternMatch)
    {
        for(int i = 0; i < pPatternMatch.getWidth(); ++i) {
            for(int j = 0; j < pPatternMatch.getHeight(); ++j) {
                for(int k = 0; k < pPatternMatch.getDepth(); ++k) {
                    CachedBlockPosition blockinworld = pPatternMatch.translate(i, j, k);
                    pLevel.updateNeighbors(blockinworld.getBlockPos(), Blocks.AIR);
                }
            }
        }
    }
}