package com.kitisplode.golemfirststonemod.entity.entity.golem.story;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemCobble;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

public class EntityGolemPrison extends PathfinderMob implements Enemy, GeoEntity
{
    public static final ResourceLocation MODEL = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "geo/entity/golem/story/golem_prison.geo.json");
    public static final ResourceLocation TEXTURE = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/story/golem_prison.png");
    public static final ResourceLocation TEXTURE_GLOWMASK = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/story/golem_prison_glowmask.png");
    public static final ResourceLocation TEXTURE_LIGHT_GLOWMASK = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/story/golem_prison_light_glowmask.png");
    public static final ResourceLocation ANIMATIONS = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "animations/entity/golem/story/golem_prison.animation.json");

    private static final RawAnimation ANIMATION_WALK = RawAnimation.begin().thenLoop("animation.golem_prison.walk");
    private static final RawAnimation ANIMATION_IDLE = RawAnimation.begin().thenLoop("animation.golem_prison.idle");
    private static final RawAnimation ANIMATION_WALK_DAMAGED = RawAnimation.begin().thenLoop("animation.golem_prison.walk_damaged");
    private static final RawAnimation ANIMATION_IDLE_DAMAGED = RawAnimation.begin().thenLoop("animation.golem_prison.idle_damaged");

    private static final EntityDataAccessor<Boolean> LIGHT_ON = SynchedEntityData.defineId(EntityGolemCobble.class, EntityDataSerializers.BOOLEAN);

    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    public EntityGolemPrison(EntityType<? extends PathfinderMob> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 100)
                .add(Attributes.MOVEMENT_SPEED, 0.25f)
                .add(Attributes.ATTACK_DAMAGE, 15.0f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1f);
    }

    public static AttributeSupplier setAttributes()
    {
        return createAttributes().build();
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        if (!this.entityData.hasItem(LIGHT_ON)) this.entityData.define(LIGHT_ON, true);
    }
    public boolean getLightOn()
    {
        return this.entityData.get(LIGHT_ON);
    }
    public void setLightOn(boolean pBoolean)
    {
        this.entityData.set(LIGHT_ON, pBoolean);
    }

    public ResourceLocation getModelLocation()
    {
        return MODEL;
    }
    public ResourceLocation getTextureLocation()
    {
        return TEXTURE;
    }
    public ResourceLocation getAnimationsLocation()
    {
        return ANIMATIONS;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, event ->
        {
//            EntityGolemPrison pGolem = event.getAnimatable();
            event.getController().setAnimationSpeed(1.00);
            if (getDeltaMovement().horizontalDistanceSqr() > 0.001D || event.isMoving())
                return event.setAndContinue(ANIMATION_WALK);
            return event.setAndContinue(ANIMATION_IDLE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }
}
