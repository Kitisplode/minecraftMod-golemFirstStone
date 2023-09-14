package com.kitisplode.golemfirststonemod.entity.entity.golem.first;

import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.entity.entity.golem.AbstractGolemDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityPawn;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDelayedMeleeAttack;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriFollowHardGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriFollowSoftGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriMoveToDeployPositionGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.MultiStageAttackGoalRanged;
import com.kitisplode.golemfirststonemod.entity.goal.target.PassiveTargetGoal;
import com.kitisplode.golemfirststonemod.item.ModItems;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class EntityGolemFirstBrick extends AbstractGolemDandoriFollower implements GeoEntity, IEntityWithDelayedMeleeAttack, IEntityDandoriFollower
{
	private static final TrackedData<Integer> ATTACK_STATE = DataTracker.registerData(EntityGolemFirstBrick.class, TrackedDataHandlerRegistry.INTEGER);
	private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
	private static final int shieldHurtTime = 30;
	private static final int shieldEffectAmount = 1;
	private static final float attackAOERange = 4.5f;
	private static final float attackVerticalRange = 5.0f;
	private static final int shieldTime = 120;
	private final ArrayList<StatusEffectInstance> shieldStatusEffects = new ArrayList<>();
	private MultiStageAttackGoalRanged attackGoal;

	public EntityGolemFirstBrick(EntityType<? extends IronGolemEntity> pEntityType, World pLevel)
	{
		super(pEntityType, pLevel);
		shieldStatusEffects.add(new StatusEffectInstance(StatusEffects.RESISTANCE, shieldTime, shieldEffectAmount, false, true));
	}

	public static DefaultAttributeContainer.Builder setAttributes()
	{
		return GolemEntity.createMobAttributes()
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 500.0f)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25f)
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 30.0f)
			.add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0f)
			.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32);
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

	@Override
	public double getEyeY()
	{
		return getY() + 2.2f;
	}

	@Override
	protected void initGoals() {
		this.attackGoal = new MultiStageAttackGoalRanged(this, 1.0, true, MathHelper.square(attackAOERange), new int[]{120, 85,80, 25}, 0);
		this.attackGoal.setCooldownMax(200);

		this.goalSelector.add(0, new DandoriFollowHardGoal(this, 1.4, dandoriMoveRange, dandoriSeeRange));
		this.goalSelector.add(1, new DandoriFollowSoftGoal(this, 1.4, dandoriMoveRange, dandoriSeeRange));

		this.goalSelector.add(2, this.attackGoal);
		this.goalSelector.add(3, new DandoriMoveToDeployPositionGoal(this, 2.0f, 1.0f));

		this.goalSelector.add(4, new DandoriFollowSoftGoal(this, 1.4, dandoriMoveRange, 0));

		this.goalSelector.add(5, new WanderNearTargetGoal(this, 0.8, 32.0F));
		this.goalSelector.add(6, new IronGolemWanderAroundGoal(this, 0.8));
		this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(7, new LookAtEntityGoal(this, MerchantEntity.class, 8.0F));
		this.goalSelector.add(8, new LookAroundGoal(this));
		this.targetSelector
				.add(1, new PassiveTargetGoal<PlayerEntity>(this, PlayerEntity.class, 5, true, false, golemTarget()));
		this.targetSelector
			.add(2, new PassiveTargetGoal<MobEntity>(this, MobEntity.class, 5, true, false, golemTarget()));
	}

	private Predicate<LivingEntity> golemTarget()
	{
		return entity ->
		{
			// Skip itself.
			if (entity == this) return false;
			// Check other golems, villagers, and players
			if ((entity instanceof IEntityDandoriFollower dandoriFollower
					&& (dandoriFollower.getOwner() == this.getOwner()
					|| (dandoriFollower.getOwner() instanceof IEntityDandoriFollower dandoriFollowerOwner && dandoriFollowerOwner.getOwner() == this.getOwner())))
					|| (entity instanceof EntityPawn pawn
					&& pawn.getOwner() instanceof EntityGolemFirstDiorite firstDiorite
					&& firstDiorite.getOwner() == this.getOwner())
					|| (entity instanceof PlayerEntity && entity == this.getOwner())
					|| entity instanceof MerchantEntity)
			{
				// For entities currently being attacked:
				LivingEntity targetCurrentAttacker = entity.getAttacker();
				if (targetCurrentAttacker != null && targetCurrentAttacker.isAlive())
				{
					return golemTarget_checkTargetAttacker(targetCurrentAttacker);
				}

				// For entities not currently being attacked but attacked recently.
				LivingEntity targetLastAttacker = entity.getLastAttacker();
				if (targetLastAttacker != null)
				{
					if (MathHelper.abs(entity.getLastAttackedTime() - entity.age) < shieldHurtTime)
					{
						return golemTarget_checkTargetAttacker(targetLastAttacker);
					}
				}
			}
			return false;
		};
	}

	private boolean golemTarget_checkTargetAttacker(LivingEntity targetAttacker)
	{
		// If the golem was player made, skip potential targets that were attacked by the player.
		if (targetAttacker instanceof PlayerEntity && this.getOwner() == targetAttacker)
		{
			return false;
		}
		// Skip other potential targets that are being attacked by golems (only happens accidentally or by other cleric golems)
		if (targetAttacker instanceof IEntityDandoriFollower dandoriFollower)
		{
			if (dandoriFollower.getOwner() == this.getOwner()) return false;
		}
		// Otherwise, this is a good target.
		return true;
	}

	@Override
	public boolean canTarget(LivingEntity entity)
	{
		return !(entity instanceof Monster);
	}

	@Override
	public boolean tryAttack()
	{
		if (getAttackState() != 3) return false;

		this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_ATTACK_SOUND);
		this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0f, 1.0f);
		this.playSound(SoundEvents.BLOCK_BEACON_POWER_SELECT, 1.0f, 1.0f);
		attackDust();
		attackAOE();

		// Check to see if the target is still viable...
		LivingEntity target = this.getTarget();
		if (target != null && target.isAlive())
		{
			boolean targetGood = golemTarget().test(target);
			if (!targetGood) this.setTarget(null);
		}
		else
		{
			this.setTarget(null);
		}

		return true;
	}

	private void attackDust()
	{
		float range = attackAOERange + 1;
		AreaEffectCloudEntity dust = new AreaEffectCloudEntity(getWorld(), getX(),getY(),getZ());
		dust.setParticleType(ParticleTypes.HAPPY_VILLAGER);
		dust.setRadius(range);
		dust.setDuration(1);
		dust.setPos(getX(),getY(),getZ());
		getWorld().spawnEntity(dust);
	}

	private void attackAOE()
	{
		EntityShieldFirstBrick shield = ModEntities.ENTITY_SHIELD_FIRST_BRICK.create(getWorld());
		if (shield != null)
		{
			shield.setPosition(getPos());
			shield.setLifeTime(shieldTime);
			shield.setFullScale((attackAOERange + 1) * 2.0f);
			shield.setOwner(this);
			getWorld().spawnEntity(shield);
		}

		List<LivingEntity> targetList = getWorld().getNonSpectatingEntities(LivingEntity.class, getBoundingBox().expand(attackAOERange));
		for (LivingEntity target : targetList)
		{
			// Do not shield ourselves.
			if (target == this) continue;
			// Do not shield targets that are monsters.
			if (target instanceof Monster) continue;
			// Do not shield targets that are players if we are not player created.
			if (target instanceof PlayerEntity && target != this.getOwner()) continue;
			// Do not shield dandori followers that are not owned by our owner.
			if (target instanceof IEntityDandoriFollower dandoriFollower)
			{
				if (dandoriFollower.getOwner() != this.getOwner()) continue;
				if (dandoriFollower.getOwner() instanceof IEntityDandoriFollower dandoriFollowerOwner && dandoriFollowerOwner.getOwner() != this.getOwner()) continue;
				if (dandoriFollower instanceof EntityPawn pawn)
				{
					if (pawn.getOwnerType() == EntityPawn.OWNER_TYPES.FIRST_OF_DIORITE.ordinal())
					{
						if (pawn.getOwner() != this.getOwner()) continue;
					}
				}
			}
			// Do not shield targets that are too far on the y axis.
			if (Math.abs(getY() - target.getY()) > attackVerticalRange) continue;

			for (StatusEffectInstance statusEffectInstance : shieldStatusEffects)
			{
				StatusEffect statusEffect = statusEffectInstance.getEffectType();
				int i2 = statusEffectInstance.mapDuration(i -> (int)(1 * (double)i + 0.5));
				StatusEffectInstance statusEffectInstance2 = new StatusEffectInstance(statusEffect, i2, statusEffectInstance.getAmplifier(), statusEffectInstance.isAmbient(), statusEffectInstance.shouldShowParticles());
				if (statusEffectInstance2.isDurationBelow(20)) continue;
				target.addStatusEffect(statusEffectInstance2, this);
			}
		}
	}

	@Override
	public boolean isPushable()
	{
		return getAttackState() == 0;
	}

	@Override
	public void tick()
	{
		super.tick();
		if (this.getAttackState() == 0 && this.attackGoal != null && this.attackGoal.isCooledDown() && this.isDandoriOff())
		{
			List<LivingEntity> list = this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(32), entity->entity instanceof Monster && this.canSee(entity));
			if (!list.isEmpty())
			{
				this.attackGoal.forceAttack();
			}
		}
	}

	@Override
	protected ActionResult interactMob(PlayerEntity player, Hand hand) {
		return ActionResult.PASS;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
	{
		controllerRegistrar.add(new AnimationController<>(this, "controller", 0, event ->
		{
			EntityGolemFirstBrick pGolem = event.getAnimatable();
			if (pGolem.getAttackState() > 0)
			{
				switch (pGolem.getAttackState())
				{
					case 1:
						event.getController().setAnimationSpeed(0.5);
						return event.setAndContinue(RawAnimation.begin().then("animation.first_brick.attack_windup", Animation.LoopType.HOLD_ON_LAST_FRAME));
					case 2, 3:
						event.getController().setAnimationSpeed(1.00);
						return event.setAndContinue(RawAnimation.begin().then("animation.first_brick.attack", Animation.LoopType.HOLD_ON_LAST_FRAME));
					default:
						event.getController().setAnimationSpeed(1.00);
						return event.setAndContinue(RawAnimation.begin().then("animation.first_brick.attack_end", Animation.LoopType.HOLD_ON_LAST_FRAME));
				}
			}
			else
			{
				event.getController().setAnimationSpeed(1.00);
				pGolem.setAttackState(0);
				if (getVelocity().horizontalLengthSquared() > 0.001D || event.isMoving())
					return event.setAndContinue(RawAnimation.begin().thenLoop("animation.first_brick.walk"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.first_brick.idle"));
		}));
	}

	@Override public AnimatableInstanceCache getAnimatableInstanceCache()
	{
		return cache;
	}
}
