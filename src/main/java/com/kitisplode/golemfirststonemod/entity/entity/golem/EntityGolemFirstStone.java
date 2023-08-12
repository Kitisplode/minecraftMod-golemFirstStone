package com.kitisplode.golemfirststonemod.entity.entity.golem;

import com.kitisplode.golemfirststonemod.entity.entity.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.IEntityWithDelayedMeleeAttack;
import com.kitisplode.golemfirststonemod.entity.goal.goal.DandoriFollowGoal;
import com.kitisplode.golemfirststonemod.entity.goal.goal.MultiStageAttackGoal;
import com.kitisplode.golemfirststonemod.item.ModItems;
import net.minecraft.entity.*;
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
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.List;

public class EntityGolemFirstStone extends IronGolemEntity implements GeoEntity, IEntityWithDelayedMeleeAttack, IEntityDandoriFollower
{
	private static final TrackedData<Integer> ATTACK_STATE = DataTracker.registerData(EntityGolemFirstStone.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> DANDORI_STATE = DataTracker.registerData(EntityGolemFirstStone.class, TrackedDataHandlerRegistry.BOOLEAN);
	private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
	private final float attackAOERange = 4.0f;
	private final float attackKnockbackAmount = 2.0f;
	private final float attackKnockbackAmountVertical = 0.25f;
	private final float attackVerticalRange = 2.0f;
	private final double dandoriMoveRange = 6;
	private final double dandoriSeeRange = 20;

	public EntityGolemFirstStone(EntityType<? extends IronGolemEntity> pEntityType, World pLevel)
	{
		super(pEntityType, pLevel);
	}

	public static DefaultAttributeContainer.Builder setAttributes()
	{
		return GolemEntity.createMobAttributes()
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 1000.0f)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25f)
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 30.0f)
			.add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0f)
			.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 16);
	}

	@Override
	protected void initDataTracker()
	{
		super.initDataTracker();
		if (!this.dataTracker.containsKey(ATTACK_STATE))
			this.dataTracker.startTracking(ATTACK_STATE, 0);
		if (!this.dataTracker.containsKey(DANDORI_STATE))
			this.dataTracker.startTracking(DANDORI_STATE, false);
	}

	public boolean getDandoriState()
	{
		return this.dataTracker.get(DANDORI_STATE);
	}

	public void setDandoriState(boolean pDandoriState)
	{
		this.dataTracker.set(DANDORI_STATE, pDandoriState);
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
		return getY() + 2.2f;
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new DandoriFollowGoal(this, 1.0, Ingredient.ofItems(ModItems.ITEM_DANDORI_CALL), dandoriMoveRange, dandoriSeeRange));
		this.goalSelector.add(2, new MultiStageAttackGoal(this, 1.0, true, 6.0D, new int[]{70, 30, 25}));
		this.goalSelector.add(3, new WanderNearTargetGoal(this, 0.8, 32.0F));
		this.goalSelector.add(3, new WanderAroundPointOfInterestGoal(this, 0.8, false));
		this.goalSelector.add(5, new IronGolemWanderAroundGoal(this, 0.8));
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
	}

	@Override
	public boolean tryAttack()
	{
		if (getAttackState() != 3) return false;

		this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_ATTACK_SOUND);
		this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
		attackDust();
		attackAOE();
		return true;
	}

	private void attackDust()
	{
		AreaEffectCloudEntity dust = new AreaEffectCloudEntity(getWorld(), getX(),getY(),getZ());
		dust.setParticleType(ParticleTypes.SMOKE);
		dust.setRadius(5.0f);
		dust.setDuration(1);
		dust.setPos(getX(),getY(),getZ());
		getWorld().spawnEntity(dust);
	}

	private void attackAOE()
	{
		List<LivingEntity> targetList = getWorld().getNonSpectatingEntities(LivingEntity.class, getBoundingBox().expand(attackAOERange));
		for (LivingEntity target : targetList)
		{
			// Do not damage ourselves.
			if (target == this) continue;
			// Do not damage targets that are villagers or golems.
			if (target instanceof VillagerEntity) continue;
			if (target instanceof MerchantEntity) continue;
			if (target instanceof GolemEntity) continue;
			// Do not damage players if the golem is player made.
			if (target instanceof PlayerEntity && isPlayerCreated()) continue;
			if (target.getFirstPassenger() != null && target.getFirstPassenger() instanceof PlayerEntity) continue;
			// Do not damage targets that are too far on the y axis.
			if (Math.abs(getY() - target.getY()) > attackVerticalRange) continue;

			// Apply damage.
			float forceMultiplier = Math.abs((attackAOERange - this.distanceTo(target)) / attackAOERange);
			float totalDamage = getAttackDamage() * forceMultiplier;
			target.damage(getDamageSources().mobAttack(this), totalDamage);
			// Apply knockback.
			double knockbackResistance = Math.max(0.0, 1.0 - target.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
			double knockbackForce = knockbackResistance * attackKnockbackAmount;
			Vec3d knockbackDirection = target.getPos().subtract(getPos()).normalize().add(0,attackKnockbackAmountVertical,0);
			target.setVelocity(target.getVelocity().add(knockbackDirection.multiply(knockbackForce)));
			applyDamageEffects(this, target);
		}
	}

	@Override
	protected ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (!itemStack.isOf(Items.STONE)) {
			return ActionResult.PASS;
		}
		float f = this.getHealth();
		this.heal(25.0f);
		if (this.getHealth() == f) {
			return ActionResult.PASS;
		}
		float g = 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.2f;
		this.playSound(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 1.0f, g);
		if (!player.getAbilities().creativeMode) {
			itemStack.decrement(1);
		}
		return ActionResult.success(this.getWorld().isClient);
	}

	@Override
	public void handleStatus(byte status)
	{
		switch(status)
		{
			case EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES:
				addDandoriParticles();
				break;
			default:
				super.handleStatus(status);
				break;
		}
	}

	private void addDandoriParticles()
	{
		this.getWorld().addParticle(ParticleTypes.NOTE,
		this.getX(), this.getEyeY() + 3, this.getZ(),
		0,1,0);
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
	{
		controllerRegistrar.add(new AnimationController<>(this, "controller", 0, event ->
		{
			EntityGolemFirstStone pGolem = event.getAnimatable();
			if (pGolem.getAttackState() > 0)
			{
				switch (pGolem.getAttackState())
				{
					case 1:
						event.getController().setAnimationSpeed(0.5);
						return event.setAndContinue(RawAnimation.begin().then("animation.first_stone.attack_windup", Animation.LoopType.HOLD_ON_LAST_FRAME));
					case 2:
						event.getController().setAnimationSpeed(1.00);
						return event.setAndContinue(RawAnimation.begin().then("animation.first_stone.attack", Animation.LoopType.HOLD_ON_LAST_FRAME));
					default:
						event.getController().setAnimationSpeed(1.00);
						return event.setAndContinue(RawAnimation.begin().then("animation.first_stone.attack_end", Animation.LoopType.HOLD_ON_LAST_FRAME));
				}
			}
			else
			{
				event.getController().setAnimationSpeed(1.00);
				pGolem.setAttackState(0);
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
