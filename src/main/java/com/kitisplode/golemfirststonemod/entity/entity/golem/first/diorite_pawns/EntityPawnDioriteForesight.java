package com.kitisplode.golemfirststonemod.entity.entity.golem.first.diorite_pawns;

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
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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
    private static final ResourceLocation MODEL = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "geo/diorite_foresight.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/pawn/diorite/diorite_foresight.png");
    public static final ResourceLocation GLOW_TEXTURE = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/pawn/diorite/diorite_foresight_glowmask.png");
    private static final ResourceLocation ANIMATIONS = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "animations/entity/golem/first/pawn/diorite_foresight.animation.json");

    private static final RawAnimation ANIMATION_ATTACK_WINDUP = RawAnimation.begin().then("animation.diorite_foresight.attack_windup", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation ANIMATION_ATTACK = RawAnimation.begin().then("animation.diorite_foresight.attack", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation ANIMATION_IDLE = RawAnimation.begin().thenLoop("animation.diorite_foresight.idle");

    private LivingEntity owner;

    public double floatAmount = 0.0f;
    private float floatCycle;
    private MultiStageAttackGoalRanged attackGoal;

    public EntityPawnDioriteForesight(EntityType<? extends IronGolem> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        floatCycle = this.getRandom().nextInt(360);
        this.shieldStatusEffects.clear();
        this.shieldStatusEffects.add(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 0, false, true));
        this.shieldStatusEffects.add(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 0, false, true));
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.35f)
                .add(Attributes.FLYING_SPEED, 0.75f)
                .add(Attributes.ATTACK_DAMAGE, 5.0f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.25f);
    }
    public static AttributeSupplier setAttributes()
    {
        return createAttributes().build();
    }

    protected PathNavigation createNavigation(Level pLevel) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, pLevel);
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(false);
        flyingpathnavigation.setCanPassDoors(false);
        return flyingpathnavigation;
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
                    || entity instanceof AbstractVillager)
            {
                if (entity instanceof Mob mob)
                    return mob.getTarget() != null && mob.getTarget().isAlive() && mob.getTarget() instanceof Enemy;
            }
            return false;
        };
    }

    @Override
    public boolean tryAttack()
    {
        if (getAttackState() == 2)
        {
            List<Mob> enemies = this.level().getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(8), entity -> entity instanceof Enemy && entity.canAttack(this));
            for (Mob enemy : enemies)
            {
                if (enemy.getTarget() == null
                        || enemy.distanceToSqr(this) < enemy.distanceToSqr(enemy.getTarget())
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
        if (!this.level().isClientSide())
        {
            BlockPos bp = this.getOnPos();
            {
                BlockState bs = this.level().getBlockState(bp);
                BlockState bsUnder = this.level().getBlockState(bp.below());
                if (bs.canOcclude() || bsUnder.canOcclude())
                {
                    this.setDeltaMovement(this.getDeltaMovement().add(0,0.03,0));
                }
                else
                {
                    this.setDeltaMovement(this.getDeltaMovement().add(0,-0.01,0));
                }
            }
            if (this.tickCount > 5 && this.getOwner() == null) discard();
            this.setDandoriState(DANDORI_STATES.SOFT.ordinal());
        }
        else
        {
            this.floatCycle += 15;
            this.floatCycle = this.floatCycle % 360;
            this.floatAmount = Math.sin(this.floatCycle * Mth.DEG_TO_RAD);
        }
    }

    @Override
    protected void updateDeployPosition()
    {
        if (this.getDeployPosition() != null)
        {
            if (this.distanceToSqr(this.getDeployPosition().getCenter()) < 4) this.setDeployPosition(null);
        }
    }

    @Override
    protected ArrayList<MobEffectInstance> getStatusEffect()
    {
        ArrayList<MobEffectInstance> results = new ArrayList<>();
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
        return this.getAttributeValue(Attributes.FOLLOW_RANGE);
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
            EntityPawnDioriteForesight pGolem = event.getAnimatable();
            if (pGolem.getAttackState() > 0)
            {
                if (pGolem.getAttackState() == 1)
                {
                    event.getController().setAnimationSpeed(1.00);
                    return event.setAndContinue(ANIMATION_ATTACK_WINDUP);
                }
                event.getController().setAnimationSpeed(1.00);
                return event.setAndContinue(ANIMATION_ATTACK);
            }
            return event.setAndContinue(ANIMATION_IDLE);
        }));
    }
}
