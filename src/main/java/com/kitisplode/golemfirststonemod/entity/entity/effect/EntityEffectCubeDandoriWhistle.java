package com.kitisplode.golemfirststonemod.entity.entity.effect;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;

public class EntityEffectCubeDandoriWhistle extends AbstractEntityEffectCube implements GeoEntity
{
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final EntityDataAccessor<Integer> LIFETIME = SynchedEntityData.defineId(EntityEffectCubeDandoriWhistle.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> FINALSIZE = SynchedEntityData.defineId(EntityEffectCubeDandoriWhistle.class, EntityDataSerializers.FLOAT);
    private static final ResourceLocation texture = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/dandori_whistle.png");

    private final int defaultLifeTime = 20;
    private int lifeTime = defaultLifeTime;
    private float finalSize = 20.0f;
    private LivingEntity owner = null;

    public EntityEffectCubeDandoriWhistle(EntityType<? extends Entity> type, Level world)
    {
        super(type, world);
    }

    public EntityEffectCubeDandoriWhistle(Level pWorld, double pX, double pY, double pZ)
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
        owner = pOwner;
    }

    @Override
    public void tick()
    {
        super.tick();
        if (owner != null && owner.isAlive())
        {
            Vec3 newPos = new Vec3(Mth.lerp(0.5, this.getX(), owner.getX()),
                    Mth.lerp(0.5, this.getY(), owner.getY()),
                    Mth.lerp(0.5, this.getZ(), owner.getZ()));
            setPos(newPos);
        }
        this.getLifeTime();
        this.getFullScale();
        this.scaleH = Mth.lerp(0.24f, this.scaleH, this.finalSize);
        this.scaleY = Mth.lerp(0.12f, this.scaleY, this.finalSize) / 5.0f;
        if (this.tickCount >= lifeTime)
        {
            this.discard();
        }
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
