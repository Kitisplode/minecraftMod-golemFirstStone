package com.kitisplode.golemfirststonemod.entity.entity.projectile;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;

public class EntityProjectileDioriteKnowledge extends EntityProjectileAoEOwnerAware implements GeoEntity
{
    public static final Identifier TEXTURE = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/pawn/diorite/projectile_knowledge.png");

    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public EntityProjectileDioriteKnowledge(EntityType<? extends ArrowEntity> entityType, World world)
    {
        super(entityType, world);
    }

    public EntityProjectileDioriteKnowledge(World world, double x, double y, double z)
    {
        super(world, x, y, z);
    }

    public EntityProjectileDioriteKnowledge(World world, @NotNull LivingEntity owner)
    {
        super(world, owner);
    }

    public EntityProjectileDioriteKnowledge(World world, @NotNull LivingEntity owner, float pAoERange, float pDamage)
    {
        super(world, owner, pAoERange, pDamage);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult)
    {
        super.onEntityHit(entityHitResult);
        discard();
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult)
    {
        super.onBlockHit(blockHitResult);
        discard();
    }

    public Identifier getTexture()
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
