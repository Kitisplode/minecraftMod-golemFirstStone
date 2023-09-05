package com.kitisplode.golemfirststonemod.entity.entity.golem;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.EntityVillagerDandori;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.EntityGolemFirstDiorite;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityCanAttackBlocks;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDandoriCount;
import com.kitisplode.golemfirststonemod.sound.ModSounds;
import com.kitisplode.golemfirststonemod.util.ExtraMath;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ParticleUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
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

public class EntityPawn extends IronGolemEntity implements GeoEntity, IEntityDandoriFollower, IEntityCanAttackBlocks
{
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    protected static final TrackedData<Integer> OWNER_TYPE = DataTracker.registerData(EntityPawn.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> PAWN_TYPE = DataTracker.registerData(EntityPawn.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> DANDORI_STATE = DataTracker.registerData(EntityPawn.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> THROWN = DataTracker.registerData(EntityPawn.class, TrackedDataHandlerRegistry.BOOLEAN);
    protected static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(EntityPawn.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);

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
            && (blockState.isOf(Blocks.DIRT));
    private int noDandoriTimer = 0;
    private static final int noDandoriTime = 20;
    public enum OWNER_TYPES {WANDERING, FIRST_OF_DIORITE, PLAYER, VILLAGER_DANDORI};
    public enum PAWN_TYPES {DIORITE_ACTION, DIORITE_FORESIGHT, DIORITE_KNOWLEDGE, PIK_YELLOW, PIK_PINK, PIK_BLUE};

    public EntityPawn(EntityType<? extends IronGolemEntity> pEntityType, World pLevel)
    {
        super(pEntityType, pLevel);
        this.moveControl = new EntityPawn.SlimeMoveControl(this);
    }

    public static DefaultAttributeContainer.Builder setAttributes()
    {
        return GolemEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0f)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32);
    }

    @Override
    public double getEyeY()
    {
        return getY() + 0.5f;
    }

    @Override
    protected int getNextAirUnderwater(int air) {
        if (this.getPawnType() == PAWN_TYPES.PIK_BLUE.ordinal()) return air;
        return air - 1;
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
        this.dataTracker.startTracking(PAWN_TYPE, pawnType);
        this.dataTracker.startTracking(OWNER_TYPE, 0);
        if (!this.dataTracker.containsKey(DANDORI_STATE))
            this.dataTracker.startTracking(DANDORI_STATE, false);
        if (!this.dataTracker.containsKey(THROWN))
            this.dataTracker.startTracking(THROWN, false);
        if (!this.dataTracker.containsKey(OWNER_UUID))
            this.dataTracker.startTracking(OWNER_UUID, Optional.empty());
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("wasOnGround", this.onGroundLastTick);
        nbt.putInt("pawnType", this.pawnType);
        nbt.putInt("ownerType", this.getOwnerType());
        if (this.getOwnerUuid() != null)
        {
            nbt.putUuid("Owner", this.getOwnerUuid());
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.onGroundLastTick = nbt.getBoolean("wasOnGround");
        if (nbt.contains("pawnType"))
            setPawnType(nbt.getInt("pawnType"));
        if (nbt.contains("ownerType"))
            setOwnerType(nbt.getInt("ownerType"));
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
        if (this.getOwnerType() == OWNER_TYPES.PLAYER.ordinal())
        {
            UUID uUID = this.getOwnerUuid();
            if (uUID == null)
                return null;
            return this.getWorld().getPlayerByUuid(uUID);
        }
        else return owner;
    }
    @Override
    public void setOwner(LivingEntity newOwner)
    {
        if (this.getOwnerType() == OWNER_TYPES.PLAYER.ordinal())
        {
            if (newOwner != null)
            {
                setOwnerUuid(newOwner.getUuid());
            }
        }
        owner = newOwner;
    }

    @Override
    public boolean isImmobile()
    {
        return super.isImmobile();
    }
    @Nullable
    private UUID getOwnerUuid() {
        return this.dataTracker.get(OWNER_UUID).orElse(null);
    }
    private void setOwnerUuid(@Nullable UUID uuid)
    {
        this.dataTracker.set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    public boolean getDandoriState()
    {
        return this.dataTracker.get(DANDORI_STATE);
    }
    public void setDandoriState(boolean pDandoriState)
    {
        if (!pDandoriState)
        {
            if (this.getOwner() != null && this.getDandoriState()) ((IEntityWithDandoriCount) this.getOwner()).setRecountDandori();
            noDandoriTimer = noDandoriTime;
        }
        else
        {
            if (this.getOwner() != null && !this.getDandoriState())
                this.playSound(ModSounds.ENTITY_VILLAGER_DANDORI_PLUCK, 0.2f, this.random.nextFloat() * 0.4f + 0.3f);
        }
        this.dataTracker.set(DANDORI_STATE, pDandoriState);
    }

    public int getPawnType()
    {
        return this.dataTracker.get(PAWN_TYPE);
    }
    public void setPawnType(int pPawnType)
    {
        pawnType = pPawnType;
        this.dataTracker.set(PAWN_TYPE, pawnType);
        if (pPawnType > 2)
        {
            this.safeRange = 3;
            this.panicRange = 16;
        }
//        if (pPawnType == PAWN_TYPES.PIK_BLUE.ordinal())
//        {
//            bsPredicate = blockState -> blockState != null
//                    && (blockState.isOf(Blocks.DIRT)
//                    || blockState.isOf(Blocks.ICE));
//        }
        else if (pPawnType == PAWN_TYPES.PIK_PINK.ordinal())
        {
            bsPredicate = blockState -> blockState != null
                    && (blockState.isOf(Blocks.DIRT)
                    || blockState.isOf(Blocks.MAGMA_BLOCK)
                    || blockState.isOf(Blocks.FIRE));
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
        this.dataTracker.set(OWNER_TYPE, pOwnerType);
    }
    public int getOwnerType()
    {
        return this.dataTracker.get(OWNER_TYPE);
    }

    public void setThrown(boolean pThrown)
    {
        this.dataTracker.set(THROWN, pThrown);
    }
    public boolean getThrown()
    {
        return this.dataTracker.get(THROWN);
    }

    private float getAttackDamage() {
        float multiplier = 1.0f;
        if (getPawnType() == PAWN_TYPES.PIK_PINK.ordinal()) multiplier = 2.0f;
        return (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) * multiplier;
    }
    public Random getRandom()
    {
        return super.getRandom();
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
        return bsPredicate.test(getWorld().getBlockState(bp));
    }

    @Override
    protected void initGoals()
    {
        this.goalSelector.add(0, new EntityPawn.SwimmingGoal(this));
        this.goalSelector.add(1, new EntityPawn.LookAtOwnerGoal(this));
        this.goalSelector.add(2, new EntityPawn.FaceTowardTargetGoal(this));
        this.goalSelector.add(3, new EntityPawn.RandomLookGoal(this));
        this.goalSelector.add(5, new EntityPawn.MoveGoal(this));
        this.targetSelector
                .add(2, new ActiveTargetGoal<>(this, MobEntity.class, 5, false, false, attackTarget()));
    }

    private Predicate<LivingEntity> attackTarget()
    {
        return entity ->
        {
            if (entity instanceof CreeperEntity) return false;
            if (entity instanceof Monster)
            {
                if (this.getOwner() != null)
                    return this.getOwner().squaredDistanceTo(entity) < MathHelper.square(panicRange);
                return true;
            }
            return false;
        };
    }

    @Override
    public void tickMovement()
    {
        super.tickMovement();
    }

    @Override
    public void tick()
    {
        super.tick();
        if (this.isOnGround() && !this.onGroundLastTick)
        {
            if (this.getThrown()) this.setThrown(false);
            int i = 1;
            for (int j = 0; j < i * 8; ++j)
            {
                float f = this.random.nextFloat() * ((float) Math.PI * 2);
                float g = this.random.nextFloat() * 0.5f + 0.5f;
                float h = MathHelper.sin(f) * (float) i * 0.5f * g;
                float k = MathHelper.cos(f) * (float) i * 0.5f * g;
                this.getWorld().addParticle(this.getParticles(), this.getX() + (double) h, this.getY(), this.getZ() + (double) k, 0.0, 0.0, 0.0);
            }
            this.playSound(this.getSquishSound(), 1, ((this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f) / 0.8f);
        }
        this.onGroundLastTick = this.isOnGround();
        if (!getWorld().isClient)
        {
            if (this.getOwnerType() != 0 && this.getOwnerType() != 2)
            {
                if (this.getOwner() == null)
                {
                    if (timeWithoutParent++ % 5 == 0)
                    {
                        TargetPredicate tp = TargetPredicate.createNonAttackable().setBaseMaxDistance(ownerSearchRange * 2);
                        LivingEntity newParent = null;
                        if (this.getOwnerType() == 1)
                        {
                            newParent = getWorld().getClosestEntity(EntityGolemFirstDiorite.class, tp, this, getX(), getY(), getZ(), getBoundingBox().expand(ownerSearchRange * 2));
                        }
                        else if (this.getOwnerType() == 3)
                        {
                            newParent = getWorld().getClosestEntity(EntityVillagerDandori.class, tp, this, getX(), getY(), getZ(), getBoundingBox().expand(ownerSearchRange * 2));
                        }
                        if (newParent != null) this.setOwner(newParent);
                    }
                }
                else
                {
                    timeWithoutParent = 0;
                    if (this.getOwnerType() == 1)
                    {
                        if (this.getTarget() == null && ((EntityGolemFirstDiorite)this.getOwner()).getTarget() != null)
                            this.setTarget(((EntityGolemFirstDiorite)this.getOwner()).getTarget());
                    }
                }
                if (this.getTarget() == null) timeWithoutTarget++;
                else timeWithoutTarget = 0;

                if (this.getOwnerType() == OWNER_TYPES.FIRST_OF_DIORITE.ordinal())
                {
                    if (timeWithoutParent > timeWithoutParentMax || timeWithoutTarget > timeWithoutTargetMax)
                    {
                        if (this.age % 20 == 0) this.damage(this.getDamageSources().starve(), 1);
                    }
                }
            }
            // Drop a block target if we've been ordered to do other things.
            if ((this.getTarget() != null || this.getDandoriState()) && this.blockTarget != null)
            {
                getWorld().setBlockBreakingInfo(getId(), this.blockTarget, -1);
                this.blockTarget = null;
                this.blockBreakProgress = 0;
            }
            // If we have a block target, damage / destroy it if we get close enough.
            else if (this.blockTarget != null)
            {
                if (squaredDistanceTo(this.blockTarget.toCenterPos()) < MathHelper.square(1.25))
                {
                    Vec3d newVelocity = this.getPos().subtract(this.blockTarget.toCenterPos()).normalize().multiply(0.25);
                    this.setVelocity(new Vec3d(newVelocity.x, 0.25, newVelocity.z));
                    this.blockBreakProgress += 16;
                    if (this.getPawnType() == PAWN_TYPES.PIK_YELLOW.ordinal())
                        this.blockBreakProgress += 16;
                    BlockState bs = getWorld().getBlockState(this.blockTarget);
                    if (this.blockBreakProgress >= 100 || bs.isOf(Blocks.FIRE))
                    {
                        this.playSound(SoundEvents.BLOCK_ROOTED_DIRT_BREAK, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
                        bs.getBlock().onBreak(getWorld(), this.blockTarget, bs, null);
                        getWorld().removeBlock(this.blockTarget, false);
                        getWorld().syncWorldEvent(WorldEvents.BLOCK_BROKEN, this.blockTarget, Block.getRawIdFromState(getWorld().getBlockState(this.blockTarget)));
                        findNewTargetBlock();
                        this.blockBreakProgress = 0;
                    } else
                    {
                        this.playSound(SoundEvents.BLOCK_ROOTED_DIRT_HIT, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
                        getWorld().setBlockBreakingInfo(getId(), this.blockTarget, this.blockBreakProgress);
                    }
                }
            }
            // If we're not in dandori and the player owner is near, and we're not doing anything anyways, just dandori.
            if (this.getOwnerType() == OWNER_TYPES.PLAYER.ordinal() && this.getOwner() != null && !this.getDandoriState())
            {
                if (this.getTarget() == null && this.blockTarget == null && this.squaredDistanceTo(this.getOwner()) < MathHelper.square(safeRange + 1) && noDandoriTimer == 0)
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
    public void pushAwayFrom(Entity entity) {
        if (entity == this.getOwner()) return;
        super.pushAwayFrom(entity);
        if (this.getTarget() == entity && !this.isAiDisabled() && (this.getVelocity().lengthSquared() > 0 || entity.getPassengerList().contains(this)))
        {
            this.damage((LivingEntity) entity);
            if (getThrown())
            {
                setThrown(false);
                this.startRiding(entity);
            }
        }
    }

    @Override
    public boolean doesRenderOnFire()
    {
        if (this.getPawnType() == PAWN_TYPES.PIK_PINK.ordinal()) return false;
        return super.doesRenderOnFire();
    }

    public void onStruckByLightning(ServerWorld world, LightningEntity lightning)
    {
        if (this.getPawnType() == PAWN_TYPES.PIK_YELLOW.ordinal())
        {
            lightning.setCosmetic(true);
            return;
        }
        super.onStruckByLightning(world, lightning);
    }

    @Override
    public boolean damage(DamageSource source, float amount)
    {
        if (source.isIn(DamageTypeTags.IS_FALL)) return false;
        if (source.isIn(DamageTypeTags.IS_FIRE) && this.getPawnType() == PAWN_TYPES.PIK_PINK.ordinal()) return false;
        if (source.isIn(DamageTypeTags.IS_LIGHTNING) && this.getPawnType() == PAWN_TYPES.PIK_YELLOW.ordinal()) return false;
        return super.damage(source, amount);
    }

    protected void damage(LivingEntity target) {
        if (this.isAlive())
        {
            if (this.squaredDistanceTo(target) < 4 && this.canSee(target) && target.damage(this.getDamageSources().mobAttack(this), this.getAttackDamage())) {
                this.playSound(SoundEvents.BLOCK_BONE_BLOCK_PLACE, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
                this.applyDamageEffects(this, target);
            }
        }
    }

    @Override
    public int getMaxLookPitchChange() {
        return 0;
    }

    @Override
    protected void jump() {
        Vec3d vec3d = this.getVelocity();
        this.setVelocity(vec3d.x, this.getJumpVelocity(), vec3d.z);
        this.velocityDirty = true;
    }

    @Override
    public void remove(RemovalReason reason)
    {
        if (this.getOwnerType() == OWNER_TYPES.PLAYER.ordinal() && this.getDandoriState() && this.getOwner() != null)
        {
            ((IEntityWithDandoriCount) this.getOwner()).setRecountDandori();
        }
        super.remove(reason);
    }

    protected int getTicksUntilNextJump() {
        return this.random.nextInt(20) + 10;
    }

    protected ParticleEffect getParticles() {
        return ParticleTypes.WHITE_ASH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return null;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }

    protected SoundEvent getSquishSound() {
        return SoundEvents.BLOCK_BONE_BLOCK_HIT;
    }

    protected SoundEvent getJumpSound()
    {
        return SoundEvents.BLOCK_BONE_BLOCK_PLACE;
    }

    protected float getJumpSoundPitch() {
        float f = 0.8f;
        return ((this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f) * f;
    }

    private void lookAtPos(Vec3d position, float maxYawChange, float maxPitchChange)
    {
        double f = position.getY() - this.getEyeY();
        double d = position.getX() - this.getX();
        double e = position.getZ() - this.getZ();
        double g = Math.sqrt(d * d + e * e);
        float h = (float)(MathHelper.atan2(e, d) * 57.2957763671875) - 90.0f;
        float i = (float)(-(MathHelper.atan2(f, g) * 57.2957763671875));
        this.setPitch(ExtraMath.changeAngle(this.getPitch(), i, maxPitchChange));
        this.setYaw(ExtraMath.changeAngle(this.getYaw(), h, maxYawChange));
    }

    @Override
    public void handleStatus(byte status)
    {
        switch(status)
        {
            case IEntityDandoriFollower.ENTITY_EVENT_DANDORI_START:
                addDandoriParticles();
                break;
            default:
                super.handleStatus(status);
                break;
        }
    }

    private void addDandoriParticles()
    {
        getWorld().addParticle(ParticleTypes.NOTE,
                getX(), getY() + getHeight() * 1.5, getZ(),
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

    public Identifier getModelLocation()
    {
        int pawnType = this.getPawnType();
        return switch (pawnType)
        {
            case 0 -> new Identifier(GolemFirstStoneMod.MOD_ID, "geo/diorite_action.geo.json");
            case 1 -> new Identifier(GolemFirstStoneMod.MOD_ID, "geo/diorite_foresight.geo.json");
            case 2 -> new Identifier(GolemFirstStoneMod.MOD_ID, "geo/diorite_knowledge.geo.json");
            default -> new Identifier(GolemFirstStoneMod.MOD_ID, "geo/pawn_pik.geo.json");
        };
    }

    public Identifier getTextureLocation()
    {
        int pawnType = this.getPawnType();
        if (this.getOwner() == null && pawnType < 3)
        {
            return switch (pawnType)
            {
                case 0 ->
                        new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/pawn/diorite/diorite_action.png");
                case 1 ->
                        new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/pawn/diorite/diorite_foresight.png");
                default ->
                        new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/pawn/diorite/diorite_knowledge.png");
            };
        }
        else
        {
            return switch (pawnType)
            {
                case 0 ->
                        new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/pawn/diorite/diorite_action_active.png");
                case 1 ->
                        new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/pawn/diorite/diorite_foresight_active.png");
                case 2 ->
                        new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/pawn/diorite/diorite_knowledge_active.png");
                case 3 ->
                        new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/pawn/pik/pawn_pik_yellow.png");
                case 4 -> new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/pawn/pik/pawn_pik_pink.png");
                default ->
                        new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/pawn/pik/pawn_pik_blue.png");
            };
        }
    }


    // =================================================================================================================
    // Custom goals

    static class SlimeMoveControl
            extends MoveControl
    {
        private float targetYaw;
        private int ticksUntilJump;
        private final EntityPawn pawn;
        private boolean jumpOften;

        public SlimeMoveControl(EntityPawn pawn) {
            super(pawn);
            this.pawn = pawn;
            this.targetYaw = 180.0f * pawn.getYaw() / (float)Math.PI;
        }

        public void look(float targetYaw, boolean jumpOften) {
            this.targetYaw = targetYaw;
            this.jumpOften = jumpOften;
        }

        public void move(double speed) {
            this.speed = speed;
            this.state = MoveControl.State.MOVE_TO;
        }

        @Override
        public void tick() {
            this.entity.setYaw(this.wrapDegrees(this.entity.getYaw(), this.targetYaw, 90.0f));
            this.entity.headYaw = this.entity.getYaw();
            this.entity.bodyYaw = this.entity.getYaw();
            if (this.state != MoveControl.State.MOVE_TO) {
                this.entity.setForwardSpeed(0.0f);
                return;
            }
            this.state = MoveControl.State.WAIT;
            if (this.entity.isOnGround()) {
                this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)));
                if (this.ticksUntilJump-- <= 0) {
                    this.ticksUntilJump = this.pawn.getTicksUntilNextJump();
                    if (this.jumpOften) {
                        this.ticksUntilJump /= 3;
                    }
                    this.pawn.getJumpControl().setActive();
                    this.pawn.playSound(this.pawn.getJumpSound(), 1.0f, this.pawn.getJumpSoundPitch());
                } else {
                    this.pawn.sidewaysSpeed = 0.0f;
                    this.pawn.forwardSpeed = 0.0f;
                    this.entity.setMovementSpeed(0.0f);
                }
            } else {
                this.entity.setMovementSpeed((float)(this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)));
            }
        }
    }
    static class FaceTowardTargetGoal
            extends Goal
    {
        private final EntityPawn pawn;
        private int ticksLeft;
        Vec3d targetPos;
        Vec3d previousTargetPos;

        public FaceTowardTargetGoal(EntityPawn pawn) {
            this.pawn = pawn;
            this.setControls(EnumSet.of(Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            LivingEntity owner = this.pawn.getOwner();
            if (owner != null)
            {
                if (owner.squaredDistanceTo(this.pawn) > MathHelper.square(this.pawn.panicRange)) return false;
            }
            else
            {
                return false;
            }
            LivingEntity target = this.pawn.getTarget();
            if (target == null && this.pawn.blockTarget == null) {
                return false;
            }
            if (target != null)
            {
                if (!this.pawn.canTarget(target)) return false;
            }
            if (this.pawn.blockTarget != null)
            {
                BlockState bs = this.pawn.getWorld().getBlockState(this.pawn.blockTarget);
                if (!this.pawn.bsPredicate.test(bs))
                {
                    this.pawn.findNewTargetBlock();
                    return false;
                }
            }
            return this.pawn.getMoveControl() instanceof EntityPawn.SlimeMoveControl;
        }

        @Override
        public void start() {
            this.ticksLeft = EntityPawn.FaceTowardTargetGoal.toGoalTicks(150);
            targetPos = null;
            previousTargetPos = null;
            super.start();
        }

        @Override
        public boolean shouldContinue() {
            LivingEntity target = this.pawn.getTarget();
            if (target == null && this.pawn.blockTarget == null) {
                return false;
            }
            if (target != null)
            {
                if (!this.pawn.canTarget(target)) return false;
            }
            if (this.pawn.blockTarget != null)
            {
                BlockState bs = this.pawn.getWorld().getBlockState(this.pawn.blockTarget);
                if (!this.pawn.bsPredicate.test(bs))
                {
                    this.pawn.findNewTargetBlock();
                    return false;
                }
            }
            return --this.ticksLeft > 0;
        }

        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            MoveControl moveControl;
            LivingEntity target = this.pawn.getTarget();
            if (target != null) {
                if (this.pawn.canSee(target))
                {
                    targetPos = target.getPos();
                }
                else
                {
                    if (previousTargetPos == null || !previousTargetPos.equals(target.getPos()))
                    {
                        Path path = this.pawn.getNavigation().findPathTo(target, 1);
                        if (path != null && path.getLength() > 1)
                        {
                            targetPos = path.getNodePosition(this.pawn, 1);
                        }
                    }
                }
                previousTargetPos = target.getPos();
            }
            else if (this.pawn.blockTarget != null)
            {
                Path path = this.pawn.getNavigation().findPathTo(this.pawn.blockTarget, 1);
                if (path != null && path.getLength() > 1)
                {
                    targetPos = path.getNodePosition(this.pawn, 1);
                }
            }
            if (targetPos != null) this.pawn.lookAtPos(targetPos, 10,10);
            if ((moveControl = this.pawn.getMoveControl()) instanceof EntityPawn.SlimeMoveControl) {
                EntityPawn.SlimeMoveControl slimeMoveControl = (EntityPawn.SlimeMoveControl)moveControl;
                slimeMoveControl.look(this.pawn.getYaw(), !this.pawn.isAiDisabled());
            }
        }
    }

    static class LookAtOwnerGoal
    extends Goal {
        private final EntityPawn pawn;
        private int ticksLeft;
        Vec3d targetPos;
        Vec3d previousOwnerPos;

        public LookAtOwnerGoal(EntityPawn pawn)
        {
            this.pawn = pawn;
            this.setControls(EnumSet.of(Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            LivingEntity owner = this.pawn.getOwner();
            if (owner == null)
                return false;
            else
            {
                if (this.pawn.getOwnerType() != OWNER_TYPES.PLAYER.ordinal())
                {
                    double sqrDistanceToOwner = owner.squaredDistanceTo(this.pawn);
                    if (sqrDistanceToOwner > MathHelper.square(this.pawn.panicRange)
                            || (sqrDistanceToOwner > MathHelper.square(this.pawn.safeRange) && this.pawn.getTarget() == null))
                    {
                        return (this.pawn.isOnGround() || this.pawn.hasStatusEffect(StatusEffects.LEVITATION)) && this.pawn.getMoveControl() instanceof EntityPawn.SlimeMoveControl;
                    }
                }
                else
                {
                    return this.pawn.getDandoriState();
                }
            }
            return false;
        }
        @Override
        public void start() {
            this.ticksLeft = EntityPawn.LookAtOwnerGoal.toGoalTicks(20);
            targetPos = null;
            previousOwnerPos = null;
            super.start();
        }

        @Override
        public boolean shouldContinue() {
            LivingEntity livingEntity = this.pawn.getOwner();
            if (livingEntity == null) {
                return false;
            }
            if (livingEntity.squaredDistanceTo(this.pawn) < MathHelper.square(this.pawn.panicRange))
                return false;
            return --this.ticksLeft > 0;
        }

        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            MoveControl moveControl;
            LivingEntity owner = this.pawn.getOwner();
            if (owner != null) {
                if (this.pawn.canSee(owner))
                {
                    targetPos = owner.getPos();
                }
                else
                {
                    if (previousOwnerPos == null || !previousOwnerPos.equals(owner.getPos()))
                    {
                        Path path = this.pawn.getNavigation().findPathTo(owner, 1);
                        if (path != null && path.getLength() > 1)
                        {
                            targetPos = path.getNodePosition(this.pawn, 1);
                        }
                    }
                }
                previousOwnerPos = owner.getPos();
            }
            if (targetPos != null) this.pawn.lookAtPos(targetPos, 10,10);
            if ((moveControl = this.pawn.getMoveControl()) instanceof EntityPawn.SlimeMoveControl) {
                EntityPawn.SlimeMoveControl slimeMoveControl = (EntityPawn.SlimeMoveControl)moveControl;
                slimeMoveControl.look(this.pawn.getYaw(), !this.pawn.isAiDisabled());
            }
        }
    }

    static class RandomLookGoal
            extends Goal {
        private final EntityPawn pawn;
        private float targetYaw;
        private int timer;

        public RandomLookGoal(EntityPawn pawn) {
            this.pawn = pawn;
            this.setControls(EnumSet.of(Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            LivingEntity owner = this.pawn.getOwner();
            if (owner != null)
            {
//                if (!this.pawn.getDandoriState() && this.pawn.getTarget() == null) return false;
                if (owner.squaredDistanceTo(this.pawn) > MathHelper.square(this.pawn.panicRange)) return false;
                return this.pawn.getTarget() == null && (this.pawn.isOnGround() || this.pawn.hasStatusEffect(StatusEffects.LEVITATION)) && this.pawn.getMoveControl() instanceof EntityPawn.SlimeMoveControl;
            }
            else return false;
        }

        @Override
        public void tick() {
            MoveControl moveControl;
            if (--this.timer <= 0) {
                this.timer = this.getTickCount(40 + this.pawn.getRandom().nextInt(60));
                this.targetYaw = this.pawn.getRandom().nextInt(360);
            }
            if ((moveControl = this.pawn.getMoveControl()) instanceof EntityPawn.SlimeMoveControl) {
                EntityPawn.SlimeMoveControl slimeMoveControl = (EntityPawn.SlimeMoveControl)moveControl;
                slimeMoveControl.look(this.targetYaw, false);
            }
        }
    }

    static class SwimmingGoal
            extends Goal {
        private final EntityPawn pawn;

        public SwimmingGoal(EntityPawn pawn) {
            this.pawn = pawn;
            this.setControls(EnumSet.of(Goal.Control.JUMP, Goal.Control.MOVE));
            pawn.getNavigation().setCanSwim(true);
        }

        @Override
        public boolean canStart() {
            if (this.pawn.getPawnType() != PAWN_TYPES.PIK_BLUE.ordinal()) return false;
            if (this.pawn.getDandoriState() && this.pawn.getOwner() != null && this.pawn.getOwner().getY() < this.pawn.getY() && !this.pawn.isOnGround()) return false;
            if (!this.pawn.getDandoriState() && this.pawn.getTarget() != null && this.pawn.getTarget().getY() < this.pawn.getY() && !this.pawn.isOnGround()) return false;
            return (this.pawn.isTouchingWater() || this.pawn.isInLava()) && this.pawn.getMoveControl() instanceof EntityPawn.SlimeMoveControl;
        }

        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            MoveControl moveControl;
            if (this.pawn.getRandom().nextFloat() < 0.8f) {
                this.pawn.getJumpControl().setActive();
            }
            if ((moveControl = this.pawn.getMoveControl()) instanceof EntityPawn.SlimeMoveControl) {
                EntityPawn.SlimeMoveControl slimeMoveControl = (EntityPawn.SlimeMoveControl)moveControl;
                float speed = 2.0f;
                if (!this.pawn.getDandoriState() && this.pawn.getTarget() == null && this.pawn.blockTarget == null) speed = 0.0f;
                slimeMoveControl.move(speed);
            }
        }
    }

    static class MoveGoal
            extends Goal {
        private final EntityPawn pawn;

        public MoveGoal(EntityPawn pawn) {
            this.pawn = pawn;
            this.setControls(EnumSet.of(Goal.Control.JUMP, Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if (this.pawn.hasVehicle()) return false;
            if (this.pawn.getOwner() == null) return false;
            if (this.pawn.squaredDistanceTo(this.pawn.getOwner()) < MathHelper.square(this.pawn.safeRange))
            {
                if (this.pawn.getDandoriState() || (this.pawn.getTarget() == null && this.pawn.blockTarget == null))
                    return false;
            }
            return !this.pawn.hasVehicle();
        }

        @Override
        public void tick() {
            MoveControl moveControl = this.pawn.getMoveControl();
            if (moveControl instanceof SlimeMoveControl slimeMoveControl) {
                if (this.pawn.getOwnerType() == OWNER_TYPES.PLAYER.ordinal())
                {
                    float speed = 1.75f;
                    if (!this.pawn.getDandoriState() && this.pawn.getTarget() == null && this.pawn.blockTarget == null) speed = 0.0f;
                    slimeMoveControl.move(speed);
                }
                else
                    slimeMoveControl.move(1.0);
            }
        }
    }
}
