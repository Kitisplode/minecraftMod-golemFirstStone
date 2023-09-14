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
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.level.Level;
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
    private static final EntityDataAccessor<Integer> ATTACK_STATE = SynchedEntityData.defineId(EntityGolemMossy.class, EntityDataSerializers.INT);
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final int healRegenTime = 20 * 2;
    private static final int healRegenAmount = 0;
    private static final float attackAOERange = 4.0f;
    private static final float attackVerticalRange = 5.0f;
    protected final ArrayList<MobEffectInstance> shieldStatusEffects = new ArrayList<>();
    private MultiStageAttackGoalRanged attackGoal;

    public EntityGolemMossy(EntityType<? extends IronGolem> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
        shieldStatusEffects.add(new MobEffectInstance(MobEffects.HEAL, healRegenTime, healRegenAmount, false, true));
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.35f)
                .add(Attributes.ATTACK_DAMAGE, 5.0f)
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
    }
    public int getAttackState()
    {
        return this.entityData.get(ATTACK_STATE);
    }
    public void setAttackState(int pInt)
    {
        this.entityData.set(ATTACK_STATE, pInt);
    }

    @Override
    public double getEyeY()
    {
        return getY() + 0.5f;
    }

    @Override
    protected void registerGoals()
    {
        this.attackGoal = new MultiStageAttackGoalRanged(this, 1.0, true, 4.0D, new int[]{80, 20});

        this.goalSelector.addGoal(0, new DandoriFollowHardGoal(this, 1.2, dandoriMoveRange, dandoriSeeRange));
        this.goalSelector.addGoal(1, new DandoriFollowSoftGoal(this, 1.2, dandoriMoveRange, dandoriSeeRange));

        this.goalSelector.addGoal(2, this.attackGoal);
        this.goalSelector.addGoal(3, new DandoriMoveToDeployPositionGoal(this, 2.0f, 1.0f));

        this.goalSelector.addGoal(4, new DandoriFollowSoftGoal(this, 1.2, dandoriMoveRange, 0));

        this.goalSelector.addGoal(5, new PanicGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new MoveTowardsTargetGoal(this, 0.8D, 32.0F));
        this.goalSelector.addGoal(7, new GolemRandomStrollInVillageGoal(this, 0.8D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, AbstractVillager.class, 6.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new PassiveTargetGoal<>(this, Player.class, 5, true, false, golemTarget()));
        this.targetSelector.addGoal(2, new PassiveTargetGoal<>(this, Mob.class, 5, true, false, golemTarget()));
    }

    private Predicate<LivingEntity> golemTarget()
    {
        return entity ->
        {
            // Skip itself.
            if (entity == this) return false;
            // Check other golems, villagers, and players
            if ((entity instanceof IEntityDandoriFollower dandoriFollower
                    && (dandoriFollower.getOwner() == this.getOwner()))
//                    || (dandoriFollower.getOwner() instanceof IEntityDandoriFollower dandoriFollowerOwner && dandoriFollowerOwner.getOwner() == this.getOwner())))
                    || (entity instanceof EntityPawn pawn
                        && pawn.getOwner() instanceof EntityGolemFirstDiorite firstDiorite
                        && firstDiorite.getOwner() == this.getOwner())
                    || (entity instanceof Player && entity == this.getOwner())
                    || entity instanceof AbstractVillager)
            {
                // See if the entity has less than its max HP.
                return entity.getHealth() < entity.getMaxHealth();
            }
            return false;
        };
    }

    public boolean canAttack(LivingEntity entity) {
        return !(entity instanceof Enemy);
    }

    @Override
    public boolean tryAttack()
    {
        if (getAttackState() != 2) return false;

        this.playSound(SoundEvents.WATER_AMBIENT, 1.0F, 1.0F);
        this.playSound(SoundEvents.BOAT_PADDLE_LAND, 2.0F, 1.0F);
        attackAOE();
        effectWhistle(level(), this, 10);

        // Check to see if the target is still viable.
        LivingEntity target = this.getTarget();
        if (target != null && target.isAlive())
        {
            boolean targetGood = golemTarget().test(target);
            if (!targetGood) this.setTarget(null);
        }
        else this.setTarget(null);

        return true;
    }

    private void attackAOE()
    {
        List<LivingEntity> targetList = level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(attackAOERange));
        for (LivingEntity target : targetList)
        {
            // Do not target ourselves.
            if (target == this) continue;
            // Do not shield targets that are monsters.
            if (target instanceof Enemy) continue;
            if (target instanceof Player && target != this.getOwner()) continue;
            // Do not shield dandori followers that are not owned by our owner.
            if (target instanceof IEntityDandoriFollower dandoriFollower)
            {
                if (dandoriFollower.getOwner() != this.getOwner()) continue;
//                if (!(this.getOwner() instanceof EntityGolemFirstDiorite) && dandoriFollower.getOwner() instanceof IEntityDandoriFollower dandoriFollowerOwner && dandoriFollowerOwner.getOwner() != this.getOwner()) continue;
                if (dandoriFollower instanceof EntityPawn pawn)
                {
                    if (pawn.getOwnerType() == EntityPawn.OWNER_TYPES.FIRST_OF_DIORITE.ordinal())
                    {
                        if (pawn.getOwner() != this.getOwner()) continue;
                    }
                }
            }
            // Do not damage targets that are too far on the y axis.
            if (Math.abs(getY() - target.getY()) > attackVerticalRange) continue;

            ArrayList<MobEffectInstance> mobEffects = getStatusEffect();
            for (MobEffectInstance statusEffectInstance : mobEffects)
            {
                target.addEffect(new MobEffectInstance(statusEffectInstance), this);
            }
        }
    }

    protected ArrayList<MobEffectInstance> getStatusEffect()
    {
        return this.shieldStatusEffects;
    }

    private void effectWhistle(Level world, LivingEntity user, int time)
    {
        EntityEffectCubeDandoriWhistle whistleEffect = ModEntities.ENTITY_EFFECT_CUBE_DANDORI_WHISTLE.get().create(world);
        if (whistleEffect != null)
        {
            whistleEffect.setPos(user.position());
            whistleEffect.setLifeTime(time);
            whistleEffect.setFullScale(attackAOERange * 2.0f);
            whistleEffect.setOwner(user);
            world.addFreshEntity(whistleEffect);
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
                if (getDeltaMovement().horizontalDistanceSqr() > 0.001D || event.isMoving())
                    return event.setAndContinue(RawAnimation.begin().thenLoop("animation.golem_mossy.walk"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.golem_mossy.idle"));
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }
}
