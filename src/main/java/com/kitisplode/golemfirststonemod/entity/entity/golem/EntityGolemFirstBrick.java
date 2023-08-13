package com.kitisplode.golemfirststonemod.entity.entity.golem;

import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.entity.entity.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.IEntityWithDelayedMeleeAttack;
import com.kitisplode.golemfirststonemod.entity.entity.effect.EntityEffectShieldFirstBrick;
import com.kitisplode.golemfirststonemod.entity.goal.goal.DandoriFollowGoal;
import com.kitisplode.golemfirststonemod.entity.goal.goal.MultiStageAttackGoalRanged;
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
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class EntityGolemFirstBrick extends IronGolemEntity implements GeoEntity, IEntityWithDelayedMeleeAttack, IEntityDandoriFollower
{
	private static final TrackedData<Integer> ATTACK_STATE = DataTracker.registerData(EntityGolemFirstBrick.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> DANDORI_STATE = DataTracker.registerData(EntityGolemFirstBrick.class, TrackedDataHandlerRegistry.BOOLEAN);
	protected static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(EntityGolemFirstBrick.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
	private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
	private static final int shieldHurtTime = 30;
	private static final int shieldAbsorptionTime = 20 * 5;
	private static final int shieldAbsorptionAmount = 0;
	private static final float attackAOERange = 10.0f;
	private static final float attackVerticalRange = 5.0f;
	private final ArrayList<StatusEffectInstance> shieldStatusEffects = new ArrayList<>();
	private static final double dandoriMoveRange = 6;
	private static final double dandoriSeeRange = 36;

	public EntityGolemFirstBrick(EntityType<? extends IronGolemEntity> pEntityType, World pLevel)
	{
		super(pEntityType, pLevel);
		shieldStatusEffects.add(new StatusEffectInstance(StatusEffects.ABSORPTION, shieldAbsorptionTime, shieldAbsorptionAmount, false, true));
	}

	public static DefaultAttributeContainer.Builder setAttributes()
	{
		return GolemEntity.createMobAttributes()
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 1000.0f)
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
		if (!this.dataTracker.containsKey(DANDORI_STATE))
			this.dataTracker.startTracking(DANDORI_STATE, false);
		if (!this.dataTracker.containsKey(OWNER_UUID))
			this.dataTracker.startTracking(OWNER_UUID, Optional.empty());
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt)
	{
		super.writeCustomDataToNbt(nbt);
		if (this.getOwnerUuid() != null) {
			nbt.putUuid("Owner", this.getOwnerUuid());
		}
	}
	@Override
	public void readCustomDataFromNbt(NbtCompound nbt)
	{
		super.readCustomDataFromNbt(nbt);
		UUID uUID;
		if (nbt.containsUuid("Owner")) {
			uUID = nbt.getUuid("Owner");
		} else {
			String string = nbt.getString("Owner");
			uUID = ServerConfigHandler.getPlayerUuidByName(this.getServer(), string);
		}
		if (uUID != null) {
			try {
				this.setOwnerUuid(uUID);
			} catch (Throwable throwable) {
			}
		}
	}
	@Override
	public LivingEntity getOwner()
	{
		UUID uUID = this.getOwnerUuid();
		if (uUID == null)
			return null;
		return this.getWorld().getPlayerByUuid(uUID);
	}
	@Override
	public void setOwner(LivingEntity newOwner)
	{
		if (newOwner != null)
		{
			setOwnerUuid(newOwner.getUuid());
		}
	}
	@Override
	public boolean isOwner(LivingEntity entity)
	{
		return entity.getUuid() == this.getOwnerUuid();
	}
	@Nullable
	private UUID getOwnerUuid() {
		return this.dataTracker.get(OWNER_UUID).orElse(null);
	}
	private void setOwnerUuid(@Nullable UUID uuid) {
		this.dataTracker.set(OWNER_UUID, Optional.ofNullable(uuid));
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

	@Override
	public double getEyeY()
	{
		return getY() + 2.2f;
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new DandoriFollowGoal(this, 1.0, Ingredient.ofItems(ModItems.ITEM_DANDORI_CALL), dandoriMoveRange, dandoriSeeRange));
		this.goalSelector.add(2, new MultiStageAttackGoalRanged(this, 1.0, true, MathHelper.square(attackAOERange), new int[]{70, 30, 25}, 0));
		this.goalSelector.add(3, new WanderNearTargetGoal(this, 0.8, 32.0F));
		this.goalSelector.add(3, new WanderAroundPointOfInterestGoal(this, 0.8, false));
		this.goalSelector.add(4, new IronGolemWanderAroundGoal(this, 0.8));
		this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(8, new LookAroundGoal(this));
		this.targetSelector
				.add(1, new PassiveTargetGoal<PlayerEntity>(this, PlayerEntity.class, 5, false, false, golemTarget()));
		this.targetSelector
			.add(2, new PassiveTargetGoal<MobEntity>(this, MobEntity.class, 5, false, false, golemTarget()));
	}

	private Predicate<LivingEntity> golemTarget()
	{
		return entity ->
		{
			// Skip itself.
			if (entity == this) return false;
			// Check other golems, villagers, and players
			if (entity instanceof GolemEntity
					|| entity instanceof MerchantEntity
					|| (entity instanceof PlayerEntity && isPlayerCreated()))
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
		if (targetAttacker instanceof PlayerEntity && this.isPlayerCreated())
		{
			return false;
		}
		// Skip other potential targets that are being attacked by golems (only happens accidentally or by other cleric golems)
		if (targetAttacker instanceof GolemEntity) return false;
		// Otherwise, this is a good target.
		return true;
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

		EntityEffectShieldFirstBrick shield = ModEntities.ENTITY_SHIELD_FIRST_BRICK.create(getWorld());
		if (shield != null)
		{
			shield.setPosition(getPos());
			shield.setLifeTime(20);
			shield.setFullScale(range * 2.0f);
			getWorld().spawnEntity(shield);
		}
	}

	private void attackAOE()
	{
		List<LivingEntity> targetList = getWorld().getNonSpectatingEntities(LivingEntity.class, getBoundingBox().expand(attackAOERange));
		for (LivingEntity target : targetList)
		{
			// Do not shield ourselves.
			if (target == this) continue;
			// Do not shield targets that are NOT villagers, golems, or players.
			if (!(target instanceof MerchantEntity
					|| target instanceof GolemEntity
					|| (target instanceof PlayerEntity && isPlayerCreated()))
					|| (target.getFirstPassenger() != null && target.getFirstPassenger() instanceof PlayerEntity))
				continue;
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
	protected ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (!itemStack.isOf(Items.BRICKS)) {
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
			EntityGolemFirstBrick pGolem = event.getAnimatable();
			if (pGolem.getAttackState() > 0)
			{
				switch (pGolem.getAttackState())
				{
					case 1:
						event.getController().setAnimationSpeed(0.5);
						return event.setAndContinue(RawAnimation.begin().then("animation.first_brick.attack_windup", Animation.LoopType.HOLD_ON_LAST_FRAME));
					case 2:
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
