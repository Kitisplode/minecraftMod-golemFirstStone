package com.kitisplode.fabricplayground.entity;

import com.kitisplode.fabricplayground.FabricPlaygroundMod;
import com.kitisplode.fabricplayground.entity.client.EntityRendererGolemClay;
import com.kitisplode.fabricplayground.entity.client.EntityRendererGolemFirstStone;
import com.kitisplode.fabricplayground.entity.custom.EntityGolemClay;
import com.kitisplode.fabricplayground.entity.custom.EntityGolemFirstStone;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities
{
    public static final EntityType<EntityGolemClay> ENTITY_GOLEM_CLAY = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(FabricPlaygroundMod.MOD_ID, "entity_golem_clay"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, EntityGolemClay::new)
                    .dimensions(EntityDimensions.fixed(0.5f, 0.5f))
                    .build()
    );
    public static final EntityType<EntityGolemFirstStone> ENTITY_GOLEM_FIRST_STONE = Registry.register(
            Registries.ENTITY_TYPE, new Identifier(FabricPlaygroundMod.MOD_ID, "entity_golem_first_stone"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, EntityGolemFirstStone::new)
                    .dimensions(EntityDimensions.fixed(2.5f, 4.0f))
                    .build()
    );

    public static void registerModEntities()
    {
        FabricDefaultAttributeRegistry.register(ENTITY_GOLEM_CLAY, EntityGolemClay.setAttributes());
        FabricDefaultAttributeRegistry.register(ENTITY_GOLEM_FIRST_STONE, EntityGolemFirstStone.setAttributes());
    }

    public static void registerModEntitiesRenderers()
    {
        EntityRendererRegistry.register(ENTITY_GOLEM_CLAY, EntityRendererGolemClay::new);
        EntityRendererRegistry.register(ENTITY_GOLEM_FIRST_STONE, EntityRendererGolemFirstStone::new);
    }
}