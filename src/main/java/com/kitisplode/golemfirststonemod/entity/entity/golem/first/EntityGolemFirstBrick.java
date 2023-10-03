package com.kitisplode.golemfirststonemod.entity.entity.golem.first;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.entity.entity.golem.AbstractGolemDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityPawn;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDelayedMeleeAttack;
import com.kitisplode.golemfirststonemod.entity.entity.effect.EntityEffectShieldFirstBrick;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriFollowHardGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriFollowSoftGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriMoveToDeployPositionGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.MultiStageAttackGoalRanged;
import com.kitisplode.golemfirststonemod.entity.goal.target.PassiveTargetGoal;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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


public class EntityGolemFirstBrick extends AbstractGolemDandoriFollower implements GeoEntity, IEntityWithDelayedMeleeAttack, IEntityDandoriFollower
{
    private static final ResourceLocation MODEL = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "geo/first_brick.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/first/first_brick.png");
    private static final ResourceLocation ANIMATIONS = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "animations/entity/golem/first/first_brick.animation.json");

    private static final RawAnimation ANIMATION_ATTACK_WINDUP = RawAnimation.begin().then("animation.first_brick.attack_windup", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation ANIMATION_ATTACK = RawAnimation.begin().then("animation.first_brick.attack", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation ANIMATION_ATTACK_END = RawAnimation.begin().then("animation.first_brick.attack_end", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation ANIMATION_WALK = RawAnimation.begin().thenLoop("animation.first_brick.walk");
    private static final RawAnimation ANIMATION_IDLE = RawAnimation.begin().thenLoop("animation.first_brick.idle");

    private static final EntityDataAccessor<Integer> ATTACK_STATE = SynchedEntityData.defineId(EntityGolemFirstBrick.class, EntityDataSerializers.INT);
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final int shieldHurtTime = 30;
    private static final int shieldEffectAmount = 1;
    private static final int shieldTime = 120;
    private static final float attackAOERange = 4.5f;
    private static final float attackVerticalRange = 5.0f;
    private static final ArrayList<MobEffectInstance> shieldStatusEffects = new ArrayList<>();
    private MultiStageAttackGoalRanged attackGoal;

    public EntityGolemFirstBrick(EntityType<? extends IronGolem> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
        shieldStatusEffects.add(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, shieldTime, shieldEffectAmount, false, true));
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 500.0f)
            .add(Attributes.MOVEMENT_SPEED, 0.25f)
            .add(Attributes.ATTACK_DAMAGE, 30.0f)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1.0f)
            .add(Attributes.FOLLOW_RANGE, 32.0f);
    }

    public static AttributeSupplier setAttributes()
    {
        return createAttributes().build();
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
        return getY() + 2.2d;
    }

    @Override
    protected void registerGoals()
    {
        this.attackGoal = new MultiStageAttackGoalRanged(this, 1.0, true, Mth.square(attackAOERange), new int[]{120, 85,80, 25});
        this.attackGoal.setCooldownMax(200);

        this.goalSelector.addGoal(0, new DandoriFollowHardGoal(this, 1.4, dandoriMoveRange, dandoriSeeRange));
        this.goalSelector.addGoal(1, new DandoriFollowSoftGoal(this, 1.4, dandoriMoveRange, dandoriSeeRange));

        this.goalSelector.addGoal(2, this.attackGoal);
        this.goalSelector.addGoal(3, new DandoriMoveToDeployPositionGoal(this, 2.0f, 1.0f));

        this.goalSelector.addGoal(4, new DandoriFollowSoftGoal(this, 1.4, dandoriMoveRange, 0));

        this.goalSelector.addGoal(5, new MoveTowardsTargetGoal(this, 0.8D, 32.0F));
        this.goalSelector.addGoal(6, new GolemRandomStrollInVillageGoal(this, 0.8D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, AbstractVillager.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
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
                        && (dandoriFollower.getOwner() == this.getOwner()
                        || (dandoriFollower.getOwner() instanceof IEntityDandoriFollower dandoriFollowerOwner && dandoriFollowerOwner.getOwner() == this.getOwner())))
                    || (entity instanceof EntityPawn pawn
                        && pawn.getOwner() instanceof EntityGolemFirstDiorite firstDiorite
                        && firstDiorite.getOwner() == this.getOwner())
                    || (entity instanceof Player && entity == this.getOwner())
                    || entity instanceof AbstractVillager)
            {
                if (entity instanceof Mob mob)
                    return mob.getTarget() != null && mob.getTarget().isAlive() && mob.getTarget() instanceof Enemy;
//                // For entities currently being attacked:
//                LivingEntity targetCurrentAttacker = entity.getLastHurtByMob();
//                if (targetCurrentAttacker != null && targetCurrentAttacker.isAlive())
//                {
//                    return golemTarget_checkTargetAttacker(targetCurrentAttacker);
//                }
//
//                // For entities not currently being attacked but attacked recently.
//                LivingEntity targetLastAttacker = entity.getLastAttacker();
//                if (targetLastAttacker != null)
//                {
//                    entity.tick();
//                    if (Mth.abs(entity.getLastHurtByMobTimestamp() - entity.tickCount) < shieldHurtTime)
//                    {
//                        return golemTarget_checkTargetAttacker(targetLastAttacker);
//                    }
//                }

                //
            }
            return false;
        };
    }

//    private boolean golemTarget_checkTargetAttacker(LivingEntity targetAttacker)
//    {
//        // If the golem was player made, skip potential targets that were attacked by the player.
//        if (targetAttacker instanceof Player && targetAttacker == this.getOwner())
//        {
//            return false;
//        }
//        // Skip other potential targets that are being attacked by golems (only happens accidentally or by other cleric golems)
//        if (targetAttacker instanceof IEntityDandoriFollower dandoriFollower)
//        {
//            if (dandoriFollower.getOwner() == this.getOwner()) return false;
//        }
//        // Otherwise, this is a good target.
//        return true;
//    }

    public boolean canAttack(LivingEntity entity) {
        return !(entity instanceof Enemy);
    }

    @Override
    public boolean tryAttack()
    {
        if (getAttackState() != 3) return false;

        this.level().broadcastEntityEvent(this, (byte)4);
        this.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 1.0F);
        this.playSound(SoundEvents.BEACON_POWER_SELECT, 1.0F, 1.0F);
        attackDust();
        attackAOE();

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

    private void attackDust()
    {
        float range = attackAOERange + 1;
        AreaEffectCloud dust = new AreaEffectCloud(level(), getX(),getY(),getZ());
        dust.setParticle(ParticleTypes.HAPPY_VILLAGER);
        dust.setRadius(range);
        dust.setDuration(1);
        dust.setPos(getX(),getY(),getZ());
        level().addFreshEntity(dust);
    }

    private void attackAOE()
    {
        EntityShieldFirstBrick shield = ModEntities.ENTITY_SHIELD_FIRST_BRICK.get().create(level());
        if (shield != null)
        {
            shield.setPos(this.position());
            shield.setLifeTime(shieldTime);
            shield.setFullScale((attackAOERange + 1) * 2.0f);
            shield.setOwner(this);
            level().addFreshEntity(shield);
        }

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
                if (dandoriFollower.getOwner() instanceof IEntityDandoriFollower dandoriFollowerOwner && dandoriFollowerOwner.getOwner() != this.getOwner()) continue;
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

            for (MobEffectInstance statusEffectInstance : shieldStatusEffects)
            {
                MobEffect statusEffect = statusEffectInstance.getEffect();
                int i2 = statusEffectInstance.mapDuration(i -> (int)(1 * (double)i + 0.5));
                MobEffectInstance statusEffectInstance2 = new MobEffectInstance(statusEffect, i2, statusEffectInstance.getAmplifier(), statusEffectInstance.isAmbient(), statusEffectInstance.isVisible());
                if (statusEffectInstance2.getDuration() < 20) continue;
                target.addEffect(statusEffectInstance2, this);
            }
        }
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
//        if (this.getAttackState() == 0 && this.attackGoal != null && this.attackGoal.isCooledDown() && this.isDandoriOff())
//        {
//            List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(32), entity->entity instanceof Enemy && this.hasLineOfSight(entity));
//            if (!list.isEmpty())
//            {
//                this.attackGoal.forceAttack();
//            }
//        }
    }

    @Override
    protected InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        return InteractionResult.PASS;
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
            EntityGolemFirstBrick pGolem = event.getAnimatable();
            if (pGolem.getAttackState() > 0)
            {
                switch (pGolem.getAttackState())
                {
                    case 1:
                        event.getController().setAnimationSpeed(0.5);
                        return event.setAndContinue(ANIMATION_ATTACK_WINDUP);
                    case 2, 3:
                        event.getController().setAnimationSpeed(1.00);
                        return event.setAndContinue(ANIMATION_ATTACK);
                    default:
                        event.getController().setAnimationSpeed(1.00);
                        return event.setAndContinue(ANIMATION_ATTACK_END);
                }
            }
            else
            {
                event.getController().setAnimationSpeed(1.00);
                pGolem.setAttackState(0);
                if (getDeltaMovement().horizontalDistanceSqr() > 0.001D || event.isMoving())
                    return event.setAndContinue(ANIMATION_WALK);
            }
            return event.setAndContinue(ANIMATION_IDLE);
        }));
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }
}
