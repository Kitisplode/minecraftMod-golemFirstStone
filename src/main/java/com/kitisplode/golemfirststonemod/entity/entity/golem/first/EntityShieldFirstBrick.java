package com.kitisplode.golemfirststonemod.entity.entity.golem.first;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.entity.entity.effect.AbstractEntityEffectCube;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ProjectileEntity;
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

import java.util.ArrayList;
import java.util.List;

public class EntityShieldFirstBrick extends AbstractEntityEffectCube implements GeoEntity
{
    protected AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final TrackedData<Integer> LIFETIME = DataTracker.registerData(EntityShieldFirstBrick.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> FINALSIZE = DataTracker.registerData(EntityShieldFirstBrick.class, TrackedDataHandlerRegistry.FLOAT);
    private static final Identifier texture = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/first_brick_shield.png");

    private final int defaultLifeTime = 20;
    private int lifeTime = defaultLifeTime;
    private float finalSize = 20.0f;

    private EntityDimensions entityDimensions = EntityDimensions.fixed(1,1);

    private ArrayList<Entity> hitEntities;
    private LivingEntity golemOwner = null;

    public EntityShieldFirstBrick(EntityType<? extends Entity> type, World world)
    {
        super(type, world);
    }

    public EntityShieldFirstBrick(World pWorld, double pX, double pY, double pZ)
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
        scaleH = MathHelper.lerp(0.12f, scaleH, finalSize);
        scaleY = ((float)Math.sin(((float)this.age / (float)lifeTime * 180.0f) * MathHelper.RADIANS_PER_DEGREE) * finalSize);
        if (this.age >= lifeTime)
        {
            this.discard();
        }

        if (!this.getWorld().isClient())
        {
            List<Entity> entitiesColliding = this.getWorld().getOtherEntities(this,
                    this.getBoundingBox().expand(scaleH / 2.0f, scaleY / 2.0f, scaleH / 2.0f),
                    entity -> entity instanceof ProjectileEntity);
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
        if (target.age <= 1) return;
        if (target instanceof ProjectileEntity projectileTarget)
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
        Vec3d oldVelocity = target.getVelocity();
        target.setVelocity(-oldVelocity.getX(), oldVelocity.getY(), -oldVelocity.getZ());
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
