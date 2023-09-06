package com.kitisplode.golemfirststonemod.entity.entity.effect;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;

public class EntityEffectCubeDandoriWhistle extends AbstractEntityEffectCube implements GeoEntity
{
    protected AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final TrackedData<Integer> LIFETIME = DataTracker.registerData(EntityEffectCubeDandoriWhistle.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> FINALSIZE = DataTracker.registerData(EntityEffectCubeDandoriWhistle.class, TrackedDataHandlerRegistry.FLOAT);
    private static final Identifier texture = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/dandori_whistle.png");

    private final int defaultLifeTime = 20;
    private int lifeTime = defaultLifeTime;
    private float finalSize = 20.0f;
    private LivingEntity owner = null;

    public EntityEffectCubeDandoriWhistle(EntityType<? extends Entity> type, World world)
    {
        super(type, world);
    }

    public EntityEffectCubeDandoriWhistle(World pWorld, double pX, double pY, double pZ)
    {
        super(ModEntities.ENTITY_EFFECT_SHIELD_FIRST_BRICK, pWorld);
        setPosition(pX, pY, pZ);
    }

    @Override
    protected void initDataTracker()
    {
        this.dataTracker.startTracking(LIFETIME, lifeTime);
        this.dataTracker.startTracking(FINALSIZE, finalSize);
    }

    @Override
    public Identifier getTexture()
    {
        return texture;
    }

    public void setLifeTime(int pLifeTime)
    {
        this.lifeTime = Math.max(pLifeTime, defaultLifeTime);
        this.dataTracker.set(LIFETIME, lifeTime);
    }
    public int getLifeTime()
    {
        return this.lifeTime = this.dataTracker.get(LIFETIME);
    }

    public void setFullScale(float pFinalSize)
    {
        this.finalSize = Math.max(pFinalSize, 1.0f);
        this.dataTracker.set(FINALSIZE, finalSize);
    }
    public float getFullScale()
    {
        return this.finalSize = this.dataTracker.get(FINALSIZE);
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
            Vec3d newPos = new Vec3d(MathHelper.lerp(0.5, this.getX(), owner.getX()),
                    MathHelper.lerp(0.5, this.getY(), owner.getY()),
                    MathHelper.lerp(0.5, this.getZ(), owner.getZ()));
            setPosition(newPos);
        }
        this.getLifeTime();
        this.getFullScale();
        this.scaleH = MathHelper.lerp(0.24f, this.scaleH, this.finalSize);
        this.scaleY = MathHelper.lerp(0.12f, this.scaleY, this.finalSize) / 5.0f;
        if (this.age >= this.lifeTime)
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

    @Override public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }
}
