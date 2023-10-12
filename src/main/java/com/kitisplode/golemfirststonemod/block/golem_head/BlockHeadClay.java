package com.kitisplode.golemfirststonemod.block.golem_head;

import com.kitisplode.golemfirststonemod.util.golempatterns.*;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

public class BlockHeadClay extends AbstractBlockHead
{
    public BlockHeadClay(Properties settings) {
        super(settings);
    }

    @Override
    protected void setupPatterns()
    {
        patternList.add(new GolemPatternIron(SPAWN_BLOCK_PREDICATE));
        patternList.add(new GolemPatternSnow(SPAWN_BLOCK_PREDICATE));
        patternList.add(new GolemPatternCobble(SPAWN_BLOCK_PREDICATE));
        patternList.add(new GolemPatternPlank(SPAWN_BLOCK_PREDICATE));
        patternList.add(new GolemPatternMossy(SPAWN_BLOCK_PREDICATE));
        patternList.add(new GolemPatternGrindstone(SPAWN_BLOCK_PREDICATE));
        patternList.add(new GolemPatternTuff(SPAWN_BLOCK_PREDICATE));
        patternList.add(new GolemPatternCopper(SPAWN_BLOCK_PREDICATE));
        patternList.add(new GolemPatternAgent(SPAWN_BLOCK_PREDICATE));
        patternList.add(new GolemPatternKey(SPAWN_BLOCK_PREDICATE));
    }

    @Override
    protected Predicate<BlockState> setupPredicates()
    {
        return state -> state != null
                && (state.is(this));
    }
}
