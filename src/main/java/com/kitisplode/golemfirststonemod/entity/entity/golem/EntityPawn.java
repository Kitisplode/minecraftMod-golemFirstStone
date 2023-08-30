package com.kitisplode.golemfirststonemod.entity.entity.golem;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.EntityVillagerDandori;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.EntityGolemFirstDiorite;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityCanAttackBlocks;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDandoriCount;
import com.kitisplode.golemfirststonemod.sound.ModSounds;
import com.kitisplode.golemfirststonemod.util.ExtraMath;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
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
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;

import java.util.EnumSet;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class EntityPawn extends IronGolem implements GeoEntity, IEntityDandoriFollower, IEntityCanAttackBlocks
{
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final EntityDataAccessor<Integer> OWNER_TYPE = SynchedEntityData.defineId(EntityPawn.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PAWN_TYPE = SynchedEntityData.defineId(EntityPawn.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DANDORI_STATE = SynchedEntityData.defineId(EntityPawn.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> THROWN = SynchedEntityData.defineId(EntityPawn.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(EntityPawn.class, EntityDataSerializers.OPTIONAL_UUID);
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
    public float thrownAngle = 0.0f;
    public BlockPos blockTarget = null;
    private int blockBreakProgress = 0;
    protected Predicate<BlockState> bsPredicate = blockState -> blockState != null
            && (blockState.is(Blocks.DIRT));
    private int noDandoriTimer = 0;
    private static final int noDandoriTime = 30;
    public enum OWNER_TYPES {WANDERING, FIRST_OF_DIORITE, PLAYER, VILLAGER_DANDORI};
    public enum PAWN_TYPES {DIORITE_ACTION, DIORITE_FORESIGHT, DIORITE_KNOWLEDGE, PIK_YELLOW, PIK_PINK, PIK_BLUE};

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
    protected int decreaseAirSupply(int pAir) {
        if (this.getPawnType() == PAWN_TYPES.PIK_BLUE.ordinal()) return pAir;
        return pAir - 1;
    }

    @Override
    public boolean isThrowable()
    {
        return true;
    }


    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        if (!this.entityData.hasItem(PAWN_TYPE)) this.entityData.define(PAWN_TYPE, pawnType);
        if (!this.entityData.hasItem(OWNER_TYPE)) this.entityData.define(OWNER_TYPE, 0);
        if (!this.entityData.hasItem(DANDORI_STATE)) this.entityData.define(DANDORI_STATE, false);
        if (!this.entityData.hasItem(THROWN)) this.entityData.define(THROWN, false);
        if (!this.entityData.hasItem(OWNER_UUID)) this.entityData.define(OWNER_UUID, Optional.empty());
    }
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("wasOnGround", this.onGroundLastTick);
        pCompound.putInt("pawnType", this.getPawnType());
        pCompound.putInt("ownerType", this.getOwnerType());
        if (this.getOwnerUUID() != null)
        {
            pCompound.putUUID("Owner", this.getOwnerUUID());
        }
    }
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.onGroundLastTick = pCompound.getBoolean("wasOnGround");
        if (pCompound.contains("pawnType")) setPawnType(pCompound.getInt("pawnType"));
        if (pCompound.contains("ownerType")) setOwnerType(pCompound.getInt("ownerType"));
        UUID uuid;
        if (pCompound.hasUUID("Owner")) {
            uuid = pCompound.getUUID("Owner");
        } else {
            String s = pCompound.getString("Owner");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }
        if (uuid != null) {
            try {
                this.setOwnerUUID(uuid);
            } catch (Throwable throwable) {}
        }
    }
    @javax.annotation.Nullable
    public UUID getOwnerUUID() {
        return this.entityData.get(OWNER_UUID).orElse((UUID)null);
    }

    public void setOwnerUUID(@javax.annotation.Nullable UUID pUuid) {
        this.entityData.set(OWNER_UUID, Optional.ofNullable(pUuid));
    }
    @Nullable
    public LivingEntity getOwner()
    {
        if (this.getOwnerType() == OWNER_TYPES.PLAYER.ordinal())
        {
            UUID uUID = this.getOwnerUUID();
            if (uUID == null)
                return null;
            return this.level().getPlayerByUUID(uUID);
        }
        else return owner;
    }
    public void setOwner(LivingEntity entity)
    {
        if (this.getOwnerType() == OWNER_TYPES.PLAYER.ordinal())
        {
            if (entity != null)
            {
                setOwnerUUID(entity.getUUID());
            }
        }
        owner = entity;
    }
    @Override
    public boolean isOwner(LivingEntity entity)
    {
        return entity.getUUID() == this.getOwnerUUID();
    }
    public boolean getDandoriState()
    {
        return this.entityData.get(DANDORI_STATE);
    }
    public void setDandoriState(boolean pDandoriState)
    {
        if (this.getOwner() != null) ((IEntityWithDandoriCount) this.getOwner()).setRecountDandori();
        noDandoriTimer = noDandoriTime;
        if (pDandoriState)
        {
            if (this.getOwner() != null && !this.getDandoriState())
                this.playSound(ModSounds.ENTITY_VILLAGER_DANDORI_PLUCK.get(), 0.2f, this.random.nextFloat() * 0.4f + 0.3f);
        }
        this.entityData.set(DANDORI_STATE, pDandoriState);
    }

    public int getPawnType()
    {
        return this.entityData.get(PAWN_TYPE);
    }
    public void setPawnType(int pPawnType)
    {
        pawnType = pPawnType;
        this.entityData.set(PAWN_TYPE, pawnType);
        if (pPawnType > 2)
        {
            this.safeRange = 3;
            this.panicRange = 16;
        }
//        if (pPawnType == PAWN_TYPES.PIK_BLUE.ordinal())
//        {
//            bsPredicate = blockState -> blockState != null
//                    && (blockState.is(Blocks.DIRT)
//                    || blockState.is(Blocks.ICE));
//        }
        else if (pPawnType == PAWN_TYPES.PIK_PINK.ordinal())
        {
            bsPredicate = blockState -> blockState != null
                    && (blockState.is(Blocks.DIRT)
                    || blockState.is(Blocks.MAGMA_BLOCK)
                    || blockState.is(Blocks.FIRE));
        }
    }
    public void setPawnTypeDiorite()
    {
        setPawnType(this.random.nextInt(3));
    }
    public void setPawnTypePik()
    {
        setPawnType(this.random.nextInt(3) + 3);
    }

    public void setOwnerType(int pOwnerType)
    {
        this.entityData.set(OWNER_TYPE, pOwnerType);
    }
    public int getOwnerType()
    {
        return this.entityData.get(OWNER_TYPE);
    }

    public void setThrown(boolean pThrown)
    {
        this.entityData.set(THROWN, pThrown);
    }
    public boolean getThrown()
    {
        return this.entityData.get(THROWN);
    }

    private float getAttackDamage() {
        float multiplier = 1.0f;
        if (getPawnType() == PAWN_TYPES.PIK_PINK.ordinal()) multiplier = 2.0f;
        return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * multiplier;
    }

    public void setBlockTarget(BlockPos pBlockPos)
    {
        blockTarget = pBlockPos;
    }
    public BlockPos getBlockTarget()
    {
        return blockTarget;
    }
    public boolean canTargetBlock(BlockPos bp)
    {
        return bsPredicate.test(level().getBlockState(bp));
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(0, new EntityPawn.SlimeFloatGoal(this));
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
            if (entity instanceof Creeper) return false;
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
        if (this.getTarget() != null && !this.getTarget().isAlive()) this.setTarget(null);
        if (this.onGround() && !this.onGroundLastTick)
        {
            if (this.getThrown()) this.setThrown(false);
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
        this.onGroundLastTick = this.onGround();
        if (!this.level().isClientSide())
        {
            // If we're not a wandering pawn, find an owner if we don't have one.
            if (this.getOwnerType() != 0 && this.getOwnerType() != 2)
            {
                if (this.getOwner() == null)
                {
                    if (timeWithoutParent++ % 20 == 0)
                    {
                        TargetingConditions tp = TargetingConditions.forNonCombat().range(ownerSearchRange * 2);
                        LivingEntity newParent = null;
                        if (this.getOwnerType() == 1)
                            newParent = level().getNearestEntity(EntityGolemFirstDiorite.class, tp, this, getX(), getY(), getZ(), getBoundingBox().inflate(ownerSearchRange * 2));
                        else if (this.getOwnerType() == 3)
                            newParent = level().getNearestEntity(EntityVillagerDandori.class, tp, this, getX(), getY(), getZ(), getBoundingBox().inflate(ownerSearchRange * 2));
                        if (newParent != null) this.setOwner(newParent);
                    }
                } else timeWithoutParent = 0;
                if (this.getTarget() == null) timeWithoutTarget++;
                else timeWithoutTarget = 0;

                // Pawns that follow players or First of Diorite starve if they don't have a target.
                if (this.getOwnerType() == 1)
                {
                    if (timeWithoutParent > timeWithoutParentMax || timeWithoutTarget > timeWithoutTargetMax)
                    {
                        if (this.tickCount % 20 == 0) this.hurt(this.damageSources().starve(), 1);
                    }
                }
            }
            // Drop a block target if we've been ordered to do other things.
            if ((this.getTarget() != null || this.getDandoriState()) && this.blockTarget != null)
            {
                level().destroyBlockProgress(getId(), this.blockTarget, -1);
                this.blockTarget = null;
                this.blockBreakProgress = 0;
            }
            else if (this.blockTarget != null)
            {
                if (distanceToSqr(this.blockTarget.getCenter()) < Mth.square(1.25))
                {
                    Vec3 newVelocity = this.position().subtract(this.blockTarget.getCenter()).normalize().scale(0.25);
                    this.setDeltaMovement(newVelocity.x, 0.25d, newVelocity.z);
                    this.blockBreakProgress += 16;
                    if (this.getPawnType() == PAWN_TYPES.PIK_YELLOW.ordinal())
                        this.blockBreakProgress += 16;
                    BlockState bs = level().getBlockState(this.blockTarget);
                    if (this.blockBreakProgress >= 100 || bs.is(Blocks.FIRE))
                    {
                        this.playSound(SoundEvents.ROOTED_DIRT_BREAK, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
                        bs.getBlock().onBlockStateChange(level(), this.blockTarget, bs, null);
                        level().removeBlock(this.blockTarget, false);
                        level().levelEvent(2001, this.blockTarget, Block.getId(level().getBlockState(this.blockTarget)));
                        this.findNewTargetBlock();
                        this.blockBreakProgress = 0;
                    }
                    else
                    {
                        this.playSound(SoundEvents.ROOTED_DIRT_HIT, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
                        level().destroyBlockProgress(getId(), this.blockTarget, this.blockBreakProgress);
                    }
                }
            }
            // If we're not in dandori and the player owner is near, and we're not doing anything anyways, just dandori.
            if (this.getOwnerType() == OWNER_TYPES.PLAYER.ordinal() && this.getOwner() != null && !this.getDandoriState())
            {
                if (this.getTarget() == null && this.blockTarget == null && this.distanceToSqr(this.getOwner()) < Mth.square(safeRange + 1) && noDandoriTimer == 0)
                {
                    ((IEntityWithDandoriCount) this.getOwner()).setRecountDandori();
                    this.setDandoriState(true);
                }
            }
            if (noDandoriTimer > 0) noDandoriTimer--;
        }
        else
        {
            if (getThrown())
            {
                thrownAngle -= 30.0f;
            }
            else thrownAngle = 0.0f;
        }
    }

    @Override
    public float getThrowAngle()
    {
        return thrownAngle;
    }

    @Override
    public void push(Entity pEntity)
    {
        if (pEntity == this.getOwner()) return;
        super.push(pEntity);
        if (pEntity instanceof Enemy && this.isEffectiveAi() && (this.getDeltaMovement().lengthSqr() > 0 || pEntity.getPassengers().contains(this))) {
            this.dealDamage((LivingEntity)pEntity);
            if (getThrown())
            {
                setThrown(false);
                this.startRiding(pEntity);
            }
        }
    }

    @Override
    public boolean isOnFire()
    {
        if (this.getPawnType() == PAWN_TYPES.PIK_PINK.ordinal()) return false;
        return super.isOnFire();
    }

    @Override
    public boolean hurt(DamageSource source, float amount)
    {
        if (source.is(DamageTypeTags.IS_FALL)) return false;
        if (source.is(DamageTypeTags.IS_FIRE) && this.getPawnType() == PAWN_TYPES.PIK_PINK.ordinal()) return false;
        if (source.is(DamageTypeTags.IS_LIGHTNING) && this.getPawnType() == PAWN_TYPES.PIK_YELLOW.ordinal()) return false;
        return super.hurt(source, amount);
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

    @Override
    public void remove(Entity.RemovalReason pReason)
    {
        if (this.getOwnerType() == OWNER_TYPES.PLAYER.ordinal() && this.getDandoriState() && this.getOwner() != null)
        {
            ((IEntityWithDandoriCount) this.getOwner()).setRecountDandori();
        }
        super.remove(pReason);
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

    private void lookAtPos(Vec3 position, float maxYawChange, float maxPitchChange)
    {
        double f = position.y() - this.getEyeY();
        double d = position.x() - this.getX();
        double e = position.z() - this.getZ();
        double g = Math.sqrt(d * d + e * e);
        float h = (float)(Mth.atan2(e, d) * 57.2957763671875) - 90.0f;
        float i = (float)(-(Mth.atan2(f, g) * 57.2957763671875));
        this.setXRot(ExtraMath.changeAngle(this.getXRot(), i, maxPitchChange));
        this.setYRot(ExtraMath.changeAngle(this.getYRot(), h, maxYawChange));
    }

    public void handleEntityEvent(byte pId)
    {
        if (pId == IEntityDandoriFollower.ENTITY_EVENT_DANDORI_START) addDandoriParticles();
        else super.handleEntityEvent(pId);
    }
    protected void addDandoriParticles()
    {
        level().addParticle(ParticleTypes.NOTE,
                getX(), getY() + getBbHeight() * 1.5, getZ(),
                0,1,0);
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
        if (this.getOwner() == null && this.getPawnType() < 3)
            return switch (this.getPawnType())
            {
                case 0 -> new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/pawn/diorite/diorite_action.png");
                case 1 -> new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/pawn/diorite/diorite_foresight.png");
                default -> new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/pawn/diorite/diorite_knowledge.png");
            };
        else
            return switch (this.getPawnType())
            {
                case 0 -> new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/pawn/diorite/diorite_action_active.png");
                case 1 -> new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/pawn/diorite/diorite_foresight_active.png");
                case 2 -> new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/pawn/diorite/diorite_knowledge_active.png");
                case 3 -> new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/pawn/pik/pawn_pik_yellow.png");
                case 4 -> new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/pawn/pik/pawn_pik_pink.png");
                default -> new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/pawn/pik/pawn_pik_blue.png");
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


    static class SlimeAttackGoal extends Goal
    {
        private final EntityPawn pawn;
        private int growTiredTimer;
        Vec3 targetPos;
        Vec3 previousTargetPos;

        public SlimeAttackGoal(EntityPawn pSlime) {
            this.pawn = pSlime;
            this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        }

        public boolean canUse() {
            LivingEntity owner = this.pawn.getOwner();
            // Allow pawns to face their target if it's close enough.
            if (owner != null)
            {
                if (owner.distanceToSqr(this.pawn) > Mth.square(this.pawn.panicRange)) return false;
            }
            // Allow only pawns with owners to attack
            else return false;

            LivingEntity target = this.pawn.getTarget();
            if (target == null && this.pawn.blockTarget == null) {
                return false;
            }
            if (target != null)
            {
                if (!this.pawn.canAttack(target)) return false;
            }
            if (this.pawn.blockTarget != null)
            {
                BlockState bs = this.pawn.level().getBlockState(this.pawn.blockTarget);
                if (!this.pawn.bsPredicate.test(bs))
                {
                    this.pawn.findNewTargetBlock();
                    return false;
                }
            }
            return this.pawn.getMoveControl() instanceof EntityPawn.SlimeMoveControl;
        }

        public void start() {
            this.growTiredTimer = reducedTickDelay(150);
            targetPos = null;
            previousTargetPos = null;
            super.start();
        }

        public boolean canContinueToUse() {
            LivingEntity target = this.pawn.getTarget();
            if (target == null && this.pawn.blockTarget == null) {
                return false;
            }
            if (target != null)
            {
                if (!this.pawn.canAttack(target)) return false;
            }
            if (this.pawn.blockTarget != null)
            {
                BlockState bs = this.pawn.level().getBlockState(this.pawn.blockTarget);
                if (!this.pawn.bsPredicate.test(bs))
                {
                    this.pawn.findNewTargetBlock();
                    return false;
                }
            }
            return --this.growTiredTimer > 0;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity target = this.pawn.getTarget();
            if (target != null)
            {
                if (this.pawn.hasLineOfSight(target))
                {
                    targetPos = target.position();
                }
                else
                {
                    if (previousTargetPos == null || !previousTargetPos.equals(target.position()))
                    {
                        Path path = this.pawn.getNavigation().createPath(target, 1);
                        if (path != null && path.getNodeCount() > 1)
                        {
                            targetPos = path.getEntityPosAtNode(this.pawn, 1);
                        }
                    }
                    previousTargetPos = target.position();
                }
            }
            else if (this.pawn.blockTarget != null)
            {
                Path path = this.pawn.getNavigation().createPath(this.pawn.blockTarget, 1);
                if (path != null && path.getNodeCount() > 1)
                {
                    targetPos = path.getEntityPosAtNode(this.pawn, 1);
                }
            }

            if (targetPos != null) this.pawn.lookAtPos(targetPos, 10.0F, 10.0F);
            MoveControl movecontrol = this.pawn.getMoveControl();
            if (movecontrol instanceof EntityPawn.SlimeMoveControl slime$slimemovecontrol) {
                slime$slimemovecontrol.setDirection(this.pawn.getYRot(), this.pawn.isEffectiveAi());
            }
        }
    }

    static class LookAtOwnerGoal extends Goal
    {
        private final EntityPawn pawn;
        private int ticksLeft;
        Vec3 targetPos;
        Vec3 previousTargetPos;

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
                if (this.pawn.getOwnerType() != OWNER_TYPES.PLAYER.ordinal())
                {
                    double sqrDistanceToOwner = owner.distanceToSqr(this.pawn);
                    if (sqrDistanceToOwner > Mth.square(this.pawn.panicRange)
                            || (sqrDistanceToOwner > Mth.square(this.pawn.safeRange) && this.pawn.getTarget() == null))
                        return (this.pawn.onGround() || this.pawn.hasEffect(MobEffects.LEVITATION)) && this.pawn.getMoveControl() instanceof EntityPawn.SlimeMoveControl;
                }
                else
                {
                    return this.pawn.getDandoriState();
                }
            }
            return false;
        }

        public void start() {
            this.ticksLeft = reducedTickDelay(20);
            targetPos = null;
            previousTargetPos = null;
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
            LivingEntity owner = this.pawn.getOwner();
            if (owner != null)
            {
                if (this.pawn.hasLineOfSight(owner))
                {
                    targetPos = owner.position();
                }
                else
                {
                    if (previousTargetPos == null || !previousTargetPos.equals(owner.position()))
                    {
                        Path path = this.pawn.getNavigation().createPath(owner, 1);
                        if (path != null && path.getNodeCount() > 1)
                        {
                            targetPos = path.getEntityPosAtNode(owner, 1);
                        }
                    }
                    previousTargetPos = owner.position();
                }
            }

            if (targetPos != null) this.pawn.lookAtPos(targetPos, 10.0F, 10.0F);
            MoveControl movecontrol = this.pawn.getMoveControl();
            if (movecontrol instanceof EntityPawn.SlimeMoveControl slime$slimemovecontrol) {
                slime$slimemovecontrol.setDirection(this.pawn.getYRot(), this.pawn.isEffectiveAi());
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

        public boolean canUse() {
            LivingEntity owner = this.pawn.getOwner();
            if (owner != null)
            {
                if (owner.distanceToSqr(this.pawn) > Mth.square(this.pawn.panicRange)) return false;
                return this.pawn.getTarget() == null && (this.pawn.onGround() || this.pawn.isInWater() || this.pawn.isInLava() || this.pawn.hasEffect(MobEffects.LEVITATION)) && this.pawn.getMoveControl() instanceof EntityPawn.SlimeMoveControl;
            }
            return false;
        }

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

    static class SlimeFloatGoal extends Goal {
        private final EntityPawn pawn;

        public SlimeFloatGoal(EntityPawn pSlime) {
            this.pawn = pSlime;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
            pSlime.getNavigation().setCanFloat(true);
        }

        public boolean canUse() {
            if (this.pawn.getPawnType() != PAWN_TYPES.PIK_BLUE.ordinal()) return false;
            if (this.pawn.getDandoriState() && this.pawn.getOwner() != null && this.pawn.getOwner().getY() < this.pawn.getY() && !this.pawn.onGround()) return false;
            if (!this.pawn.getDandoriState() && this.pawn.getTarget() != null && this.pawn.getTarget().getY() < this.pawn.getY() && !this.pawn.onGround()) return false;
            return (this.pawn.isInWater() || this.pawn.isInLava()) && this.pawn.getMoveControl() instanceof EntityPawn.SlimeMoveControl;
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        public void tick() {
            if (this.pawn.getRandom().nextFloat() < 0.8F) {
                this.pawn.getJumpControl().jump();
            }

            MoveControl movecontrol = this.pawn.getMoveControl();
            if (movecontrol instanceof EntityPawn.SlimeMoveControl slime$slimemovecontrol)
            {
                float speed = 2.0f;
                if (!this.pawn.getDandoriState() && this.pawn.getTarget() == null && this.pawn.blockTarget == null) speed = 0.0f;
                slime$slimemovecontrol.setWantedMovement(speed);
            }
        }
    }

    static class SlimeKeepOnJumpingGoal extends Goal {
        private final EntityPawn pawn;

        public SlimeKeepOnJumpingGoal(EntityPawn pSlime) {
            this.pawn = pSlime;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        public boolean canUse() {
            if (this.pawn.isPassenger()) return false;
            if (this.pawn.getOwner() == null) return false;
            if (this.pawn.distanceToSqr(this.pawn.getOwner()) < Mth.square(this.pawn.safeRange))
            {
                return !this.pawn.getDandoriState() && (this.pawn.getTarget() != null || this.pawn.blockTarget != null);
            }
            return true;
        }

        public void tick() {
            MoveControl movecontrol = this.pawn.getMoveControl();
            if (movecontrol instanceof EntityPawn.SlimeMoveControl slime$slimemovecontrol)
            {
                if (this.pawn.getOwnerType() == OWNER_TYPES.PLAYER.ordinal())
                {
                    float speed = 1.75f;
                    if (!this.pawn.getDandoriState() && this.pawn.getTarget() == null && this.pawn.blockTarget == null) speed = 0.0f;
                    slime$slimemovecontrol.setWantedMovement(speed);
                }
                else
                    slime$slimemovecontrol.setWantedMovement(1.0D);
            }

        }
    }
}
