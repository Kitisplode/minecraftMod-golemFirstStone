package com.kitisplode.fabricplayground;

import com.kitisplode.fabricplayground.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;

public class FabricPlaygroundModClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        ModEntities.registerModEntitiesRenderers();
    }
}
