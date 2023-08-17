package com.kitisplode.golemfirststonemod.entity.entity;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemFirstDiorite;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;

import java.util.EnumSet;
import java.util.function.Predicate;

public class EntityPawn extends IronGolem implements GeoEntity
{
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final EntityDataAccessor<Integer> OWNER_TYPE = SynchedEntityData.defineId(EntityPawn.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PAWN_TYPE = SynchedEntityData.defineId(EntityPawn.class, EntityDataSerializers.INT);
    private int pawnType = 0;
    private boolean onGroundLastTick;
    public static final double ownerSearchRange = 32;
    public double panicRange = 20;
    public double safeRange = 8;
    private LivingEntity owner = null;
    private int timeWithoutParent = 0;
    private static final int timeWithoutParentMax = 100;
    private int timeWithoutTarget = 0;
    private static final int timeWithoutTargetMax = 30 * 20;
    public enum OWNER_TYPES {WANDERING, FIRST_OF_DIORITE, PLAYER, VILLAGER_DANDORI};

    public EntityPawn(EntityType<? extends IronGolem> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
        setPawnType(pLevel.getRandom().nextInt(3));
        this.moveControl = new EntityPawn.SlimeMoveControl(this);
    }

    public static AttributeSupplier setAttributes()
    {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.5f)
                .add(Attributes.ATTACK_DAMAGE, 5.0f)
                .add(Attributes.FOLLOW_RANGE, 32)
                .build();
    }

    @Override
    public double getEyeY()
    {
        return getY() + 0.5f;
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        if (!this.entityData.hasItem(PAWN_TYPE)) this.entityData.define(PAWN_TYPE, pawnType);
        if (!this.entityData.hasItem(OWNER_TYPE)) this.entityData.define(OWNER_TYPE, 0);
    }

    public int getPawnType()
    {
        return this.entityData.get(PAWN_TYPE);
    }
    private void setPawnType(int pPawnType)
    {
        pawnType = pPawnType;
        this.entityData.set(PAWN_TYPE, pawnType);
    }
    public void setPawnTypeDiorite()
    {
        setPawnType(this.random.nextInt(3));
    }
    public void setPawnTypePik()
    {
        setPawnType(this.random.nextInt(3) + 3);
        this.safeRange = 2;
        this.panicRange = 16;
    }
    public int getOwnerType()
    {
        return this.entityData.get(OWNER_TYPE);
    }
    public void setOwnerType(int pOwnerType)
    {
        this.entityData.set(OWNER_TYPE, pOwnerType);
    }

    private float getAttackDamage() {
        return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(1, new EntityPawn.LookAtOwnerGoal(this));
        this.goalSelector.addGoal(2, new EntityPawn.SlimeAttackGoal(this));
        this.goalSelector.addGoal(3, new EntityPawn.SlimeRandomDirectionGoal(this));
        this.goalSelector.addGoal(5, new EntityPawn.SlimeKeepOnJumpingGoal(this));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, attackTarget()));
    }

    private Predicate<LivingEntity> attackTarget()
    {
        return entity ->
        {
            if (entity instanceof Enemy)
            {
                if (this.getOwner() != null)
                {
                    return this.getOwner().distanceToSqr(entity) < Mth.square(panicRange);
                }
                return true;
            }
            return false;
        };
    }

    @Override
    public void tick()
    {
        super.tick();
        this.onGroundLastTick = this.onGround();
        if (this.onGround() && !this.onGroundLastTick)
        {
            int i = 1;
            for (int j = 0; j < i * 8; ++j)
            {
                float f = this.random.nextFloat() * ((float) Math.PI * 2);
                float g = this.random.nextFloat() * 0.5f + 0.5f;
                float h = Mth.sin(f) * (float) i * 0.5f * g;
                float k = Mth.cos(f) * (float) i * 0.5f * g;
                this.level().addParticle(this.getParticleType(), this.getX() + (double) h, this.getY(), this.getZ() + (double) k, 0.0, 0.0, 0.0);
            }
            this.playSound(this.getSquishSound(), 1, ((this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f) / 0.8f);
        }

        // If we're not a wandering pawn, find an owner if we don't have one.
        if (this.getOwnerType() != 0)
        {
            if (this.getOwner() == null)
            {
                if (timeWithoutParent++ % 20 == 0)
                {
                    TargetingConditions tp = TargetingConditions.forNonCombat().range(ownerSearchRange * 2);
                    LivingEntity newParent = null;
                    if (this.getOwnerType() == 1)
                    {
                        newParent = level().getNearestEntity(EntityGolemFirstDiorite.class, tp, this, getX(), getY(), getZ(), getBoundingBox().inflate(ownerSearchRange * 2));
                    }
                    else if (this.getOwnerType() == 2)
                    {
                        newParent = level().getNearestPlayer(tp, this);
                    }
                    else if (this.getOwnerType() == 3)
                    {
                        newParent = level().getNearestEntity(EntityVillagerDandori.class, tp, this, getX(), getY(), getZ(), getBoundingBox().inflate(ownerSearchRange * 2));
                    }
                    if (newParent != null) this.setOwner(newParent);
                }
            }
            else timeWithoutParent = 0;
            if (this.getTarget() == null) timeWithoutTarget++;
            else timeWithoutTarget = 0;

            // Pawns that follow players or First of Diorite starve if they don't have a target.
            if (this.getOwnerType() != 3)
            {
                if (timeWithoutParent > timeWithoutParentMax || timeWithoutTarget > timeWithoutTargetMax)
                {
                    if (this.tickCount % 20 == 0) this.hurt(this.damageSources().starve(), 1);
                }
            }
        }
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("wasOnGround", this.onGroundLastTick);
        pCompound.putInt("pawnType", this.getPawnType());
        pCompound.putInt("ownerType", this.getOwnerType());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.onGroundLastTick = pCompound.getBoolean("wasOnGround");
        if (pCompound.contains("pawnType")) setPawnType(pCompound.getInt("pawnType"));
        if (pCompound.contains("ownerType")) setOwnerType(pCompound.getInt("ownerType"));
    }

    @Nullable
    public LivingEntity getOwner()
    {
        return owner;
    }

    public void setOwner(LivingEntity entity)
    {
        owner = entity;
    }

    @Override
    public void push(Entity pEntity)
    {
        super.push(pEntity);
        if (pEntity instanceof Enemy && this.isEffectiveAi()) {
            this.dealDamage((LivingEntity)pEntity);
        }
    }

    protected void dealDamage(LivingEntity pLivingEntity)
    {
        if (this.isAlive())
        {
            if (this.distanceToSqr(pLivingEntity) < 4 && this.hasLineOfSight(pLivingEntity) && pLivingEntity.hurt(this.damageSources().mobAttack(this), this.getAttackDamage())) {
                this.playSound(SoundEvents.BONE_BLOCK_PLACE, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                this.doEnchantDamageEffects(this, pLivingEntity);
            }
        }
    }

    public int getMaxHeadXRot() {
        return 0;
    }

    protected void jumpFromGround()
    {
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.x, (double)this.getJumpPower(), vec3.z);
        this.hasImpulse = true;
    }

    protected int getJumpDelay() {
        return this.random.nextInt(20) + 10;
    }

    protected ParticleOptions getParticleType() {
        return ParticleTypes.WHITE_ASH;
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return null;
    }

    protected SoundEvent getDeathSound() {
        return null;
    }

    protected SoundEvent getSquishSound() {
        return SoundEvents.BONE_BLOCK_HIT;
    }

    protected SoundEvent getJumpSound() {
        return SoundEvents.BONE_BLOCK_PLACE;
    }

    protected float getSoundPitch() {
        float f = 0.8F;
        return ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * f;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, event ->
                PlayState.CONTINUE));
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }

    public ResourceLocation getModelLocation()
    {
        return switch (this.getPawnType())
        {
            case 0 -> new ResourceLocation(GolemFirstStoneMod.MOD_ID, "geo/diorite_action.geo.json");
            case 1 -> new ResourceLocation(GolemFirstStoneMod.MOD_ID, "geo/diorite_foresight.geo.json");
            case 2 -> new ResourceLocation(GolemFirstStoneMod.MOD_ID, "geo/diorite_knowledge.geo.json");
            default -> new ResourceLocation(GolemFirstStoneMod.MOD_ID, "geo/pawn_pik.geo.json");
        };
    }

    public ResourceLocation getTextureLocation()
    {
        if (this.getOwner() == null)
            return switch (this.getPawnType())
            {
                case 0 -> new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/diorite_action.png");
                case 1 -> new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/diorite_foresight.png");
                case 2 -> new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/diorite_knowledge.png");
                case 3 -> new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/pik/pawn_pik_yellow.png");
                case 4 -> new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/pik/pawn_pik_pink.png");
                default -> new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/pik/pawn_pik_blue.png");
            };
        else
            return switch (this.getPawnType())
            {
                case 0 -> new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/diorite_action_active.png");
                case 1 -> new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/diorite_foresight_active.png");
                case 2 -> new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/diorite_knowledge_active.png");
                case 3 -> new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/pik/pawn_pik_yellow.png");
                case 4 -> new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/pik/pawn_pik_pink.png");
                default -> new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/pik/pawn_pik_blue.png");
            };
    }

    // =================================================================================================================
    // Custom goals

    static class SlimeMoveControl extends MoveControl
    {
        private float yRot;
        private int jumpDelay;
        private final EntityPawn slime;
        private boolean isAggressive;

        public SlimeMoveControl(EntityPawn pSlime) {
            super(pSlime);
            this.slime = pSlime;
            this.yRot = 180.0F * pSlime.getYRot() / (float)Math.PI;
        }

        public void setDirection(float pYRot, boolean pAggressive) {
            this.yRot = pYRot;
            this.isAggressive = pAggressive;
        }

        public void setWantedMovement(double pSpeed) {
            this.speedModifier = pSpeed;
            this.operation = MoveControl.Operation.MOVE_TO;
        }

        public void tick() {
            this.mob.setYRot(this.rotlerp(this.mob.getYRot(), this.yRot, 90.0F));
            this.mob.yHeadRot = this.mob.getYRot();
            this.mob.yBodyRot = this.mob.getYRot();
            if (this.operation != MoveControl.Operation.MOVE_TO) {
                this.mob.setZza(0.0F);
            } else {
                this.operation = MoveControl.Operation.WAIT;
                if (this.mob.onGround()) {
                    this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                    if (this.jumpDelay-- <= 0) {
                        this.jumpDelay = this.slime.getJumpDelay();
                        if (this.isAggressive) {
                            this.jumpDelay /= 3;
                        }

                        this.slime.getJumpControl().jump();
                        this.slime.playSound(this.slime.getJumpSound(), this.slime.getSoundVolume(), this.slime.getSoundPitch());
                    } else {
                        this.slime.xxa = 0.0F;
                        this.slime.zza = 0.0F;
                        this.mob.setSpeed(0.0F);
                    }
                } else {
                    this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
                }

            }
        }
    }

    static class LookAtOwnerGoal extends Goal
    {
        private final EntityPawn pawn;
        private int ticksLeft;

        public LookAtOwnerGoal(EntityPawn pPawn)
        {
            this.pawn = pPawn;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity owner = this.pawn.getOwner();
            if (owner == null) return false;
            else
            {
                double sqrDistanceToOwner = owner.distanceToSqr(this.pawn);
                if (sqrDistanceToOwner > Mth.square(this.pawn.panicRange) ||
                        (sqrDistanceToOwner > Mth.square(this.pawn.safeRange) && this.pawn.getTarget() == null))
                    return (this.pawn.onGround() || this.pawn.hasEffect(MobEffects.LEVITATION)) && this.pawn.getMoveControl() instanceof EntityPawn.SlimeMoveControl;
            }
            return false;
        }

        public void start() {
            this.ticksLeft = reducedTickDelay(200);
            super.start();
        }

        public boolean canContinueToUse() {
            LivingEntity livingEntity = this.pawn.getOwner();
            if (livingEntity == null) {
                return false;
            }
            if (livingEntity.distanceToSqr(this.pawn) < Mth.square(this.pawn.panicRange))
                return false;
            return --this.ticksLeft > 0;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity livingentity = this.pawn.getOwner();
            if (livingentity != null) {
                this.pawn.lookAt(livingentity, 10.0F, 10.0F);
            }

            MoveControl movecontrol = this.pawn.getMoveControl();
            if (movecontrol instanceof EntityPawn.SlimeMoveControl slime$slimemovecontrol) {
                slime$slimemovecontrol.setDirection(this.pawn.getYRot(), this.pawn.isEffectiveAi());
            }
        }
    }

    static class SlimeAttackGoal extends Goal
    {
        private final EntityPawn pawn;
        private int growTiredTimer;

        public SlimeAttackGoal(EntityPawn pSlime) {
            this.pawn = pSlime;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity owner = this.pawn.getOwner();
            if (owner != null)
            {
                if (owner.distanceToSqr(this.pawn) > Mth.square(this.pawn.panicRange)) return false;
            }
            LivingEntity livingentity = this.pawn.getTarget();
            if (livingentity == null) {
                return false;
            } else {
                return this.pawn.canAttack(livingentity) && this.pawn.getMoveControl() instanceof SlimeMoveControl;
            }
        }

        public void start() {
            this.growTiredTimer = reducedTickDelay(150);
            super.start();
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = this.pawn.getTarget();
            if (livingentity == null) {
                return false;
            } else if (!this.pawn.canAttack(livingentity)) {
                return false;
            } else {
                return --this.growTiredTimer > 0;
            }
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity livingentity = this.pawn.getTarget();
            if (livingentity != null) {
                this.pawn.lookAt(livingentity, 10.0F, 10.0F);
            }

            MoveControl movecontrol = this.pawn.getMoveControl();
            if (movecontrol instanceof EntityPawn.SlimeMoveControl slime$slimemovecontrol) {
                slime$slimemovecontrol.setDirection(this.pawn.getYRot(), this.pawn.isEffectiveAi());
            }
        }
    }

    static class SlimeKeepOnJumpingGoal extends Goal {
        private final EntityPawn slime;

        public SlimeKeepOnJumpingGoal(EntityPawn pSlime) {
            this.slime = pSlime;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        public boolean canUse() {
            return !this.slime.isPassenger();
        }

        public void tick() {
            MoveControl movecontrol = this.slime.getMoveControl();
            if (movecontrol instanceof EntityPawn.SlimeMoveControl slime$slimemovecontrol) {
                slime$slimemovecontrol.setWantedMovement(1.0D);
            }

        }
    }

    static class SlimeRandomDirectionGoal extends Goal {
        private final EntityPawn pawn;
        private float chosenDegrees;
        private int nextRandomizeTime;

        public SlimeRandomDirectionGoal(EntityPawn pSlime) {
            this.pawn = pSlime;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean canUse() {
            LivingEntity owner = this.pawn.getOwner();
            if (owner != null)
            {
                if (owner.distanceToSqr(this.pawn) > Mth.square(this.pawn.panicRange)) return false;
            }
            return this.pawn.getTarget() == null && (this.pawn.onGround() || this.pawn.isInWater() || this.pawn.isInLava() || this.pawn.hasEffect(MobEffects.LEVITATION)) && this.pawn.getMoveControl() instanceof EntityPawn.SlimeMoveControl;
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        public void tick() {
            if (--this.nextRandomizeTime <= 0) {
                this.nextRandomizeTime = this.adjustedTickDelay(40 + this.pawn.getRandom().nextInt(60));
                this.chosenDegrees = (float)this.pawn.getRandom().nextInt(360);
            }

            MoveControl movecontrol = this.pawn.getMoveControl();
            if (movecontrol instanceof EntityPawn.SlimeMoveControl slime$slimemovecontrol) {
                slime$slimemovecontrol.setDirection(this.chosenDegrees, false);
            }

        }
    }
}
