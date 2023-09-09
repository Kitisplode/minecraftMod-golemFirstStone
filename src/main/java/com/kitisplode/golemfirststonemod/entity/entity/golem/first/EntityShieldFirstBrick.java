package com.kitisplode.golemfirststonemod.entity.entity.golem.first;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.entity.entity.effect.AbstractEntityEffectCube;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;

import java.util.ArrayList;
import java.util.List;

public class EntityShieldFirstBrick extends AbstractEntityEffectCube implements GeoEntity
{
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final EntityDataAccessor<Integer> LIFETIME = SynchedEntityData.defineId(EntityShieldFirstBrick.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> FINALSIZE = SynchedEntityData.defineId(EntityShieldFirstBrick.class, EntityDataSerializers.FLOAT);
    private static final ResourceLocation texture = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/first_brick_shield.png");

    private final int defaultLifeTime = 20;
    private int lifeTime = defaultLifeTime;
    private float finalSize = 20.0f;

    private EntityDimensions entityDimensions = EntityDimensions.fixed(1,1);

    private ArrayList<Entity> hitEntities;
    private LivingEntity golemOwner = null;

    public EntityShieldFirstBrick(EntityType<? extends Entity> type, Level world)
    {
        super(type, world);
    }

    public EntityShieldFirstBrick(Level pWorld, double pX, double pY, double pZ)
    {
        super(ModEntities.ENTITY_EFFECT_SHIELD_FIRST_BRICK.get(), pWorld);
        setPos(pX, pY, pZ);
    }
    @Override
    protected void defineSynchedData()
    {
        this.entityData.define(LIFETIME, lifeTime);
        this.entityData.define(FINALSIZE, finalSize);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound)
    {
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound)
    {
    }

    @Override
    public ResourceLocation getTexture()
    {
        return texture;
    }

    public void setLifeTime(int pLifeTime)
    {
        lifeTime = Math.max(pLifeTime, defaultLifeTime);
        this.entityData.set(LIFETIME, lifeTime);
    }
    public int getLifeTime()
    {
        return lifeTime = this.entityData.get(LIFETIME);
    }

    public void setFullScale(float pFinalSize)
    {
        finalSize = Math.max(pFinalSize, 1.0f);
        this.entityData.set(FINALSIZE, finalSize);
    }
    public float getFullScale()
    {
        return finalSize = this.entityData.get(FINALSIZE);
    }

    public void setOwner(LivingEntity pOwner)
    {
        this.golemOwner = pOwner;
    }
    public LivingEntity getOwner()
    {
        return this.golemOwner;
    }

    @Override
    public void tick()
    {
        super.tick();
        this.getLifeTime();
        this.getFullScale();
        scaleH = Mth.lerp(0.12f, scaleH, finalSize);
        scaleY = ((float)Math.sin(((float)this.tickCount / (float)lifeTime * 180.0f) * Mth.DEG_TO_RAD) * finalSize);
        if (this.tickCount >= lifeTime)
        {
            this.discard();
        }
        if (!this.level().isClientSide())
        {
            List<Entity> entitiesColliding = this.level().getEntities(this,
                    this.getBoundingBox().inflate((scaleH - 1) / 2.0f, (scaleY - 1) / 2.0f, (scaleH - 1) / 2.0f),
                    entity -> entity instanceof Projectile);
            for (Entity entity : entitiesColliding)
            {
                this.onCollision(entity);
            }
        }
    }

    private void onCollision(Entity target)
    {
        if (hitEntities == null) hitEntities = new ArrayList<>();
        if (hitEntities.contains(target)) return;

        hitEntities.add(target);
        if (target.tickCount <= 1) return;
        if (target instanceof Projectile projectileTarget)
        {
            Entity projectileOwner = projectileTarget.getOwner();
            LivingEntity owner = this.getOwner();
            if (owner instanceof IEntityDandoriFollower dandoriOwner)
            {
                LivingEntity ownerOwner = dandoriOwner.getOwner();
                if (projectileOwner == ownerOwner) return;
                if (projectileOwner instanceof IEntityDandoriFollower dandoriFollower)
                {
                    LivingEntity projectileOwnerOwner = dandoriFollower.getOwner();
                    if (projectileOwnerOwner == ownerOwner) return;
                }
            }
        }
        Vec3 oldVelocity = target.getDeltaMovement();
        target.setDeltaMovement(-oldVelocity.x(), oldVelocity.y(), -oldVelocity.z());
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, event ->
                PlayState.CONTINUE));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }
}
