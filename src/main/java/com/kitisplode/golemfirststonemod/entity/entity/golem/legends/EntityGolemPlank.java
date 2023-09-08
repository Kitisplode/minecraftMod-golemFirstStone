package com.kitisplode.golemfirststonemod.entity.entity.golem.legends;

import com.kitisplode.golemfirststonemod.entity.entity.golem.AbstractGolemDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDelayedMeleeAttack;
import com.kitisplode.golemfirststonemod.entity.entity.projectile.EntityProjectileAoEOwnerAware;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriFollowHardGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriMoveToDeployPositionGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.MultiStageAttackGoalRanged;
import com.kitisplode.golemfirststonemod.entity.goal.target.SharedTargetGoal;
import com.kitisplode.golemfirststonemod.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

public class EntityGolemPlank extends AbstractGolemDandoriFollower implements GeoEntity, IEntityWithDelayedMeleeAttack, IEntityDandoriFollower
{
    private static final TrackedData<Integer> ATTACK_STATE = DataTracker.registerData(EntityGolemPlank.class, TrackedDataHandlerRegistry.INTEGER);
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final float projectileSpeed = 3.0f;

    public EntityGolemPlank(EntityType<? extends IronGolemEntity> pEntityType, World pLevel)
    {
        super(pEntityType, pLevel);
    }

    public static DefaultAttributeContainer.Builder setAttributes()
    {
        return GolemEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 35.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5f)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 24);
    }

    @Override
    public boolean isThrowable()
    {
        return true;
    }

    @Override
    protected void initDataTracker()
    {
        super.initDataTracker();
        if (!this.dataTracker.containsKey(ATTACK_STATE))
            this.dataTracker.startTracking(ATTACK_STATE, 0);
    }

    public int getAttackState()
    {
        return this.dataTracker.get(ATTACK_STATE);
    }
    public void setAttackState(int pInt)
    {
        this.dataTracker.set(ATTACK_STATE, pInt);
    }

    private float getAttackDamage() {
        return (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
    }

    @Override
    public double getEyeY()
    {
        return getY() + 0.5f;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new DandoriFollowHardGoal(this, 1.2, Ingredient.ofItems(ModItems.ITEM_DANDORI_CALL, ModItems.ITEM_DANDORI_ATTACK), dandoriMoveRange, dandoriSeeRange));

        this.goalSelector.add(2, new MultiStageAttackGoalRanged(this, 1.0, true, MathHelper.square(20), new int[]{30, 10}, 0));
        this.goalSelector.add(2, new DandoriMoveToDeployPositionGoal(this, 2.0f, 1.0f));

        this.goalSelector.add(3, new WanderNearTargetGoal(this, 0.8, 32.0F));
        this.goalSelector.add(5, new IronGolemWanderAroundGoal(this, 0.8));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(2, new RevengeGoal(this));
        this.targetSelector.add(3, new SharedTargetGoal<>(this, GolemEntity.class, MobEntity.class, 5, false, false, entity -> entity instanceof Monster && !(entity instanceof CreeperEntity), 16));
    }

    @Override
    public boolean tryAttack()
    {
        if (getAttackState() != 2) return false;

        if (getTarget() != null)
        {
            this.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0f, 1.0f);
            attack();
        }
        return true;
    }

    public void attack()
    {
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) return;

        // Spawn the projectile
        if (!this.getWorld().isClient())
        {
            EntityProjectileAoEOwnerAware arrow = new EntityProjectileAoEOwnerAware(this.getWorld(), this, 0.0f, getAttackDamage());

            Vec3d shootingVelocity = target.getEyePos().subtract(this.getEyePos()).normalize().multiply(projectileSpeed);
            arrow.setVelocity(shootingVelocity);
            arrow.setDamage(getAttackDamage());
            arrow.setNoGravity(true);
            arrow.setHasAoE(false);
            this.getWorld().spawnEntity(arrow);
        }
    }

    @Override
    public boolean isPushable()
    {
        return getAttackState() == 0;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, event ->
        {
            EntityGolemPlank pGolem = event.getAnimatable();
            if (pGolem.getAttackState() > 0)
            {
                if (pGolem.getAttackState() == 1)
                {
                    event.getController().setAnimationSpeed(2.00);
                    return event.setAndContinue(RawAnimation.begin().then("animation.golem_plank.attack_windup", Animation.LoopType.HOLD_ON_LAST_FRAME));
                }
                event.getController().setAnimationSpeed(2.00);
                return event.setAndContinue(RawAnimation.begin().then("animation.golem_plank.attack", Animation.LoopType.HOLD_ON_LAST_FRAME));
            }
            else
            {
                event.getController().setAnimationSpeed(1.00);
                pGolem.setAttackState(0);
                if (getVelocity().horizontalLengthSquared() > 0.001D || event.isMoving())
                    return event.setAndContinue(RawAnimation.begin().thenLoop("animation.golem_plank.walk"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.golem_plank.idle"));
        }));
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }
}
