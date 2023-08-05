package com.kitisplode.golemfirststonemod.entity.entity.effect;

import com.kitisplode.golemfirststonemod.entity.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;

public class EntityEffectShieldFirstBrick extends Entity implements GeoEntity
{
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final TrackedData<Integer> LIFETIME = DataTracker.registerData(EntityEffectShieldFirstBrick.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> FINALSIZE = DataTracker.registerData(EntityEffectShieldFirstBrick.class, TrackedDataHandlerRegistry.FLOAT);
    private final int defaultLifeTime = 20;
    private int lifeTime = defaultLifeTime;
    private float finalSize = 20.0f;
    private float scaleH = 1.0f;
    private float scaleY = 1.0f;

    public EntityEffectShieldFirstBrick(EntityType<? extends Entity> type, World world)
    {
        super(type, world);
    }

    public EntityEffectShieldFirstBrick(World pWorld, double pX, double pY, double pZ)
    {
        super(ModEntities.ENTITY_SHIELD_FIRST_BRICK, pWorld);
        setPosition(pX, pY, pZ);
    }

    @Override
    protected void initDataTracker()
    {
        this.dataTracker.startTracking(LIFETIME, lifeTime);
        this.dataTracker.startTracking(FINALSIZE, finalSize);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt)
    {
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt)
    {
    }

    public void setLifeTime(int pLifeTime)
    {
        lifeTime = Math.max(pLifeTime, defaultLifeTime);
        this.dataTracker.set(LIFETIME, lifeTime);
    }
    public int getLifeTime()
    {
        return lifeTime = this.dataTracker.get(LIFETIME);
    }

    public void setFullScale(float pFinalSize)
    {
        finalSize = Math.max(pFinalSize, 1.0f);
        this.dataTracker.set(FINALSIZE, finalSize);
    }
    public float getFullScale()
    {
        return finalSize = this.dataTracker.get(FINALSIZE);
    }

    public float getScaleH()
    {
        return scaleH;
    }

    public float getScaleY()
    {
        return scaleY;
    }

    @Override
    public void tick()
    {
        super.tick();
        this.getLifeTime();
        this.getFullScale();
        scaleH = MathHelper.lerp(0.12f, scaleH, finalSize);
        scaleY = (float)Math.sin(((float)this.age / (float)lifeTime * 180.0f) * MathHelper.RADIANS_PER_DEGREE) * finalSize;
        if (this.age >= lifeTime)
        {
            this.kill();
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, event ->
                PlayState.CONTINUE));
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }
}
