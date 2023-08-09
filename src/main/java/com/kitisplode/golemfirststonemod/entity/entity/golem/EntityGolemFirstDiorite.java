package com.kitisplode.golemfirststonemod.entity.entity.golem;

import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.entity.entity.IEntityWithDelayedMeleeAttack;
import com.kitisplode.golemfirststonemod.entity.entity.golem.pawn.EntityPawnFirstDiorite;
import com.kitisplode.golemfirststonemod.entity.goal.goal.MultiStageAttackGoal;
import com.kitisplode.golemfirststonemod.entity.goal.goal.MultiStageAttackGoalRanged;
import net.minecraft.block.BlockState;
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
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
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

import java.util.List;

public class EntityGolemFirstDiorite extends IronGolemEntity implements GeoEntity, IEntityWithDelayedMeleeAttack
{
	private static final TrackedData<Integer> ATTACK_STATE = DataTracker.registerData(EntityGolemFirstDiorite.class, TrackedDataHandlerRegistry.INTEGER);
	private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
	private final float attackRange = 32.0f;
	private final int pawnsToSpawn = 3;

	public EntityGolemFirstDiorite(EntityType<? extends IronGolemEntity> pEntityType, World pLevel)
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
			.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48);
	}

	@Override
	protected void initDataTracker()
	{
		super.initDataTracker();
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
		return getY() + 2.2f;
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new MultiStageAttackGoalRanged(this, 1.0, true, MathHelper.square(attackRange), new int[]{300, 240, 50}));
		this.goalSelector.add(2, new WanderNearTargetGoal(this, 0.8, 32.0F));
		this.goalSelector.add(2, new WanderAroundPointOfInterestGoal(this, 0.8, false));
		this.goalSelector.add(4, new IronGolemWanderAroundGoal(this, 0.8));
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
	public boolean isPushable()
	{
		return getAttackState() == 0;
	}

	@Override
	public boolean tryAttack()
	{
		if (getAttackState() != 3) return false;

		this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_ATTACK_SOUND);
		this.playSound(SoundEvents.BLOCK_BEACON_ACTIVATE, 1.0f, 1.0f);
		spawnPawns(pawnsToSpawn);
		return true;
	}

	private void spawnPawns(int pawnCount)
	{
		for (int i = 0; i < pawnCount; i++)
		{
			double direction = this.random.nextInt(360) * MathHelper.RADIANS_PER_DEGREE;
			double offset = this.random.nextInt(8) + 2;
			Vec3d spawnOffset = new Vec3d(Math.sin(direction) * offset,
					0.0f,
					Math.cos(direction) * offset);
			BlockState bs = getWorld().getBlockState(new BlockPos((int)(getX() + spawnOffset.getX()), (int)getY(),(int)(getZ() + spawnOffset.getZ())));
			while (!(bs.isAir() || bs.isOpaque()))
			{
				spawnOffset.add(0,1,0);
				bs = getWorld().getBlockState(new BlockPos((int)(getX() + spawnOffset.getX()), (int)getY(),(int)(getZ() + spawnOffset.getZ())));
			}

			EntityPawnFirstDiorite pawn = ModEntities.ENTITY_PAWN_FIRST_DIORITE.create(getWorld());
			if (pawn == null) continue;
			pawn.setPlayerCreated(isPlayerCreated());
			pawn.refreshPositionAndAngles(getX() + spawnOffset.getX(), getY() + spawnOffset.getY(), getZ() + spawnOffset.getZ(), 0.0f, 0.0F);
			getWorld().spawnEntity(pawn);

			AreaEffectCloudEntity dust = new AreaEffectCloudEntity(getWorld(), getX() + spawnOffset.getX(), getY() + spawnOffset.getY(), getZ() + spawnOffset.getZ());
			dust.setParticleType(ParticleTypes.POOF);
			dust.setRadius(1.0f);
			dust.setDuration(1);
			dust.setPos(dust.getX(),dust.getY(),dust.getZ());
			getWorld().spawnEntity(dust);
		}
	}

	@Override
	protected ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (!itemStack.isOf(Items.GOLD_INGOT)) {
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
	public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
	{
		controllerRegistrar.add(new AnimationController<>(this, "controller", 0, event ->
		{
			EntityGolemFirstDiorite pGolem = event.getAnimatable();
			if (pGolem.getAttackState() > 0)
			{
				switch (pGolem.getAttackState())
				{
					case 1:
						event.getController().setAnimationSpeed(0.5);
						return event.setAndContinue(RawAnimation.begin().then("animation.first_diorite.attack_windup", Animation.LoopType.HOLD_ON_LAST_FRAME));
					case 2:
						event.getController().setAnimationSpeed(1.00);
						return event.setAndContinue(RawAnimation.begin().then("animation.first_diorite.attack_charge", Animation.LoopType.LOOP));
					default:
						event.getController().setAnimationSpeed(1.00);
						return event.setAndContinue(RawAnimation.begin().then("animation.first_diorite.attack_end", Animation.LoopType.HOLD_ON_LAST_FRAME));
				}
			}
			else
			{
				event.getController().setAnimationSpeed(1.00);
				pGolem.setAttackState(0);
				if (getVelocity().horizontalLengthSquared() > 0.001D || event.isMoving())
					return event.setAndContinue(RawAnimation.begin().thenLoop("animation.first_diorite.walk"));
			}
			return event.setAndContinue(RawAnimation.begin().thenLoop("animation.first_diorite.idle"));
		}));
	}

	@Override public AnimatableInstanceCache getAnimatableInstanceCache()
	{
		return cache;
	}
}
