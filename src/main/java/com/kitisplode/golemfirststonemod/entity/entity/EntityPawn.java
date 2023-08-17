package com.kitisplode.golemfirststonemod.entity.entity;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemFirstDiorite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;

import java.util.EnumSet;
import java.util.function.Predicate;

public class EntityPawn extends IronGolemEntity implements GeoEntity
{
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    protected static final TrackedData<Integer> OWNER_TYPE = DataTracker.registerData(EntityPawn.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> PAWN_TYPE = DataTracker.registerData(EntityPawn.class, TrackedDataHandlerRegistry.INTEGER);
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
        return air;
    }

    @Override
    protected void initDataTracker()
    {
        super.initDataTracker();
        this.dataTracker.startTracking(PAWN_TYPE, pawnType);
        this.dataTracker.startTracking(OWNER_TYPE, 0);
    }

    public int getPawnType()
    {
        return this.dataTracker.get(PAWN_TYPE);
    }
    private void setPawnType(int pPawnType)
    {
        pawnType = pPawnType;
        this.dataTracker.set(PAWN_TYPE, pawnType);
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

    public void setOwnerType(int pOwnerType)
    {
        this.dataTracker.set(OWNER_TYPE, pOwnerType);
    }
    public int getOwnerType()
    {
        return this.dataTracker.get(OWNER_TYPE);
    }

    private float getAttackDamage() {
        return (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
    }

    @Override
    protected void initGoals()
    {
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
            if (entity instanceof Monster)
            {
                if (this.getOwner() != null)
                {
                    return this.getOwner().squaredDistanceTo(entity) < MathHelper.square(panicRange);
                }
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
        this.onGroundLastTick = this.isOnGround();
        if (this.isOnGround() && !this.onGroundLastTick)
        {
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
        if (this.getOwnerType() != 0)
        {
            if (this.getOwner() == null)
            {
                if (timeWithoutParent++ % 20 == 0)
                {
                    TargetPredicate tp = TargetPredicate.createNonAttackable().setBaseMaxDistance(ownerSearchRange * 2);
                    LivingEntity newParent = null;
                    if (this.getOwnerType() == 1)
                    {
                        newParent = getWorld().getClosestEntity(EntityGolemFirstDiorite.class, tp, this, getX(), getY(), getZ(), getBoundingBox().expand(ownerSearchRange * 2));
                    }
                    else if (this.getOwnerType() == 2)
                    {
                        newParent = getWorld().getClosestPlayer(tp, this);
                    }
                    else if (this.getOwnerType() == 3)
                    {
                        newParent = getWorld().getClosestEntity(EntityVillagerDandori.class, tp, this, getX(), getY(), getZ(), getBoundingBox().expand(ownerSearchRange * 2));
                    }
                    if (newParent != null) this.setOwner(newParent);
                }
            }
            else timeWithoutParent = 0;
            if (this.getTarget() == null) timeWithoutTarget++;
            else timeWithoutTarget = 0;

            if (this.getOwnerType() != 3)
            {
                if (timeWithoutParent > timeWithoutParentMax || timeWithoutTarget > timeWithoutTargetMax)
                {
                    if (this.age % 20 == 0) this.damage(this.getDamageSources().starve(), 1);
                }
            }
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("wasOnGround", this.onGroundLastTick);
        nbt.putInt("pawnType", this.pawnType);
        nbt.putInt("ownerType", this.getOwnerType());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.onGroundLastTick = nbt.getBoolean("wasOnGround");
        if (nbt.contains("pawnType"))
            setPawnType(nbt.getInt("pawnType"));
        if (nbt.contains("ownerType"))
            setOwnerType(nbt.getInt("ownerType"));
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
    public void pushAwayFrom(Entity entity) {
        super.pushAwayFrom(entity);
        if (entity instanceof Monster && !this.isAiDisabled()) {
            this.damage((LivingEntity) entity);
        }
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
        if (this.getOwner() == null)
            return switch (pawnType)
            {
                case 0 -> new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/diorite_action.png");
                case 1 -> new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/diorite_foresight.png");
                case 2 -> new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/diorite_knowledge.png");
                case 3 -> new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/pik/pawn_pik_yellow.png");
                case 4 -> new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/pik/pawn_pik_pink.png");
                default -> new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/pik/pawn_pik_blue.png");
            };
        else
            return switch (pawnType)
            {
                case 0 -> new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/diorite_action_active.png");
                case 1 -> new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/diorite_foresight_active.png");
                case 2 -> new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/diorite_knowledge_active.png");
                case 3 -> new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/pik/pawn_pik_yellow.png");
                case 4 -> new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/pik/pawn_pik_pink.png");
                default -> new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/pik/pawn_pik_blue.png");
            };
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

        public FaceTowardTargetGoal(EntityPawn pawn) {
            this.pawn = pawn;
            this.setControls(EnumSet.of(Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            LivingEntity livingEntity = this.pawn.getTarget();
            if (livingEntity == null) {
                return false;
            }
            if (!this.pawn.canTarget(livingEntity)) {
                return false;
            }
            return this.pawn.getMoveControl() instanceof EntityPawn.SlimeMoveControl;
        }

        @Override
        public void start() {
            this.ticksLeft = EntityPawn.FaceTowardTargetGoal.toGoalTicks(150);
            super.start();
        }

        @Override
        public boolean shouldContinue() {
            LivingEntity livingEntity = this.pawn.getTarget();
            if (livingEntity == null) {
                return false;
            }
            if (!this.pawn.canTarget(livingEntity)) {
                return false;
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
            LivingEntity livingEntity = this.pawn.getTarget();
            if (livingEntity != null) {
                this.pawn.lookAtEntity(livingEntity, 10.0f, 10.0f);
            }
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

        public LookAtOwnerGoal(EntityPawn pawn)
        {
            this.pawn = pawn;
            this.setControls(EnumSet.of(Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            if (this.pawn.getOwner() == null)
                return false;
            else
            {
                if (this.pawn.getOwner().squaredDistanceTo(this.pawn) > MathHelper.square(this.pawn.panicRange)
                        || (this.pawn.getOwner().squaredDistanceTo(this.pawn) > MathHelper.square(this.pawn.safeRange) && this.pawn.getTarget() == null))
                {
                    return (this.pawn.isOnGround() || this.pawn.hasStatusEffect(StatusEffects.LEVITATION)) && this.pawn.getMoveControl() instanceof EntityPawn.SlimeMoveControl;
                }
            }
            return false;
        }
        @Override
        public void start() {
            this.ticksLeft = EntityPawn.LookAtOwnerGoal.toGoalTicks(200);
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
            LivingEntity livingEntity = this.pawn.getOwner();
            if (livingEntity != null) {
                this.pawn.lookAtEntity(livingEntity, 10.0f, 10.0f);
            }
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
            if (this.pawn.getOwner() != null)
            {
                if (this.pawn.getOwner().squaredDistanceTo(this.pawn) > MathHelper.square(this.pawn.panicRange))
                {
                    return false;
                }
            }
            return this.pawn.getTarget() == null && (this.pawn.isOnGround() || this.pawn.hasStatusEffect(StatusEffects.LEVITATION)) && this.pawn.getMoveControl() instanceof EntityPawn.SlimeMoveControl;
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

    static class MoveGoal
            extends Goal {
        private final EntityPawn pawn;

        public MoveGoal(EntityPawn pawn) {
            this.pawn = pawn;
            this.setControls(EnumSet.of(Goal.Control.JUMP, Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return !this.pawn.hasVehicle();
        }

        @Override
        public void tick() {
            MoveControl moveControl = this.pawn.getMoveControl();
            if (moveControl instanceof SlimeMoveControl slimeMoveControl) {
                slimeMoveControl.move(1.0);
            }
        }
    }
}
