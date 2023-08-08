package com.kitisplode.golemfirststonemod;

import com.kitisplode.golemfirststonemod.block.ModBlocks;
import com.kitisplode.golemfirststonemod.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;

public class GolemFirstStoneModClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        ModBlocks.registerTransparentBlocks();
        ModEntities.registerModEntitiesRenderers();
    }
}
