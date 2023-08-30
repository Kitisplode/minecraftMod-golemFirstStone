package com.kitisplode.golemfirststonemod.entity.entity.golem.legends;

import com.kitisplode.golemfirststonemod.entity.entity.golem.AbstractGolemDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityCanAttackBlocks;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDelayedMeleeAttack;
import com.kitisplode.golemfirststonemod.entity.goal.goal.DandoriFollowGoal;
import com.kitisplode.golemfirststonemod.entity.goal.goal.MultiStageAttackBlockGoalRanged;
import com.kitisplode.golemfirststonemod.entity.goal.goal.MultiStageAttackGoalRanged;
import com.kitisplode.golemfirststonemod.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.GolemRandomStrollInVillageGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
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
    private static final EntityDataAccessor<Integer> ATTACK_STATE = SynchedEntityData.defineId(EntityGolemCobble.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> LEFT_ARM = SynchedEntityData.defineId(EntityGolemCobble.class, EntityDataSerializers.BOOLEAN);
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private BlockPos blockTarget;
    private final Predicate<BlockState> bsPredicate = blockState -> blockState != null
            && ((blockState.is(BlockTags.MINEABLE_WITH_PICKAXE)
            || blockState.is(BlockTags.MINEABLE_WITH_SHOVEL))
            && !blockState.is(BlockTags.NEEDS_IRON_TOOL)
            && !blockState.is(BlockTags.NEEDS_DIAMOND_TOOL));
    private int blockBreakProgress = 0;

    public EntityGolemCobble(EntityType<? extends IronGolem> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 75.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.35f)
                .add(Attributes.ATTACK_DAMAGE, 1.5f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75f);
    }

    public static AttributeSupplier setAttributes()
    {
        return createAttributes().build();
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
        if (!this.entityData.hasItem(ATTACK_STATE)) this.entityData.define(ATTACK_STATE, 0);
        if (!this.entityData.hasItem(LEFT_ARM)) this.entityData.define(LEFT_ARM, false);
    }
    public int getAttackState()
    {
        return this.entityData.get(ATTACK_STATE);
    }
    public void setAttackState(int pInt)
    {
        this.entityData.set(ATTACK_STATE, pInt);
    }
    public boolean getLeftArm()
    {
        return this.entityData.get(LEFT_ARM);
    }
    public void setLeftArm(boolean pBoolean)
    {
        this.entityData.set(LEFT_ARM, pBoolean);
    }

    private float getAttackDamage() {
        return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    @Override
    public double getEyeY()
    {
        return getY() + 0.5f;
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(1, new DandoriFollowGoal(this, 1.2, Ingredient.of(ModItems.ITEM_DANDORI_CALL.get(), ModItems.ITEM_DANDORI_ATTACK.get()), dandoriMoveRange, dandoriSeeRange));
        this.goalSelector.addGoal(2, new MultiStageAttackGoalRanged(this, 1.0, true, 6.0D, new int[]{10, 5}));
        this.goalSelector.addGoal(2, new MultiStageAttackBlockGoalRanged(this, 1.0, true, 8.0D, new int[]{10, 5}));
        this.goalSelector.addGoal(3, new MoveTowardsTargetGoal(this, 0.8D, 32.0F));
        this.goalSelector.addGoal(4, new GolemRandomStrollInVillageGoal(this, 0.8D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, (p_28879_) -> p_28879_ instanceof Enemy && !(p_28879_ instanceof Creeper)));
    }

    @Override
    public boolean tryAttack()
    {
        if (getAttackState() == 1) this.setLeftArm(!this.getLeftArm());
        if (getAttackState() != 2) return false;

        if (getTarget() != null)
        {
            this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
            getTarget().hurt(this.damageSources().mobAttack(this), getAttackDamage());
            this.doEnchantDamageEffects(this, getTarget());
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
        if ((this.getTarget() != null || this.getDandoriState()) && getBlockTarget() != null)
        {
            level().destroyBlockProgress(getId(), this.blockTarget, -1);
            setBlockTarget(null);
            this.blockBreakProgress = 0;
        }
        if (!canTargetBlock(getBlockTarget())) setBlockTarget(null);
    }
    @Override
    public void setBlockTarget(BlockPos pBlockPos)
    {
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
        return bsPredicate.test(level().getBlockState(pBlockPos));
    }
    @Override
    public boolean tryAttackBlock()
    {
        if (getAttackState() == 1) this.setLeftArm(!this.getLeftArm());
        if (getAttackState() != 2) return false;
        if (!canTargetBlock(getBlockTarget())) return false;

        this.blockBreakProgress += 16;
        BlockState bs = level().getBlockState(this.blockTarget);
        if (this.blockBreakProgress >= 100)
        {
            this.playSound(SoundEvents.ROOTED_DIRT_BREAK, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
            bs.getBlock().onBlockStateChange(level(), this.blockTarget, bs, null);
            Block.dropResources(bs, level(), this.blockTarget);
            level().removeBlock(this.blockTarget, false);
            level().levelEvent(2001, this.blockTarget, Block.getId(level().getBlockState(this.blockTarget)));
            findNewTargetBlock();
            this.blockBreakProgress = 0;
        } else
        {
            this.playSound(SoundEvents.ROOTED_DIRT_HIT, 1.0f, (this.random.nextFloat() - this.random.nextFloat()) * 0.2f + 1.0f);
            level().destroyBlockProgress(getId(), this.blockTarget, this.blockBreakProgress);
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
                if (getDeltaMovement().horizontalDistanceSqr() > 0.001D || event.isMoving())
                    return event.setAndContinue(RawAnimation.begin().thenLoop("animation.golem_cobble.walk"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.golem_cobble.idle"));
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }
}
