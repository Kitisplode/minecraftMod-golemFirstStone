package com.kitisplode.golemfirststonemod.entity.client.model.first.diorite_pawns;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.projectile.EntityProjectileDioriteKnowledge;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class EntityModelProjectileDioriteKnowledge extends GeoModel<EntityProjectileDioriteKnowledge>
{
    @Override
    public ResourceLocation getModelResource(EntityProjectileDioriteKnowledge animatable)
    {
        return new ResourceLocation(GolemFirstStoneMod.MOD_ID, "geo/first_brick_shield.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(EntityProjectileDioriteKnowledge animatable)
    {
        return animatable.getTexture();
    }

    @Override
    public ResourceLocation getAnimationResource(EntityProjectileDioriteKnowledge animatable)
    {
        return null;
    }
}
