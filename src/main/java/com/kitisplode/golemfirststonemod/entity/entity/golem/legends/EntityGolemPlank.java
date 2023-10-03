package com.kitisplode.golemfirststonemod.entity.entity.golem.legends;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.entity.entity.golem.AbstractGolemDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDelayedMeleeAttack;
import com.kitisplode.golemfirststonemod.entity.entity.projectile.EntityProjectileAoEOwnerAware;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriFollowHardGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriFollowSoftGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriMoveToDeployPositionGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.MultiStageAttackGoalRanged;
import com.kitisplode.golemfirststonemod.entity.goal.target.SharedTargetGoal;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.GolemRandomStrollInVillageGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

public class EntityGolemPlank extends AbstractGolemDandoriFollower implements GeoEntity, IEntityWithDelayedMeleeAttack, IEntityDandoriFollower
{
    private static final ResourceLocation MODEL = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "geo/entity/golem/legends/golem_plank.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/legends/golem_plank.png");
    private static final ResourceLocation ANIMATIONS = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "animations/entity/golem/legends/golem_plank.animation.json");

    private static final RawAnimation ANIMATION_ATTACK_WINDUP = RawAnimation.begin().then("animation.golem_plank.attack_windup", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation ANIMATION_ATTACK = RawAnimation.begin().then("animation.golem_plank.attack", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation ANIMATION_WALK = RawAnimation.begin().thenLoop("animation.golem_plank.walk");
    private static final RawAnimation ANIMATION_IDLE = RawAnimation.begin().thenLoop("animation.golem_plank.idle");

    private static final EntityDataAccessor<Integer> ATTACK_STATE = SynchedEntityData.defineId(EntityGolemPlank.class, EntityDataSerializers.INT);
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final float projectileSpeed = 3.0f;

    public EntityGolemPlank(EntityType<? extends IronGolem> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 35.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.35f)
                .add(Attributes.ATTACK_DAMAGE, 1.0f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5f)
                .add(Attributes.FOLLOW_RANGE, 24);
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
        this.goalSelector.addGoal(0, new DandoriFollowHardGoal(this, 1.2, dandoriMoveRange, dandoriSeeRange));
        this.goalSelector.addGoal(1, new DandoriFollowSoftGoal(this, 1.2, dandoriMoveRange, dandoriSeeRange));

        this.goalSelector.addGoal(2, new MultiStageAttackGoalRanged(this, 1.0, true, Mth.square(20), new int[]{30, 10}));
        this.goalSelector.addGoal(3, new DandoriMoveToDeployPositionGoal(this, 2.0f, 1.0f));

        this.goalSelector.addGoal(4, new DandoriFollowSoftGoal(this, 1.2, dandoriMoveRange, 0));

        this.goalSelector.addGoal(5, new MoveTowardsTargetGoal(this, 0.8D, 32.0F));
        this.goalSelector.addGoal(6, new GolemRandomStrollInVillageGoal(this, 0.8D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, AbstractVillager.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new SharedTargetGoal<>(this, AbstractGolem.class, Mob.class, 5, true, false, (p_28879_) -> p_28879_ instanceof Enemy && !(p_28879_ instanceof Creeper), 16));
    }

    @Override
    public boolean tryAttack()
    {
        if (getAttackState() != 2) return false;

        if (getTarget() != null)
        {
            this.playSound(SoundEvents.ARROW_SHOOT, 1.0f, 1.0f);
            attack();
        }
        return true;
    }

    public void attack()
    {
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) return;

        // Spawn the projectile
        if (!this.level().isClientSide())
        {
            EntityProjectileAoEOwnerAware arrow = createProjectile();

            if (arrow == null) return;

            Vec3 shootingVelocity = target.getEyePosition().subtract(this.getEyePosition()).normalize().scale(projectileSpeed);
            arrow.setOwner(this);
            arrow.setPos(this.getEyePosition());
            arrow.setDeltaMovement(shootingVelocity);
            arrow.setBaseDamage(getAttackDamage());
            arrow.setAoERange(0.0f);
            arrow.setNoGravity(true);
            arrow.setHasAoE(false);
            this.level().addFreshEntity(arrow);
        }
    }

    protected EntityProjectileAoEOwnerAware createProjectile()
    {
        return ModEntities.ENTITY_PROJECTILE_FIRST_OAK.get().create(this.level());
    }

    @Override
    public boolean isPushable()
    {
        return getAttackState() == 0;
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
            EntityGolemPlank pGolem = event.getAnimatable();
            if (pGolem.getAttackState() > 0)
            {
                if (pGolem.getAttackState() == 1)
                {
                    event.getController().setAnimationSpeed(2.00);
                    return event.setAndContinue(ANIMATION_ATTACK_WINDUP);
                }
                event.getController().setAnimationSpeed(2.00);
                return event.setAndContinue(ANIMATION_ATTACK);
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

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }
}
