package com.kitisplode.fabricplayground.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

public class EntityGolemFirstStone extends IronGolemEntity implements GeoEntity
{
	private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

	public int attackState;
	private int attackTimer;
	private final int attackTime1Windup = 40;
	private final int attackTime1Attack = 20;
	private final int attackTime = attackTime1Windup + attackTime1Attack;

	public EntityGolemFirstStone(EntityType<? extends IronGolemEntity> pEntityType, World pLevel)
	{
		super(pEntityType, pLevel);
		attackState = 0;
		attackTimer = 0;
	}

	public static DefaultAttributeContainer.Builder setAttributes()
	{
		return GolemEntity.createMobAttributes()
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 100.0D)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25f)
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 15.0f);
	}

//	@Override
//	protected void initGoals()
//	{
//		this.goalSelector.add(1, new LookAroundGoal(this));
//		this.goalSelector.add(2, new WanderAroundFarGoal(this, 0.4D));
//		this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
//
//		this.targetSelector
//			.add(3, new TargetGoal(this, MobEntity.class, 5, false, false, entity -> entity instanceof Monster && !(entity instanceof CreeperEntity)));
//	}
	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0, true));
		this.goalSelector.add(2, new WanderNearTargetGoal(this, 0.3, 32.0F));
		this.goalSelector.add(2, new WanderAroundPointOfInterestGoal(this, 0.2, false));
		this.goalSelector.add(4, new IronGolemWanderAroundGoal(this, 0.2));
		this.goalSelector.add(5, new IronGolemLookGoal(this));
		this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(8, new LookAroundGoal(this));
		this.targetSelector.add(1, new TrackIronGolemTargetGoal(this));
		this.targetSelector.add(2, new RevengeGoal(this));
		this.targetSelector
			.add(3, new ActiveTargetGoal(this, MobEntity.class, 5, false, false, entity -> entity instanceof Monster && !(entity instanceof CreeperEntity)));
		this.targetSelector.add(4, new UniversalAngerGoal<>(this, false));
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (this.attackTimer > 0) {
			--this.attackTimer;
		}
		if (isAttacking() || this.attackTimer > 0)
		{
			setForwardSpeed(0.0f);
			setSidewaysSpeed(0.0f);
		}
	}

	@Override
	public boolean tryAttack(Entity target)
	{
		this.attackTimer = this.attackTime;
		return super.tryAttack(target);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
	{
		controllerRegistrar.add(new AnimationController<>(this, "controller", 0, event ->
		{
			EntityGolemFirstStone pGolem = event.getAnimatable();
			if (pGolem.isAttacking())
			{
				if (pGolem.attackTimer == 0)
					pGolem.attackTimer = pGolem.attackTime;
			}
			if (pGolem.attackTimer > 0)
			{
				if (pGolem.attackTimer <= pGolem.attackTime - pGolem.attackTime1Windup)
				{
					pGolem.attackState = 1;
				}
				if (pGolem.attackState == 0)
				{
					event.getController().setAnimationSpeed(0.5);
					return event.setAndContinue(RawAnimation.begin().then("animation.first_stone.attack_windup", Animation.LoopType.HOLD_ON_LAST_FRAME));
				}
				else
				{
					event.getController().setAnimationSpeed(1.00);
					return event.setAndContinue(RawAnimation.begin().then("animation.first_stone.attack", Animation.LoopType.HOLD_ON_LAST_FRAME));
				}
			}
			else
			{
				event.getController().setAnimationSpeed(1.00);
				pGolem.attackState = 0;
				if (getVelocity().horizontalLengthSquared() > 0.001D || event.isMoving())
					return event.setAndContinue(RawAnimation.begin().thenLoop("animation.first_stone.walk"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.first_stone.idle"));
		}));
	}

	@Override public AnimatableInstanceCache getAnimatableInstanceCache()
	{
		return cache;
	}
}
