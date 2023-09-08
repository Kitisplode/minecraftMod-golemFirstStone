package com.kitisplode.golemfirststonemod.entity.entity.golem.first;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.entity.entity.effect.EntityEffectCubeDandoriWhistle;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemCobble;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityPawn;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntitySummoner;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriFollowHardGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriMoveToDeployPositionGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.SummonEntityGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.WanderAroundTargetGoal;
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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
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

public class EntityGolemFirstDiorite extends EntityGolemCobble implements GeoEntity, IEntitySummoner, IEntityDandoriFollower
{
	private static final TrackedData<Integer> SUMMON_STATE = DataTracker.registerData(EntityGolemFirstDiorite.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> SUMMON_COOLDOWN = DataTracker.registerData(EntityGolemFirstDiorite.class, TrackedDataHandlerRegistry.BOOLEAN);
	private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

	private static final double pawnSearchRange = 24;
	private static final int pawnsMax = 15;
	private static final int pawnsToSpawn = 1;
	private static final int spawnCooldown = 100;
	private static final int[] spawnStages = new int[]{140,100,50};
	private static final int spawnStage = 3;

	private static final double dandoriMoveRange = 6;
	private static final double dandoriSeeRange = 36;

	private SummonEntityGoal summonGoal;

	public EntityGolemFirstDiorite(EntityType<? extends IronGolemEntity> pEntityType, World pLevel)
	{
		super(pEntityType, pLevel);
	}

	public static DefaultAttributeContainer.Builder setAttributes()
	{
		return GolemEntity.createMobAttributes()
			.add(EntityAttributes.GENERIC_MAX_HEALTH, 500.0f)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25f)
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 30.0f)
			.add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0f)
			.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 24);
	}

	@Override
	protected void initDataTracker()
	{
		super.initDataTracker();
		if (!this.dataTracker.containsKey(SUMMON_STATE)) this.dataTracker.startTracking(SUMMON_STATE, 0);
		if (!this.dataTracker.containsKey(SUMMON_COOLDOWN)) this.dataTracker.startTracking(SUMMON_COOLDOWN, false);
	}
	public int getSummonState()
	{
		return this.dataTracker.get(SUMMON_STATE);
	}
	public void setSummonState(int pInt)
	{
		this.dataTracker.set(SUMMON_STATE, pInt);
	}
	public boolean getSummonCooleddown()
	{
		return this.dataTracker.get(SUMMON_COOLDOWN);
	}
	public void setSummonCooledDown(boolean pBoolean)
	{
		this.dataTracker.set(SUMMON_COOLDOWN, pBoolean);
	}
	@Override
	public int getMaxHeadRotation()
	{
		if (this.getSummonState() > 0) return 0;
		return super.getMaxHeadRotation();
	}
	@Override
	public int getMaxLookYawChange()
	{
		if (this.getSummonState() > 0) return 0;
		return super.getMaxLookYawChange();
	}
	@Override
	public float getMovementSpeed()
	{
		if (this.getSummonState() > 0) return 0.0f;
		return (float)this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
	}
	@Override
	public double getEyeY()
	{
		return getY() + 2.2f;
	}

	@Override
	protected void initGoals() {
		this.summonGoal	= new SummonEntityGoal<>(this, EntityPawn.class, spawnStages, pawnSearchRange, pawnsMax, spawnCooldown, 1);
		this.goalSelector.add(0, new DandoriFollowHardGoal(this, 1.4, Ingredient.ofItems(ModItems.ITEM_DANDORI_CALL, ModItems.ITEM_DANDORI_ATTACK), dandoriMoveRange, dandoriSeeRange));

		this.goalSelector.add(1, this.summonGoal);
		this.goalSelector.add(2, new DandoriMoveToDeployPositionGoal(this, 2.0f, 1.0f));

		this.goalSelector.add(2, new EscapeDangerGoal(this, 1.0));
		this.goalSelector.add(3, new WanderAroundTargetGoal(this, 0.8, 13.0f));
		this.goalSelector.add(5, new IronGolemWanderAroundGoal(this, 0.8));
		this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(8, new LookAroundGoal(this));
		this.targetSelector.add(2, new RevengeGoal(this));
		this.targetSelector.add(3, new ActiveTargetGoal<>(this, MobEntity.class, 5, false, false, entity -> entity instanceof Monster && !(entity instanceof CreeperEntity)));
	}

	@Override
	public void tick()
	{
		super.tick();
		if (!this.getWorld().isClient())
		{
			if (this.summonGoal != null)
			{
				this.setSummonCooledDown(this.summonGoal.isCooledDown());
			}
		}
	}

	@Override
	public boolean isPushable()
	{
		return getSummonState() == 0;
	}

	public boolean isReadyToSummon()
	{
		return (this.getTarget() != null && this.getTarget().isAlive());
	}
	@Override
	public boolean trySummon(int summonState)
	{
		if (summonState != spawnStage) return false;

		this.getWorld().sendEntityStatus(this, EntityStatuses.PLAY_ATTACK_SOUND);
		this.playSound(SoundEvents.BLOCK_BEACON_POWER_SELECT, 1.0f, 1.0f);
		spawnPawns(pawnsToSpawn);
		spawnEffect(this.getWorld(), this, 10, 4, new Vec3d(this.getX(), this.getY() + 2.5d, this.getZ()));
		return true;
	}

	private void spawnPawns(int pawnCount)
	{
		for (int i = 0; i < pawnCount; i++)
		{
			double direction = this.random.nextInt(360) * MathHelper.RADIANS_PER_DEGREE;
//			double offset = this.random.nextInt(4) + 2;
			double offset = 0.0d;
			Vec3d spawnOffset = new Vec3d(Math.sin(direction) * offset,
					2.5f,
					Math.cos(direction) * offset);
//			BlockState bs = getWorld().getBlockState(new BlockPos((int)(getX() + spawnOffset.getX()), (int)(getY() + spawnOffset.getY()),(int)(getZ() + spawnOffset.getZ())));
//			BlockState bsUnder = getWorld().getBlockState(new BlockPos((int)(getX() + spawnOffset.getX()), (int)(getY() + spawnOffset.getY() - 1),(int)(getZ() + spawnOffset.getZ())));
//			int failCount = 0;
//			while (!bs.isAir() || bs.isOpaque() || bsUnder.isAir() || !bsUnder.isOpaque())
//			{
//				spawnOffset = spawnOffset.add(0,1,0);
//				bs = getWorld().getBlockState(new BlockPos((int)(getX() + spawnOffset.getX()), (int)(getY() + spawnOffset.getY()),(int)(getZ() + spawnOffset.getZ())));
//				bsUnder = getWorld().getBlockState(new BlockPos((int)(getX() + spawnOffset.getX()), (int)(getY() + spawnOffset.getY() - 1),(int)(getZ() + spawnOffset.getZ())));
//				failCount++;
//				if (failCount > 5) break;
//			}
//			if (failCount > 5) continue;

			EntityPawn pawn = ModEntities.ENTITY_PAWN_FIRST_DIORITE.create(getWorld());
			if (pawn == null) continue;
			pawn.setOwnerType(EntityPawn.OWNER_TYPES.FIRST_OF_DIORITE.ordinal());
			pawn.setOwner(this);
			pawn.setPlayerCreated(isPlayerCreated());
			pawn.setPawnTypeDiorite();
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

	private void spawnEffect(World world, LivingEntity user, int time, float range, Vec3d position)
	{
		EntityEffectCubeDandoriWhistle whistleEffect = ModEntities.ENTITY_EFFECT_CUBE_DANDORI_WHISTLE.create(world);
		if (whistleEffect != null)
		{
			whistleEffect.setPosition(position);
			whistleEffect.setLifeTime(time);
			whistleEffect.setFullScale(range * 2.0f);
			whistleEffect.setBodyYaw(this.getBodyYaw());
			whistleEffect.setYaw(this.getYaw());
			world.spawnEntity(whistleEffect);
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
			EntityGolemFirstDiorite pGolem = event.getAnimatable();
			if (pGolem.getSummonState() > 0)
			{
				switch (pGolem.getSummonState())
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
				pGolem.setSummonState(0);
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
