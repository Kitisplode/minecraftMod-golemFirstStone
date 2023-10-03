package com.kitisplode.golemfirststonemod.entity.entity.golem.first;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.entity.entity.effect.EntityEffectCubeDandoriWhistle;
import com.kitisplode.golemfirststonemod.entity.entity.golem.AbstractGolemDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityPawn;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntitySummoner;
import com.kitisplode.golemfirststonemod.entity.goal.action.*;
import com.kitisplode.golemfirststonemod.entity.goal.target.ActiveTargetGoalBiggerY;
import com.kitisplode.golemfirststonemod.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;


public class EntityGolemFirstDiorite extends AbstractGolemDandoriFollower implements GeoEntity, IEntitySummoner, IEntityDandoriFollower
{
    private static final ResourceLocation MODEL = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "geo/entity/golem/first/first_diorite.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/first/first_diorite.png");
    private static final ResourceLocation ANIMATIONS = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "animations/entity/golem/first/first_diorite.animation.json");

    private static final RawAnimation ANIMATION_ATTACK_WINDUP = RawAnimation.begin().then("animation.first_diorite.attack_windup", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation ANIMATION_ATTACK = RawAnimation.begin().then("animation.first_diorite.attack", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation ANIMATION_ATTACK_END = RawAnimation.begin().then("animation.first_diorite.attack_end", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation ANIMATION_WALK = RawAnimation.begin().thenLoop("animation.first_diorite.walk");
    private static final RawAnimation ANIMATION_IDLE = RawAnimation.begin().thenLoop("animation.first_diorite.idle");

    private static final EntityDataAccessor<Integer> SUMMON_STATE = SynchedEntityData.defineId(EntityGolemFirstDiorite.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SUMMON_COOLDOWN = SynchedEntityData.defineId(EntityGolemFirstDiorite.class, EntityDataSerializers.BOOLEAN);
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private static final double pawnSearchRange = 64;
    private static final int pawnsMax = 6;
    private static final int pawnsToSpawn = 3;
    private static final int spawnCooldown = 100;
    private static final int[] spawnStages = new int[]{125,85,35};
    private static final int spawnStage = 3;

    private SummonEntityGoal summonGoal;

    public EntityGolemFirstDiorite(EntityType<? extends IronGolem> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 500.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.25f)
                .add(Attributes.ATTACK_DAMAGE, 30.0f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0f)
                .add(Attributes.FOLLOW_RANGE, 24);
    }

    public static AttributeSupplier setAttributes()
    {
        return createAttributes().build();
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        if (!this.entityData.hasItem(SUMMON_STATE)) this.entityData.define(SUMMON_STATE, 0);
        if (!this.entityData.hasItem(SUMMON_COOLDOWN)) this.entityData.define(SUMMON_COOLDOWN, false);
    }
    public int getSummonState()
    {
        return this.entityData.get(SUMMON_STATE);
    }
    public void setSummonState(int pInt)
    {
        this.entityData.set(SUMMON_STATE, pInt);
    }
    public boolean getSummonCooleddown()
    {
        return this.entityData.get(SUMMON_COOLDOWN);
    }
    public void setSummonCooledDown(boolean pBoolean)
    {
        this.entityData.set(SUMMON_COOLDOWN, pBoolean);
    }

    @Override
    public int getMaxHeadYRot()
    {
        if (this.getSummonState() > 0) return 0;
        return super.getMaxHeadYRot();
    }
    @Override
    public int getMaxHeadXRot()
    {
        if (this.getSummonState() > 0) return 0;
        return super.getMaxHeadXRot();
    }
    @Override
    public int getHeadRotSpeed()
    {
        if (this.getSummonState() > 0) return 0;
        return super.getHeadRotSpeed();
    }

    @Override
    public double getEyeY()
    {
        return getY() + 2.2d;
    }

    @Override
    protected void registerGoals()
    {
        this.summonGoal = new SummonEntityGoal<>(this, AbstractGolemDandoriFollower.class, spawnStages, pawnSearchRange, pawnsMax, spawnCooldown, 1);

        this.goalSelector.addGoal(0, new DandoriFollowHardGoal(this, 1.4, dandoriMoveRange, dandoriSeeRange));
        this.goalSelector.addGoal(1, new DandoriFollowSoftGoal(this, 1.4, dandoriMoveRange, dandoriSeeRange));

        this.goalSelector.addGoal(2, this.summonGoal);
        this.goalSelector.addGoal(3, new DandoriMoveToDeployPositionGoal(this, 2.0f, 1.0f));
        this.goalSelector.addGoal(4, new DandoriFollowSoftGoal(this, 1.4, dandoriMoveRange, 0));

        this.goalSelector.addGoal(5, new PanicGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new WanderAroundTargetGoal(this, 0.8D, 13.0f));
        this.goalSelector.addGoal(7, new GolemRandomStrollInVillageGoal(this, 0.8D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, AbstractVillager.class, 6.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new ActiveTargetGoalBiggerY<>(this, Mob.class, 5, true, false, (entity) -> entity instanceof Enemy && !(entity instanceof Creeper), 5));
    }

    @Override
    public void tick()
    {
        super.tick();
        if (this.isSleeping() || this.isImmobile() || this.getSummonState() > 0)
        {
            this.xxa = 0.0F;
            this.zza = 0.0F;
        }
        if (!this.level().isClientSide())
        {
            if (this.summonGoal != null)
            {
                this.setSummonCooledDown(this.summonGoal.isCooledDown());
            }
        }
    }

    @Override
    public boolean isPushable()
    {
        return getSummonState() == 0;
    }

    @Override
    public boolean trySummon(int summonState)
    {
        if (summonState != spawnStage) return false;

        this.level().broadcastEntityEvent(this, (byte)4);
        this.playSound(SoundEvents.BEACON_POWER_SELECT, 1.0F, 1.0F);
        int pawnCount = pawnsToSpawn;
        int currentPawns = this.summonGoal.getPikCount();
        if (currentPawns > pawnsMax - pawnsToSpawn) pawnCount = Math.abs(currentPawns - pawnsMax);
        spawnPawns(pawnCount);
        spawnEffect(this.level(), 10, 4, new Vec3(this.getX(), this.getY() + 2.5d, this.getZ()));
        return true;
    }

    private void spawnPawns(int pawnCount)
    {
        for (int i = 0; i < pawnCount; i++)
        {
            double direction = (((double) (360 / pawnCount) * i) + this.getYRot()) * Mth.DEG_TO_RAD;
            double offset = 1.0f;
            Vec3 spawnOffset = new Vec3(Math.sin(direction) * offset,
                    2.5d,
                    Math.cos(direction) * offset);

            AbstractGolemDandoriFollower pawn = null;
            int randomType = this.getRandom().nextInt(3);
            if (i == 0)        pawn = ModEntities.ENTITY_PAWN_DIORITE_ACTION.get().create(level());
            else if (i == 1)   pawn = ModEntities.ENTITY_PAWN_DIORITE_KNOWLEDGE.get().create(level());
            else               pawn = ModEntities.ENTITY_PAWN_DIORITE_FORESIGHT.get().create(level());

            if (pawn == null) continue;
            pawn.setOwner(this);
            pawn.setPlayerCreated(isPlayerCreated());
            pawn.moveTo(getX() + spawnOffset.x(), getY() + spawnOffset.y(), getZ() + spawnOffset.z(), 0.0f, 0.0F);
            level().addFreshEntity(pawn);

            AreaEffectCloud dust = new AreaEffectCloud(level(), getX() + spawnOffset.x(), getY() + spawnOffset.y(), getZ() + spawnOffset.z());
            dust.setParticle(ParticleTypes.POOF);
            dust.setRadius(1.0f);
            dust.setDuration(1);
            dust.setPos(dust.getX(),dust.getY(),dust.getZ());
            level().addFreshEntity(dust);
        }
    }

    private void spawnEffect(Level world, int time, float range, Vec3 position)
    {
        EntityEffectCubeDandoriWhistle whistleEffect = ModEntities.ENTITY_EFFECT_CUBE_DANDORI_WHISTLE.get().create(world);
        if (whistleEffect != null)
        {
            whistleEffect.setPos(position);
            whistleEffect.setLifeTime(time);
            whistleEffect.setFullScale(range * 2.0f);
            whistleEffect.setYBodyRot(this.getYRot());
            whistleEffect.setYRot(this.getYRot());
            world.addFreshEntity(whistleEffect);
        }
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
            EntityGolemFirstDiorite pGolem = event.getAnimatable();
            if (pGolem.getSummonState() > 0)
            {
                switch (pGolem.getSummonState())
                {
                    case 1:
                        event.getController().setAnimationSpeed(0.5);
                        return event.setAndContinue(ANIMATION_ATTACK_WINDUP);
                    case 2:
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
                pGolem.setSummonState(0);
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
