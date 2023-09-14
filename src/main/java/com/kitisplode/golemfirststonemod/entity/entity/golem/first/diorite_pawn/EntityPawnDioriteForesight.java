package com.kitisplode.golemfirststonemod.entity.entity.golem.first.diorite_pawn;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityPawn;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.EntityGolemFirstDiorite;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemMossy;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDelayedMeleeAttack;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriFollowHardGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriFollowSoftGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriMoveToDeployPositionGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.MultiStageAttackGoalRanged;
import com.kitisplode.golemfirststonemod.entity.goal.target.PassiveTargetGoal;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.control.FlightMoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class EntityPawnDioriteForesight extends EntityGolemMossy implements GeoEntity, IEntityWithDelayedMeleeAttack, IEntityDandoriFollower
{
    private static final Identifier MODEL = new Identifier(GolemFirstStoneMod.MOD_ID, "geo/diorite_foresight.geo.json");
    private static final Identifier TEXTURE = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/pawn/diorite/diorite_foresight.png");
    public static final Identifier GLOW_TEXTURE = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/pawn/diorite/diorite_foresight_glowmask.png");
    private static final Identifier ANIMATIONS = new Identifier(GolemFirstStoneMod.MOD_ID, "animations/diorite_foresight.animation.json");

    private LivingEntity owner;

    public double floatAmount = 0.0f;
    private float floatCycle;
    private MultiStageAttackGoalRanged attackGoal;

    public EntityPawnDioriteForesight(EntityType<? extends IronGolemEntity> pEntityType, World pLevel)
    {
        super(pEntityType, pLevel);
        this.moveControl = new FlightMoveControl(this, 20, true);
        floatCycle = this.getRandom().nextInt(360);
        this.shieldStatusEffects.clear();
        this.shieldStatusEffects.add(new StatusEffectInstance(StatusEffects.STRENGTH, 100, 2, false, true));
        this.shieldStatusEffects.add(new StatusEffectInstance(StatusEffects.RESISTANCE, 100, 2, false, true));
    }

    public static DefaultAttributeContainer.Builder setAttributes()
    {
        return GolemEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35f)
                .add(EntityAttributes.GENERIC_FLYING_SPEED, 0.75f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.25f);
    }

    @Override
    protected EntityNavigation createNavigation(World world) {
        BirdNavigation birdNavigation = new BirdNavigation(this, world);
        birdNavigation.setCanPathThroughDoors(false);
        birdNavigation.setCanSwim(false);
        birdNavigation.setCanEnterOpenDoors(false);
        return birdNavigation;
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
                    && (dandoriFollower.getOwner() == this.getOwner()
                    || (dandoriFollower.getOwner() instanceof IEntityDandoriFollower dandoriFollowerOwner && dandoriFollowerOwner.getOwner() == this.getOwner())))
                    || (entity instanceof EntityPawn pawn
                    && pawn.getOwner() instanceof EntityGolemFirstDiorite firstDiorite
                    && firstDiorite.getOwner() == this.getOwner())
                    || entity instanceof MerchantEntity)
            {
                if (entity instanceof MobEntity mob)
                    return mob.getTarget() != null && mob.getTarget().isAlive() && mob.getTarget() instanceof Monster;
            }
            return false;
        };
    }

    @Override
    public boolean tryAttack()
    {
        if (getAttackState() == 2)
        {
            List<MobEntity> enemies = this.getWorld().getEntitiesByClass(MobEntity.class, this.getBoundingBox().expand(8), entity -> entity instanceof Monster && entity.canTarget(this));
            for (MobEntity enemy : enemies)
            {
                if (enemy.getTarget() == null
                        || enemy.squaredDistanceTo(this) < enemy.squaredDistanceTo(enemy.getTarget())
                        || enemy instanceof RangedAttackMob)
                {
                    enemy.setTarget(this);
                }
            }
        }
        return super.tryAttack();
    }

    public void tick()
    {
        super.tick();
        if (!this.getWorld().isClient())
        {
            BlockPos bp = this.getBlockPos();
            {
                BlockState bs = this.getWorld().getBlockState(bp);
                BlockState bsUnder = this.getWorld().getBlockState(bp.down());
                if (bs.isOpaque() || bsUnder.isOpaque())
                {
                    this.setVelocity(this.getVelocity().add(0,0.03,0));
                }
                else
                {
                    this.setVelocity(this.getVelocity().add(0,-0.01,0));
                }
            }
            if (this.age > 5 && this.getOwner() == null) discard();
            this.setDandoriState(DANDORI_STATES.SOFT.ordinal());
        }
        else
        {
            this.floatCycle += 15;
            this.floatCycle = this.floatCycle % 360;
            this.floatAmount = Math.sin(this.floatCycle * MathHelper.RADIANS_PER_DEGREE);
        }
    }

    @Override
    protected void updateDeployPosition()
    {
        if (this.getDeployPosition() != null)
        {
            if (this.squaredDistanceTo(this.getDeployPosition().toCenterPos()) < 4) this.setDeployPosition(null);
        }
    }

    @Override
    protected ArrayList<StatusEffectInstance> getStatusEffect()
    {
        ArrayList<StatusEffectInstance> results = new ArrayList<>();
        results.add(shieldStatusEffects.get(this.getRandom().nextInt(shieldStatusEffects.size())));
        return results;
    }

    @Override
    public void setOwner(LivingEntity pOwner)
    {
        this.owner = pOwner;
    }

    @Override
    public LivingEntity getOwner()
    {
        return this.owner;
    }

    @Override
    public double getTargetRange()
    {
        return this.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
    }

    public Identifier getModelLocation()
    {
        return MODEL;
    }

    public Identifier getTextureLocation()
    {
        return TEXTURE;
    }

    public Identifier getAnimationsLocation()
    {
        return ANIMATIONS;
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
                    return event.setAndContinue(RawAnimation.begin().then("animation.diorite_foresight.attack_windup", Animation.LoopType.HOLD_ON_LAST_FRAME));
                }
                event.getController().setAnimationSpeed(1.00);
                return event.setAndContinue(RawAnimation.begin().then("animation.diorite_foresight.attack", Animation.LoopType.HOLD_ON_LAST_FRAME));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.diorite_foresight.idle"));
        }));
    }

}
