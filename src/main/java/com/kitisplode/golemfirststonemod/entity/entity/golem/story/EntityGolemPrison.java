package com.kitisplode.golemfirststonemod.entity.entity.golem.story;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemCobble;
import com.kitisplode.golemfirststonemod.sound.ModSounds;
import com.kitisplode.golemfirststonemod.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class EntityGolemPrison extends PathfinderMob implements Enemy, GeoEntity
{
    public static final ResourceLocation MODEL = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "geo/entity/golem/story/golem_prison.geo.json");
    public static final ResourceLocation TEXTURE = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/story/golem_prison.png");
    public static final ResourceLocation TEXTURE_GLOWMASK = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/story/golem_prison_glowmask.png");
    public static final ResourceLocation TEXTURE_LIGHT_GLOWMASK = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/story/golem_prison_light_glowmask.png");
    public static final ResourceLocation ANIMATIONS = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "animations/entity/golem/story/golem_prison.animation.json");

    private static final RawAnimation ANIMATION_WALK = RawAnimation.begin().thenLoop("animation.golem_prison.walk");
    private static final RawAnimation ANIMATION_IDLE = RawAnimation.begin().thenLoop("animation.golem_prison.idle");
    private static final RawAnimation ANIMATION_WALK_DAMAGED = RawAnimation.begin().thenLoop("animation.golem_prison.walk_damaged");
    private static final RawAnimation ANIMATION_IDLE_DAMAGED = RawAnimation.begin().thenLoop("animation.golem_prison.idle_damaged");

    private static final EntityDataAccessor<Boolean> LIGHT_ON = SynchedEntityData.defineId(EntityGolemCobble.class, EntityDataSerializers.BOOLEAN);
    private BlockPos deployPosition;
    private ArrayList<BlockPos> previousDeployPositions = new ArrayList<>();

    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    public EntityGolemPrison(EntityType<? extends PathfinderMob> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 100)
                .add(Attributes.MOVEMENT_SPEED, 0.25f)
                .add(Attributes.ATTACK_DAMAGE, 15.0f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1f);
    }

    public static AttributeSupplier setAttributes()
    {
        return createAttributes().build();
    }

    @Override
    protected void registerGoals()
    {
        // move to and attack current target goal
        this.goalSelector.addGoal(2, new PrisonGolemFindNextDeployPositionGoal(this, 5, 10, ModTags.POIs.PATH_GOLEM_PRISON));
        this.goalSelector.addGoal(3, new PrisonGolemMoveToDeployPositionGoal(this, 0.75f, 1.0f));

        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        // target anything seen in the search light
    }

    public void setDeployPosition(BlockPos bp)
    {
        this.deployPosition = bp;
    }
    public BlockPos getDeployPosition()
    {
        return this.deployPosition;
    }
    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        if (!this.entityData.hasItem(LIGHT_ON)) this.entityData.define(LIGHT_ON, true);
    }
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        if (this.getDeployPosition() != null) pCompound.put("DeployPos", NbtUtils.writeBlockPos(this.getDeployPosition()));
    }
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("DeployPos")) this.setDeployPosition(NbtUtils.readBlockPos(pCompound.getCompound("DeployPos")));
    }
    public boolean getLightOn()
    {
        return this.entityData.get(LIGHT_ON);
    }
    public void setLightOn(boolean pBoolean)
    {
        this.entityData.set(LIGHT_ON, pBoolean);
    }

    public boolean canSpawnSprintParticle() {
        return this.getDeltaMovement().horizontalDistanceSqr() > (double)2.5000003E-7F && this.random.nextInt(5) == 0;
    }
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return ModSounds.ENTITY_GOLEM_PRISON_HURT.get();
    }
    protected SoundEvent getDeathSound() {
        return ModSounds.ENTITY_GOLEM_PRISON_DEATH.get();
    }
    protected void playStepSound(BlockPos pPos, BlockState pBlock) {
        this.playSound(ModSounds.ENTITY_GOLEM_PRISON_STEP.get(), 1.0F, this.getRandom().nextFloat() * 0.4F + 0.4F);
    }
    protected float nextStep() {
        return (float)((int)this.moveDist + 2);
    }

    public ResourceLocation getModelLocation()
    {
        return MODEL;
    }
    public ResourceLocation getTextureLocation()
    {
        return TEXTURE;
    }
    public ResourceLocation getAnimationsLocation()
    {
        return ANIMATIONS;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, event ->
        {
//            EntityGolemPrison pGolem = event.getAnimatable();
            event.getController().setAnimationSpeed(0.50);
            if (getDeltaMovement().horizontalDistanceSqr() > 0.001D || event.isMoving())
                return event.setAndContinue(ANIMATION_WALK);
            return event.setAndContinue(ANIMATION_IDLE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }

    //==================================================================================================================
    public class PrisonGolemMoveToDeployPositionGoal extends Goal
    {
        private final EntityGolemPrison golemPrison;
        private final double proximityDistance;
        private final double speed;
        private Path path;
        public PrisonGolemMoveToDeployPositionGoal(EntityGolemPrison mob, double proximityDistance, double speed)
        {
            this.golemPrison = mob;
            this.proximityDistance = proximityDistance;
            this.speed = speed;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }
        @Override
        public boolean canUse()
        {
            BlockPos bp = this.golemPrison.getDeployPosition();
            return bp != null;
        }
        public boolean canContinueToUse()
        {
            BlockPos bp = this.golemPrison.getDeployPosition();
            return bp != null && (bp.distToCenterSqr(this.golemPrison.position()) > Mth.square(this.proximityDistance));
        }
        public void start()
        {
            this.path = null;
        }
        @Override
        public void stop() {
            Vec3 pos = this.golemPrison.getDeployPosition().getCenter();
            this.golemPrison.setPos(pos.x(), this.golemPrison.getY(), pos.z());
            this.golemPrison.setDeployPosition(null);
            this.golemPrison.getNavigation().stop();
        }
        @Override
        public void tick() {
            BlockPos bp = this.golemPrison.getDeployPosition();
            if (bp != null) {
                if (bp.distToCenterSqr(this.golemPrison.position()) < Mth.square(this.proximityDistance + 1.5))
                {
                    Vec3 move = bp.getCenter().subtract(this.golemPrison.position()).multiply(1,0,1).normalize().scale(this.speed*0.1);
                    this.golemPrison.setDeltaMovement(move.x, this.golemPrison.getDeltaMovement().y(), move.z);
                }
                else if (this.path == null || !this.golemPrison.getNavigation().isInProgress())
                {
                    this.golemPrison.getNavigation().setMaxVisitedNodesMultiplier(2.0f);
                    this.path = this.golemPrison.getNavigation().createPath(bp, 1);
                    if (this.path != null) this.golemPrison.getNavigation().moveTo(this.path, this.speed);
                    this.golemPrison.getNavigation().resetMaxVisitedNodesMultiplier();
                }
            }
        }
    }

    public class PrisonGolemFindNextDeployPositionGoal extends Goal
    {
        private final int waitTimeMin;
        private final int waitTimeMax;
        private int waitTimer;
        private final EntityGolemPrison golemPrison;
        private final TagKey<PoiType> poiType;
        public PrisonGolemFindNextDeployPositionGoal(EntityGolemPrison mob, int waitTimeMin, int waitTimeMax, TagKey<PoiType> poiType)
        {
            this.golemPrison = mob;
            this.waitTimeMin = waitTimeMin;
            this.waitTimeMax = waitTimeMax;
            this.poiType = poiType;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }
        @Override
        public boolean canUse()
        {
            return this.golemPrison.getDeployPosition() == null;
        }
        @Override
        public boolean canContinueToUse()
        {
            return (--this.waitTimer >= 0);
        }
        @Override
        public void start()
        {
            this.waitTimer = this.golemPrison.getRandom().nextIntBetweenInclusive(this.waitTimeMin, this.waitTimeMax);
            this.golemPrison.setDeployPosition(this.findNewDeployPosition());
        }
        private BlockPos findNewDeployPosition()
        {
            if (this.golemPrison.previousDeployPositions.size() > 2) this.golemPrison.previousDeployPositions.remove(2);
            BlockPos bp = this.golemPrison.blockPosition();
            PoiManager poimanager = ((ServerLevel)this.golemPrison.level()).getPoiManager();
            List<BlockPos> list = poimanager.getInRange((poi) -> {
                return poi.is(this.poiType);
            }, bp, 32, PoiManager.Occupancy.ANY).map(PoiRecord::getPos).sorted(Comparator.comparingDouble((pos) -> {
                return pos.distSqr(bp);
            })).toList();
            if (list.size() == 2) return list.get(1);
            else if (list.size() == 1) return list.get(0);
            int i = 0;
            for (; i < list.size(); i++)
            {
                BlockPos pos = list.get(i);
                if (!this.golemPrison.previousDeployPositions.contains(pos))
                {
                    this.golemPrison.previousDeployPositions.add(0, pos);
                    break;
                }
            }
            return list.get(i);
        }
    }
}
