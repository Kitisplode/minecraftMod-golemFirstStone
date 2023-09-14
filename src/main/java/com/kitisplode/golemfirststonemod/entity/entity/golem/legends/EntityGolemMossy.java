package com.kitisplode.golemfirststonemod.entity.entity.golem.legends;

import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.entity.entity.effect.EntityEffectCubeDandoriWhistle;
import com.kitisplode.golemfirststonemod.entity.entity.golem.AbstractGolemDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityPawn;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.EntityGolemFirstDiorite;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDelayedMeleeAttack;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriFollowHardGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriFollowSoftGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriMoveToDeployPositionGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.MultiStageAttackGoalRanged;
import com.kitisplode.golemfirststonemod.entity.goal.target.PassiveTargetGoal;
import com.kitisplode.golemfirststonemod.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class EntityGolemMossy extends AbstractGolemDandoriFollower implements GeoEntity, IEntityWithDelayedMeleeAttack, IEntityDandoriFollower
{
    private static final TrackedData<Integer> ATTACK_STATE = DataTracker.registerData(EntityGolemMossy.class, TrackedDataHandlerRegistry.INTEGER);
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final int healRegenTime = 20 * 2;
    private static final int healRegenAmount = 0;
    private static final float attackAOERange = 4.0f;
    private static final float attackVerticalRange = 5.0f;
    protected final ArrayList<StatusEffectInstance> shieldStatusEffects = new ArrayList<>();
    private MultiStageAttackGoalRanged attackGoal;

    public EntityGolemMossy(EntityType<? extends IronGolemEntity> pEntityType, World pLevel)
    {
        super(pEntityType, pLevel);
        shieldStatusEffects.add(new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, healRegenTime, healRegenAmount, false, true));
    }

    public static DefaultAttributeContainer.Builder setAttributes()
    {
        return GolemEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 50.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0f)
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
    }

    public int getAttackState()
    {
        return this.dataTracker.get(ATTACK_STATE);
    }
    public void setAttackState(int pInt)
    {
        this.dataTracker.set(ATTACK_STATE, pInt);
    }

    @Override
    public double getEyeY()
    {
        return getY() + 0.5f;
    }

    @Override
    protected void initGoals() {
        this.attackGoal = new MultiStageAttackGoalRanged(this, 1.0, true, 4.0D, new int[]{80, 20});
        this.goalSelector.add(0, new DandoriFollowHardGoal(this, 1.2, dandoriMoveRange, dandoriSeeRange));
        this.goalSelector.add(1, new DandoriFollowSoftGoal(this, 1.2, dandoriMoveRange, dandoriSeeRange));

        this.goalSelector.add(2, this.attackGoal);
        this.goalSelector.add(3, new DandoriMoveToDeployPositionGoal(this, 2.0f, 1.0f));

        this.goalSelector.add(4, new DandoriFollowSoftGoal(this, 1.2, dandoriMoveRange, 0));

        this.goalSelector.add(5, new EscapeDangerGoal(this, 1.0));
        this.goalSelector.add(6, new WanderNearTargetGoal(this, 0.8, 32.0F));
        this.goalSelector.add(7, new IronGolemWanderAroundGoal(this, 0.8));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(8, new LookAtEntityGoal(this, MerchantEntity.class, 8.0F));
        this.goalSelector.add(9, new LookAroundGoal(this));
        this.targetSelector
                .add(1, new PassiveTargetGoal<PlayerEntity>(this, PlayerEntity.class, 5, true, false, golemTarget()));
        this.targetSelector
                .add(2, new PassiveTargetGoal<MobEntity>(this, MobEntity.class, 5, true, false, golemTarget()));
    }

    private Predicate<LivingEntity> golemTarget()
    {
        return entity ->
        {
            // Skip itself.
            if (entity == this) return false;
            // Check other golems, villagers, and players
            if ((entity instanceof IEntityDandoriFollower dandoriFollower
                    && dandoriFollower.getOwner() == this.getOwner())
                    || (entity instanceof EntityPawn pawn && pawn.getOwner() instanceof EntityGolemFirstDiorite firstDiorite && firstDiorite.getOwner() == this.getOwner())
                    || (entity instanceof PlayerEntity && entity == this.getOwner())
                    || entity instanceof MerchantEntity)
            {
                // See if the entity has less than its max HP.
                return entity.getHealth() < entity.getMaxHealth();
            }
            return false;
        };
    }

    @Override
    public boolean tryAttack()
    {
        if (getAttackState() != 2) return false;

        attackAOE();
        effectAttack(getWorld(), this, 10);

        this.playSound(SoundEvents.BLOCK_WATER_AMBIENT, 1.0f, 1.0f);
        this.playSound(SoundEvents.ENTITY_BOAT_PADDLE_WATER, 2.0f, 1.0f);

        // Check to see if the target is still viable...
        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive())
        {
            boolean targetGood = golemTarget().test(target);
            if (!targetGood) this.setTarget(null);
        }
        else
        {
            this.setTarget(null);
        }

        return true;
    }

    private void attackAOE()
    {
        List<LivingEntity> targetList = getWorld().getNonSpectatingEntities(LivingEntity.class, getBoundingBox().expand(attackAOERange));
        for (LivingEntity target : targetList)
        {
            // Do not heal ourselves.
            if (target == this) continue;
            // Do not heal targets that are monsters.
            if (target instanceof Monster) continue;
            // Do not heal targets that are players if we are not player created.
            if (target instanceof PlayerEntity && target != this.getOwner()) continue;
            // Do not heal dandori followers that are not owned by our owner.
            if (target instanceof IEntityDandoriFollower dandoriFollower)
            {
                if (dandoriFollower.getOwner() != this.getOwner()) continue;
                if (dandoriFollower instanceof EntityPawn pawn)
                {
                    if (pawn.getOwnerType() == EntityPawn.OWNER_TYPES.FIRST_OF_DIORITE.ordinal() && pawn.getOwner() instanceof EntityGolemFirstDiorite firstDiorite)
                    {
                        if (firstDiorite.getOwner() != this.getOwner()) continue;
                    }
                }
            }
            // Do not heal targets that are too far on the y axis.
            if (Math.abs(getY() - target.getY()) > attackVerticalRange) continue;

            ArrayList<StatusEffectInstance> mobEffects = getStatusEffect();
            for (StatusEffectInstance statusEffectInstance : mobEffects)
            {
                target.addStatusEffect(new StatusEffectInstance(statusEffectInstance), this);
            }
        }
    }

    protected ArrayList<StatusEffectInstance> getStatusEffect()
    {
        return this.shieldStatusEffects;
    }

    private void effectAttack(World world, LivingEntity user, int time)
    {
        EntityEffectCubeDandoriWhistle whistleEffect = ModEntities.ENTITY_EFFECT_CUBE_DANDORI_WHISTLE.create(world);
        if (whistleEffect != null)
        {
            whistleEffect.setPosition(user.getPos());
            whistleEffect.setLifeTime(time);
            whistleEffect.setFullScale(attackAOERange * 2.0f);
            whistleEffect.setOwner(user);
            world.spawnEntity(whistleEffect);
        }
    }

    @Override
    public boolean isPushable()
    {
        return getAttackState() == 0;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, event ->
        {
            EntityGolemMossy pGolem = event.getAnimatable();
            if (pGolem.getAttackState() > 0)
            {
                if (pGolem.getAttackState() == 1)
                {
                    event.getController().setAnimationSpeed(0.50);
                    return event.setAndContinue(RawAnimation.begin().then("animation.golem_mossy.attack_windup", Animation.LoopType.HOLD_ON_LAST_FRAME));
                }
                event.getController().setAnimationSpeed(1.00);
                return event.setAndContinue(RawAnimation.begin().then("animation.golem_mossy.attack", Animation.LoopType.HOLD_ON_LAST_FRAME));
            }
            else
            {
                event.getController().setAnimationSpeed(1.00);
                pGolem.setAttackState(0);
                if (getVelocity().horizontalLengthSquared() > 0.001D || event.isMoving())
                    return event.setAndContinue(RawAnimation.begin().thenLoop("animation.golem_mossy.walk"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.golem_mossy.idle"));
        }));
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }
}
