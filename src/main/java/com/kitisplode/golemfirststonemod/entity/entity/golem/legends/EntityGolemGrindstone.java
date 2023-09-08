package com.kitisplode.golemfirststonemod.entity.entity.golem.legends;

import com.kitisplode.golemfirststonemod.entity.entity.golem.AbstractGolemDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityPawn;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.EntityGolemFirstDiorite;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDelayedMeleeAttack;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriFollowHardGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriFollowSoftGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriMoveToDeployPositionGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.MultiStageAttackGoalRanged;
import com.kitisplode.golemfirststonemod.entity.goal.target.SharedTargetGoal;
import com.kitisplode.golemfirststonemod.item.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

public class EntityGolemGrindstone extends AbstractGolemDandoriFollower implements GeoEntity, IEntityWithDelayedMeleeAttack, IEntityDandoriFollower
{
    private static final TrackedData<Integer> ATTACK_STATE = DataTracker.registerData(EntityGolemGrindstone.class, TrackedDataHandlerRegistry.INTEGER);
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final float attackSpeed = 0.7f;
    private boolean movingBackwards = false;
    private MultiStageAttackGoalRanged attackGoal;

    private static final StatusEffectInstance stunEffect = new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 2, false, false);
    private static final StatusEffectInstance armorEffect = new StatusEffectInstance(StatusEffects.RESISTANCE, 15, 3, false, false);

//    private int steeringAngle = 0;
//    private int steeringVelocity = 0;
//    private static final int steeringAccel = 1;
//    private static final int steeringVelocityMax = 10;
//    private double turningVelocity = 0;
//    private static final double turningAccel = 1;
//    private static final double turningVelocityMax = 10;

    public EntityGolemGrindstone(EntityType<? extends IronGolemEntity> pEntityType, World pLevel)
    {
        super(pEntityType, pLevel);
    }

    public static DefaultAttributeContainer.Builder setAttributes()
    {
        return GolemEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 65.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.75f);
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
        if (!this.dataTracker.containsKey(ATTACK_STATE)) this.dataTracker.startTracking(ATTACK_STATE, 0);
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
    protected void initGoals()
    {
        this.attackGoal = new MultiStageAttackGoalRanged(this, 1.0, true, MathHelper.square(12.0d), new int[]{70, 40, 20});
        this.goalSelector.add(1, new DandoriFollowHardGoal(this, 1.2, dandoriMoveRange, dandoriSeeRange));

        this.goalSelector.add(2, this.attackGoal);
        this.goalSelector.add(2, new DandoriMoveToDeployPositionGoal(this, 2.0f, 1.0f));

        this.goalSelector.add(3, new DandoriFollowSoftGoal(this, 1.2, dandoriMoveRange, dandoriSeeRange));

        this.goalSelector.add(3, new WanderNearTargetGoal(this, 0.8, 32.0F));
        this.goalSelector.add(5, new IronGolemWanderAroundGoal(this, 0.8));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(7, new LookAtEntityGoal(this, MerchantEntity.class, 8.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(2, new RevengeGoal(this));
        this.targetSelector.add(3, new SharedTargetGoal<>(this, GolemEntity.class, MobEntity.class, 5, false, false, entity -> entity instanceof Monster && !(entity instanceof CreeperEntity), 5));
    }

    @Override
    public boolean tryAttack()
    {
        if (getAttackState() != 2) return false;

        this.addStatusEffect(new StatusEffectInstance(armorEffect));
        return true;
    }

    @Override
    public void tick()
    {
        super.tick();
        // If we're attacking, move forward.
        if (getAttackState() == 2)
        {
            float angle = this.getYaw()*MathHelper.RADIANS_PER_DEGREE;
            Vec3d newVelocity = new Vec3d(-Math.sin(angle), 0, Math.cos(angle)).multiply(attackSpeed);
            this.setVelocity(newVelocity);
        }
    }

    @Override
    public void pushAwayFrom(Entity target)
    {
        if (getAttackState() == 2)
        {
            // Do not damage targets that are our owner or are owned by our owner.
            if (this.getOwner() == target) return;
            if (target instanceof TameableEntity && ((TameableEntity)target).getOwner() == this.getOwner()) return;
            if (target instanceof IEntityDandoriFollower && ((IEntityDandoriFollower)target).getOwner() == this.getOwner()) return;
            // Do not damage targets that are pawns owned by a first of diorite that is owned by our owner lol
            if (target instanceof EntityPawn pawn && pawn.getOwner() instanceof EntityGolemFirstDiorite firstDiorite)
            {
                if (firstDiorite.getOwner() == this.getOwner()) return;
            }
            // Do not damage villagers.
            if (target instanceof MerchantEntity) return;

            // Deal damage to the enemy.
            this.playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0f, 1.0f);
            target.damage(getDamageSources().mobAttack(this), getAttackDamage());
            applyDamageEffects(this, target);
            target.setVelocity(target.getVelocity().multiply(2));

            if (target instanceof LivingEntity livingTarget)
                livingTarget.addStatusEffect(new StatusEffectInstance(stunEffect));
            return;
        }
        super.pushAwayFrom(target);
    }

    @Override
    public boolean isPushable()
    {
        return getAttackState() == 0 || getAttackState() == 2;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand)
    {
        if (this.hasPassengers()) return super.interactMob(player, hand);
        if (this.getOwner() != player) return super.interactMob(player, hand);
        this.putPlayerOnBack(player);
        return ActionResult.success(this.getWorld().isClient);
    }

    protected void putPlayerOnBack(PlayerEntity player) {
        if (!this.getWorld().isClient) {
            player.setYaw(this.getYaw());
            player.setPitch(this.getPitch());
            player.startRiding(this);
            this.setDandoriState(DANDORI_STATES.OFF.ordinal());
        }
    }

    @Override
    public void travel(Vec3d movementInput) {
        movingBackwards = movementInput.z < 0;
        if (getAttackState() != 0) movementInput = Vec3d.ZERO;
        super.travel(new Vec3d(0,0, movementInput.z));
    }

    @Override
    protected void tickControlled(PlayerEntity controllingPlayer, Vec3d movementInput)
    {
        super.tickControlled(controllingPlayer, movementInput);
        if (this.isLogicalSideForUpdatingMovement())
        {
            double newRotation = this.getYaw() - controllingPlayer.sidewaysSpeed * 10;
            this.setRotation((float)newRotation, 0.0f);
            this.bodyYaw = this.headYaw = this.getYaw();
            this.prevYaw = this.headYaw;
        }
    }

    @Override
    protected float getSaddledSpeed(PlayerEntity controllingPlayer) {
        return (float)this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
    }

    @Override
    @Nullable
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        if (entity instanceof MobEntity)
        {
            MobEntity mobEntity = (MobEntity)entity;
            return mobEntity;
        }
        if ((entity = this.getFirstPassenger()) instanceof PlayerEntity)
        {
            PlayerEntity playerEntity = (PlayerEntity)entity;
            return playerEntity;
        }
        return null;
    }

    @Override
    protected Vec3d getControlledMovementInput(PlayerEntity controllingPlayer, Vec3d movementInput)
    {
        float f = controllingPlayer.sidewaysSpeed;
        float g = controllingPlayer.forwardSpeed * 0.5f;
        return new Vec3d(f, 0.0f, g);
    }

    @Override
    protected void updatePassengerPosition(Entity passenger, Entity.PositionUpdater positionUpdater)
    {
        super.updatePassengerPosition(passenger, positionUpdater);
        positionUpdater.accept(passenger, this.getX(), this.getY() + this.getMountedHeightOffset() + passenger.getHeightOffset(), this.getZ());
        if (passenger instanceof LivingEntity livingPassenger) livingPassenger.bodyYaw = this.bodyYaw;
    }

    @Override
    public double getMountedHeightOffset() {
        return getHeight() * 1.25;
    }

    public void forceAttack()
    {
        if (this.attackGoal == null) return;
        if (this.getAttackState() == 0)
            this.attackGoal.forceAttack();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, event ->
        {
            EntityGolemGrindstone pGolem = event.getAnimatable();
            if (pGolem.getAttackState() > 0)
            {
                if (pGolem.getAttackState() == 1)
                {
                    event.getController().setAnimationSpeed(1.00);
                    return event.setAndContinue(RawAnimation.begin().then("animation.golem_grindstone.attack_windup", Animation.LoopType.HOLD_ON_LAST_FRAME));
                }
                else if (pGolem.getAttackState() == 2)
                {
                    event.getController().setAnimationSpeed(2.00);
                    return event.setAndContinue(RawAnimation.begin().then("animation.golem_grindstone.attack", Animation.LoopType.LOOP));
                }
                event.getController().setAnimationSpeed(1.00);
                return event.setAndContinue(RawAnimation.begin().then("animation.golem_grindstone.attack_end", Animation.LoopType.HOLD_ON_LAST_FRAME));
            }
            else
            {
                event.getController().setAnimationSpeed(1.00);
                pGolem.setAttackState(0);
                if (pGolem.getVelocity().horizontalLengthSquared() > 0.001D || event.isMoving())
                {
                    if (pGolem.hasPassengers())
                    {
                        if (pGolem.movingBackwards)
                        {
                            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.golem_grindstone.carry_backwards"));
                        }
                        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.golem_grindstone.carry"));
                    }
                    return event.setAndContinue(RawAnimation.begin().thenLoop("animation.golem_grindstone.walk"));
                }
                else if (this.hasPassengers())
                    return event.setAndContinue(RawAnimation.begin().thenLoop("animation.golem_grindstone.carry_idle"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.golem_grindstone.idle"));
        }));
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }
}
