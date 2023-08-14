package com.kitisplode.golemfirststonemod.entity.entity.golem;

import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.entity.entity.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.IEntityWithDelayedMeleeAttack;
import com.kitisplode.golemfirststonemod.entity.entity.golem.pawn.EntityPawnFirstDiorite;
import com.kitisplode.golemfirststonemod.entity.goal.goal.DandoriFollowGoal;
import com.kitisplode.golemfirststonemod.entity.goal.goal.MultiStageAttackGoalRanged;
import com.kitisplode.golemfirststonemod.item.ModItems;
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
import net.minecraft.util.math.BlockPos;
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

import java.util.Optional;
import java.util.UUID;

public class EntityGolemFirstDiorite extends IronGolemEntity implements GeoEntity, IEntityWithDelayedMeleeAttack, IEntityDandoriFollower
{
	private static final TrackedData<Integer> ATTACK_STATE = DataTracker.registerData(EntityGolemFirstDiorite.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> DANDORI_STATE = DataTracker.registerData(EntityGolemFirstDiorite.class, TrackedDataHandlerRegistry.BOOLEAN);
	protected static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(EntityGolemFirstDiorite.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
	private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
	private static final float attackRange = 20.0f;
	private static final int pawnsToSpawn = 3;
	private static final double dandoriMoveRange = 6;
	private static final double dandoriSeeRange = 36;

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
			.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 24);
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
		this.goalSelector.add(2, new MultiStageAttackGoalRanged(this, 1.0, true, MathHelper.square(attackRange), new int[]{300, 240, 50}));
		this.goalSelector.add(3, new WanderNearTargetGoal(this, 0.8, 32.0F));
		this.goalSelector.add(5, new IronGolemWanderAroundGoal(this, 0.8));
		this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(8, new LookAroundGoal(this));
		this.targetSelector
			.add(3, new ActiveTargetGoal(this, MobEntity.class, 5, false, false, entity -> entity instanceof Monster && !(entity instanceof CreeperEntity)));
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
		this.playSound(SoundEvents.BLOCK_BEACON_POWER_SELECT, 1.0f, 1.0f);
		spawnPawns(pawnsToSpawn);
		return true;
	}

	private void spawnPawns(int pawnCount)
	{
		for (int i = 0; i < pawnCount; i++)
		{
			double direction = this.random.nextInt(360) * MathHelper.RADIANS_PER_DEGREE;
			double offset = this.random.nextInt(4) + 2;
			Vec3d spawnOffset = new Vec3d(Math.sin(direction) * offset,
					0.0f,
					Math.cos(direction) * offset);
			BlockState bs = getWorld().getBlockState(new BlockPos((int)(getX() + spawnOffset.getX()), (int)(getY() + spawnOffset.getY()),(int)(getZ() + spawnOffset.getZ())));
			int failCount = 0;
			while (!bs.isAir() || bs.isOpaque())
			{
				spawnOffset = spawnOffset.add(0,1,0);
				bs = getWorld().getBlockState(new BlockPos((int)(getX() + spawnOffset.getX()), (int)(getY() + spawnOffset.getY()),(int)(getZ() + spawnOffset.getZ())));
				failCount++;
				if (failCount > 5) break;
			}

			EntityPawnFirstDiorite pawn = ModEntities.ENTITY_PAWN_FIRST_DIORITE.create(getWorld());
			if (pawn == null) continue;
			pawn.setOwner(this);
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
