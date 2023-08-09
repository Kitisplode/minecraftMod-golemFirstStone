package com.kitisplode.golemfirststonemod.entity.entity.golem.pawn;

import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemFirstDiorite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;

import java.util.EnumSet;

public class EntityPawnFirstDiorite extends IronGolemEntity implements GeoEntity
{
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final TrackedData<Integer> PAWN_TYPE = DataTracker.registerData(EntityGolemFirstDiorite.class, TrackedDataHandlerRegistry.INTEGER);
    private int pawnType = 0;
    private boolean onGroundLastTick;

    public EntityPawnFirstDiorite(EntityType<? extends IronGolemEntity> pEntityType, World pLevel)
    {
        super(pEntityType, pLevel);
        // Pick the pawn type randomly.
        setPawnType(pLevel.getRandom().nextBetween(0,2));
        this.moveControl = new EntityPawnFirstDiorite.SlimeMoveControl(this);
    }

    public static DefaultAttributeContainer.Builder setAttributes()
    {
        return GolemEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.5f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0f)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48);
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

    private float getAttackDamage() {
        return (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
    }

    @Override
    protected void initGoals()
    {
        this.goalSelector.add(1, new EntityPawnFirstDiorite.FaceTowardTargetGoal(this));
        this.goalSelector.add(3, new EntityPawnFirstDiorite.RandomLookGoal(this));
        this.goalSelector.add(5, new EntityPawnFirstDiorite.MoveGoal(this));
        this.targetSelector
                .add(1, new ActiveTargetGoal<>(this, MobEntity.class, 5, false, false, entity -> entity instanceof Monster));
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
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("wasOnGround", this.onGroundLastTick);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.onGroundLastTick = nbt.getBoolean("wasOnGround");
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
        return SoundEvents.BLOCK_BONE_BLOCK_PLACE;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BLOCK_BONE_BLOCK_HIT;
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


    // =================================================================================================================
    // Custom goals

    static class SlimeMoveControl
            extends MoveControl
    {
        private float targetYaw;
        private int ticksUntilJump;
        private final EntityPawnFirstDiorite pawn;
        private boolean jumpOften;

        public SlimeMoveControl(EntityPawnFirstDiorite pawn) {
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
        private final EntityPawnFirstDiorite pawn;
        private int ticksLeft;

        public FaceTowardTargetGoal(EntityPawnFirstDiorite pawn) {
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
            return this.pawn.getMoveControl() instanceof EntityPawnFirstDiorite.SlimeMoveControl;
        }

        @Override
        public void start() {
            this.ticksLeft = EntityPawnFirstDiorite.FaceTowardTargetGoal.toGoalTicks(300);
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
            if ((moveControl = this.pawn.getMoveControl()) instanceof EntityPawnFirstDiorite.SlimeMoveControl) {
                EntityPawnFirstDiorite.SlimeMoveControl slimeMoveControl = (EntityPawnFirstDiorite.SlimeMoveControl)moveControl;
                slimeMoveControl.look(this.pawn.getYaw(), !this.pawn.isAiDisabled());
            }
        }
    }

    static class RandomLookGoal
            extends Goal {
        private final EntityPawnFirstDiorite pawn;
        private float targetYaw;
        private int timer;

        public RandomLookGoal(EntityPawnFirstDiorite pawn) {
            this.pawn = pawn;
            this.setControls(EnumSet.of(Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            return this.pawn.getTarget() == null && (this.pawn.isOnGround() || this.pawn.hasStatusEffect(StatusEffects.LEVITATION)) && this.pawn.getMoveControl() instanceof EntityPawnFirstDiorite.SlimeMoveControl;
        }

        @Override
        public void tick() {
            MoveControl moveControl;
            if (--this.timer <= 0) {
                this.timer = this.getTickCount(40 + this.pawn.getRandom().nextInt(60));
                this.targetYaw = this.pawn.getRandom().nextInt(360);
            }
            if ((moveControl = this.pawn.getMoveControl()) instanceof EntityPawnFirstDiorite.SlimeMoveControl) {
                EntityPawnFirstDiorite.SlimeMoveControl slimeMoveControl = (EntityPawnFirstDiorite.SlimeMoveControl)moveControl;
                slimeMoveControl.look(this.targetYaw, false);
            }
        }
    }

    static class MoveGoal
            extends Goal {
        private final EntityPawnFirstDiorite pawn;

        public MoveGoal(EntityPawnFirstDiorite pawn) {
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
