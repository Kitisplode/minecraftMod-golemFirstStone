package com.kitisplode.golemfirststonemod.entity.entity.golem.legends;

import com.kitisplode.golemfirststonemod.entity.entity.golem.AbstractGolemDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityCanAttackBlocks;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDelayedMeleeAttack;
import com.kitisplode.golemfirststonemod.entity.goal.action.*;
import com.kitisplode.golemfirststonemod.entity.goal.target.SharedTargetGoal;
import com.kitisplode.golemfirststonemod.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.function.Predicate;

public class EntityGolemCobble extends AbstractGolemDandoriFollower implements GeoEntity, IEntityWithDelayedMeleeAttack, IEntityDandoriFollower, IEntityCanAttackBlocks
{
    private static final TrackedData<Integer> ATTACK_STATE = DataTracker.registerData(EntityGolemCobble.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> LEFT_ARM = DataTracker.registerData(EntityGolemCobble.class, TrackedDataHandlerRegistry.BOOLEAN);
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private BlockPos blockTarget;
    private final Predicate<BlockState> bsPredicate = blockState -> blockState != null
            && ((blockState.isIn(BlockTags.PICKAXE_MINEABLE)
            || blockState.isIn(BlockTags.SHOVEL_MINEABLE))
            && !blockState.isIn(BlockTags.NEEDS_IRON_TOOL)
            && !blockState.isIn(BlockTags.NEEDS_DIAMOND_TOOL));
    private int blockBreakProgress = 0;

    public EntityGolemCobble(EntityType<? extends IronGolemEntity> pEntityType, World pLevel)
    {
        super(pEntityType, pLevel);
    }

    public static DefaultAttributeContainer.Builder setAttributes()
    {
        return GolemEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 75.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.5f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.75f);
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
        if (!this.dataTracker.containsKey(ATTACK_STATE))
            this.dataTracker.startTracking(ATTACK_STATE, 0);
        if (!this.dataTracker.containsKey(LEFT_ARM))
            this.dataTracker.startTracking(LEFT_ARM, false);
    }

    public int getAttackState()
    {
        return this.dataTracker.get(ATTACK_STATE);
    }
    public void setAttackState(int pInt)
    {
        this.dataTracker.set(ATTACK_STATE, pInt);
    }
    private boolean getLeftArm()
    {
        return this.dataTracker.get(LEFT_ARM);
    }
    private void setLeftArm(boolean pLeftArm)
    {
        this.dataTracker.set(LEFT_ARM, pLeftArm);
    }

    private float getAttackDamage() {
        return (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
    }

    public Random getRandom()
    {
        return super.getRandom();
    }

    @Override
    public double getEyeY()
    {
        return getY() + 0.5f;
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new DandoriFollowHardGoal(this, 1.2, dandoriMoveRange, dandoriSeeRange));

        this.goalSelector.add(2, new MultiStageAttackGoalRanged(this, 1.0, true, 6.0D, new int[]{10, 5}));
        this.goalSelector.add(2, new MultiStageAttackBlockGoalRanged(this, 1.0, true, 8.0D, new int[]{10, 5}));
        this.goalSelector.add(2, new DandoriMoveToDeployPositionGoal(this, 2.0f, 1.0f));

        this.goalSelector.add(3, new DandoriFollowSoftGoal(this, 1.2, dandoriMoveRange, dandoriSeeRange));

        this.goalSelector.add(3, new WanderNearTargetGoal(this, 0.8, 32.0F));
        this.goalSelector.add(5, new IronGolemWanderAroundGoal(this, 0.8));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(7, new LookAtEntityGoal(this, MerchantEntity.class, 8.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.targetSelector.add(2, new RevengeGoal(this, new Class[0]));
        this.targetSelector.add(3, new SharedTargetGoal<>(this, GolemEntity.class, MobEntity.class, 5, false, false, entity -> entity instanceof Monster && !(entity instanceof CreeperEntity), 5));
    }

    @Override
    public boolean tryAttack()
    {
        if (getAttackState() == 1) this.setLeftArm(!this.getLeftArm());
        if (getAttackState() != 2) return false;

        if (getTarget() != null)
        {
            this.playSound(SoundEvents.ENTITY_IRON_GOLEM_ATTACK, 1.0f, 1.0f);
            getTarget().damage(getDamageSources().mobAttack(this), getAttackDamage());
            getTarget().setVelocity(getTarget().getVelocity().multiply(0.35d));
            applyDamageEffects(this, getTarget());
        }
        return true;
    }
    @Override
    public boolean isPushable()
    {
        return getAttackState() == 0;
    }

    @Override
    public void tick()
    {
        super.tick();
        // Drop a block target if we've been ordered to do other things.
        if ((this.getTarget() != null || this.isDandoriOn()) && getBlockTarget() != null)
        {
            getWorld().setBlockBreakingInfo(getId(), this.blockTarget, -1);
            setBlockTarget(null);
            this.blockBreakProgress = 0;
        }
        if (!canTargetBlock(getBlockTarget())) setBlockTarget(null);
    }
    @Override
    public void setBlockTarget(BlockPos pBlockPos)
    {
        if (pBlockPos != null && pBlockPos.getY() <= this.getWorld().getBottomY()) return;
        blockTarget = pBlockPos;
    }
    @Override
    public BlockPos getBlockTarget()
    {
        return blockTarget;
    }
    @Override
    public boolean canTargetBlock(BlockPos pBlockPos)
    {
        if (pBlockPos == null) return false;
        return bsPredicate.test(getWorld().getBlockState(pBlockPos));
    }
    @Override
    public boolean tryAttackBlock()
    {
        if (getAttackState() == 1) this.setLeftArm(!this.getLeftArm());
        if (getAttackState() != 2) return false;
        if (!canTargetBlock(getBlockTarget())) return false;

        this.blockBreakProgress += 16;
        BlockState bs = getWorld().getBlockState(this.blockTarget);
        if (this.blockBreakProgress >= 100)
        {
            this.playSound(SoundEvents.BLOCK_ROOTED_DIRT_BREAK, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
            bs.getBlock().onBreak(getWorld(), this.blockTarget, bs, null);
            Block.dropStacks(bs, getWorld(), this.blockTarget);
            getWorld().removeBlock(this.blockTarget, false);
            getWorld().syncWorldEvent(WorldEvents.BLOCK_BROKEN, this.blockTarget, Block.getRawIdFromState(getWorld().getBlockState(this.blockTarget)));
            findNewTargetBlock();
            this.blockBreakProgress = 0;
        } else
        {
            this.playSound(SoundEvents.BLOCK_ROOTED_DIRT_HIT, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
            getWorld().setBlockBreakingInfo(getId(), this.blockTarget, this.blockBreakProgress);
        }

        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, event ->
        {
            EntityGolemCobble pGolem = event.getAnimatable();
            if (pGolem.getAttackState() > 0)
            {
                if (pGolem.getAttackState() == 1)
                {
                    event.getController().setAnimationSpeed(4.00);
                    if (!this.getLeftArm()) return event.setAndContinue(RawAnimation.begin().then("animation.golem_cobble.attack_windup_right", Animation.LoopType.HOLD_ON_LAST_FRAME));
                    return event.setAndContinue(RawAnimation.begin().then("animation.golem_cobble.attack_windup_left", Animation.LoopType.HOLD_ON_LAST_FRAME));
                }
                event.getController().setAnimationSpeed(4.00);
                if (!this.getLeftArm()) return event.setAndContinue(RawAnimation.begin().then("animation.golem_cobble.attack_right", Animation.LoopType.HOLD_ON_LAST_FRAME));
                return event.setAndContinue(RawAnimation.begin().then("animation.golem_cobble.attack_left", Animation.LoopType.HOLD_ON_LAST_FRAME));
            }
            else
            {
                event.getController().setAnimationSpeed(1.00);
                pGolem.setAttackState(0);
                if (getVelocity().horizontalLengthSquared() > 0.001D || event.isMoving())
                    return event.setAndContinue(RawAnimation.begin().thenLoop("animation.golem_cobble.walk"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.golem_cobble.idle"));
        }));
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }
}
