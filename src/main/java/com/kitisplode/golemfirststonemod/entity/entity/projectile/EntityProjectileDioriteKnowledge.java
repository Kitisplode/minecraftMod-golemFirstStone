package com.kitisplode.golemfirststonemod.entity.entity.projectile;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;

public class EntityProjectileDioriteKnowledge extends EntityProjectileAoEOwnerAware implements GeoEntity
{
    public static final ResourceLocation TEXTURE = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/pawn/diorite/projectile_knowledge.png");

    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public EntityProjectileDioriteKnowledge(EntityType<? extends Arrow> entityType, Level world)
    {
        super(entityType, world);
    }

    public EntityProjectileDioriteKnowledge(Level world, double x, double y, double z)
    {
        super(world, x, y, z);
    }

    public EntityProjectileDioriteKnowledge(Level world, @NotNull LivingEntity owner)
    {
        super(world, owner);
    }

    public EntityProjectileDioriteKnowledge(Level world, @NotNull LivingEntity owner, float pAoERange, float pDamage)
    {
        super(world, owner, pAoERange, pDamage);
    }

    @Override
    public void onHitEntity(EntityHitResult entityHitResult)
    {
        super.onHitEntity(entityHitResult);
        discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult blockHitResult)
    {
        super.onHitBlock(blockHitResult);
        discard();
    }

    public ResourceLocation getTexture()
    {
        return TEXTURE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }
}
