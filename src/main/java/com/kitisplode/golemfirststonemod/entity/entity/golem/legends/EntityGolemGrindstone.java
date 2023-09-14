package com.kitisplode.golemfirststonemod.entity.entity.golem.legends;

import com.kitisplode.golemfirststonemod.entity.entity.golem.AbstractGolemDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityPawn;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.EntityGolemFirstDiorite;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDelayedMeleeAttack;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriFollowHardGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriFollowSoftGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriMoveToDeployPositionGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.MultiStageAttackGoalRanged;
import com.kitisplode.golemfirststonemod.entity.goal.target.SharedTargetGoal;
import com.kitisplode.golemfirststonemod.item.ModItems;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.GolemRandomStrollInVillageGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

public class EntityGolemGrindstone extends AbstractGolemDandoriFollower implements GeoEntity, IEntityWithDelayedMeleeAttack, IEntityDandoriFollower
{
    private static final EntityDataAccessor<Integer> ATTACK_STATE = SynchedEntityData.defineId(EntityGolemGrindstone.class, EntityDataSerializers.INT);
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final float attackSpeed = 0.7f;
    private boolean movingBackwards = false;
    private MultiStageAttackGoalRanged attackGoal;

    private static final MobEffectInstance stunEffect = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 2, false, false);
    private static final MobEffectInstance armorEffect = new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 15, 3, false, false);

    public EntityGolemGrindstone(EntityType<? extends IronGolem> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 65.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.35f)
                .add(Attributes.ATTACK_DAMAGE, 3.0f)
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
        this.attackGoal = new MultiStageAttackGoalRanged(this, 1.0, true, Mth.square(12.0d), new int[]{70, 40, 20});
        this.goalSelector.addGoal(0, new DandoriFollowHardGoal(this, 1.2, dandoriMoveRange, dandoriSeeRange));
        this.goalSelector.addGoal(1, new DandoriFollowSoftGoal(this, 1.2, dandoriMoveRange, dandoriSeeRange));

        this.goalSelector.addGoal(2, this.attackGoal);
        this.goalSelector.addGoal(3, new DandoriMoveToDeployPositionGoal(this, 2.0f, 1.0f));

        this.goalSelector.addGoal(4, new DandoriFollowSoftGoal(this, 1.2, dandoriMoveRange, 0));

        this.goalSelector.addGoal(5, new MoveTowardsTargetGoal(this, 0.8D, 32.0F));
        this.goalSelector.addGoal(6, new GolemRandomStrollInVillageGoal(this, 0.8D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, AbstractGolem.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new SharedTargetGoal<>(this, AbstractGolem.class, Mob.class, 5, true, false, (p_28879_) -> p_28879_ instanceof Enemy && !(p_28879_ instanceof Creeper), 5));
    }

    @Override
    public boolean tryAttack()
    {
        if (getAttackState() != 2) return false;
        this.addEffect(new MobEffectInstance(armorEffect));
        return true;
    }

    @Override
    public void tick()
    {
        super.tick();
        // If we're attacking, move forward.
        if (getAttackState() == 2)
        {
            float angle = this.getYRot()*Mth.DEG_TO_RAD;
            Vec3 newVelocity = new Vec3(-Math.sin(angle), 0, Math.cos(angle)).scale(attackSpeed);
            this.setDeltaMovement(newVelocity);
        }
    }

    @Override
    public void push(Entity target)
    {
        if (getAttackState() == 2)
        {
            // Do not damage targets that are our owner or are owned by our owner.
            if (this.getOwner() == target) return;
            if (target instanceof TamableAnimal tamableAnimal && tamableAnimal.getOwner() == this.getOwner()) return;
            if (target instanceof IEntityDandoriFollower dandoriFollower && dandoriFollower.getOwner() == this.getOwner()) return;
            // Do not damage targets that are pawns owned by a first of diorite that is owned by our owner lol
            if (target instanceof EntityPawn pawn && pawn.getOwner() instanceof EntityGolemFirstDiorite firstDiorite)
            {
                if (firstDiorite.getOwner() == this.getOwner()) return;
            }
            // Do not damage villagers.
            if (target instanceof AbstractVillager) return;

            // Deal damage to the enemy.
            this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0f, 1.0f);
            target.hurt(damageSources().mobAttack(this), getAttackDamage());
            this.doEnchantDamageEffects(this, target);
            target.setDeltaMovement(target.getDeltaMovement().scale(2));
            if (target instanceof LivingEntity livingTarget)
                livingTarget.addEffect(new MobEffectInstance(stunEffect));
            return;
        }
        super.push(target);
    }

    @Override
    public boolean isPushable()
    {
        return getAttackState() == 0 || getAttackState() == 2;
    }

    @NotNull
    public InteractionResult mobInteract(@NotNull Player pPlayer, @NotNull InteractionHand pHand)
    {
        if (!this.getPassengers().isEmpty()) return super.mobInteract(pPlayer, pHand);
        if (this.getOwner() != pPlayer) return super.mobInteract(pPlayer, pHand);
        this.doPlayerRide(pPlayer);
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    protected void doPlayerRide(Player pPlayer)
    {
        if (!this.level().isClientSide) {
            pPlayer.setYRot(this.getYRot());
            pPlayer.setXRot(this.getXRot());
            pPlayer.startRiding(this);
            this.setDandoriState(DANDORI_STATES.OFF.ordinal());
        }
    }

    @Override
    public void travel(Vec3 movementInput) {
        this.movingBackwards = movementInput.z < 0;
        if (getAttackState() != 0) movementInput = Vec3.ZERO;
        super.travel(new Vec3(0,0, movementInput.z));
    }

    protected void tickRidden(@NotNull Player pPlayer, @NotNull Vec3 pTravelVector)
    {
        super.tickRidden(pPlayer, pTravelVector);
        if (this.isControlledByLocalInstance())
        {
            float newRotation = this.getYRot() - pPlayer.xxa * 10;
            this.setRot(newRotation, 0.0f);
            this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
        }
    }

    protected float getRiddenSpeed(@NotNull Player pPlayer) {
        return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    @Override
    public LivingEntity getControllingPassenger() {
        Entity entity = this.getFirstPassenger();
        if (entity instanceof Mob)
        {
            Mob mobEntity = (Mob)entity;
            return mobEntity;
        }
        if ((entity = this.getFirstPassenger()) instanceof Player)
        {
            Player playerEntity = (Player)entity;
            return playerEntity;
        }
        return null;
    }

    @NotNull
    protected Vec3 getRiddenInput(Player pPlayer, @NotNull Vec3 pTravelVector)
    {
        float f = pPlayer.xxa;
        float g = pPlayer.zza * 0.5f;
        return new Vec3(f, 0.0f, g);
    }

    protected void positionRider(@NotNull Entity pPassenger, Entity.@NotNull MoveFunction pCallback)
    {
        super.positionRider(pPassenger, pCallback);
        pCallback.accept(pPassenger, this.getX(), this.getY() + this.getPassengersRidingOffset() + pPassenger.getMyRidingOffset(), this.getZ());
        if (pPassenger instanceof LivingEntity livingPassenger) livingPassenger.setYBodyRot(this.getYRot());
    }

    public void forceAttack()
    {
        if (this.attackGoal == null) return;
        if (this.getAttackState() == 0)
            this.attackGoal.forceAttack();
    }

    public double getPassengersRidingOffset() {
        return this.getBbHeight() * 1.25d;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, event ->
        {
            EntityGolemGrindstone pGolem = event.getAnimatable();
            if (pGolem.getAttackState() > 0)
            {
                if (pGolem.getAttackState() == 1)
                {
                    event.getController().setAnimationSpeed(1.00);
                    return event.setAndContinue(RawAnimation.begin().then("animation.golem_grindstone.attack_windup", Animation.LoopType.HOLD_ON_LAST_FRAME));
                }
                else if (pGolem.getAttackState() == 2)
                {
                    event.getController().setAnimationSpeed(2.00);
                    return event.setAndContinue(RawAnimation.begin().then("animation.golem_grindstone.attack", Animation.LoopType.LOOP));
                }
                event.getController().setAnimationSpeed(1.00);
                return event.setAndContinue(RawAnimation.begin().then("animation.golem_grindstone.attack_end", Animation.LoopType.HOLD_ON_LAST_FRAME));
            }
            else
            {
                event.getController().setAnimationSpeed(1.00);
                pGolem.setAttackState(0);
                boolean hasPassengers = !pGolem.getPassengers().isEmpty();
                if (pGolem.getDeltaMovement().horizontalDistanceSqr() > 0.001D || event.isMoving())
                {
                    if (hasPassengers)
                    {
                        if (pGolem.movingBackwards)
                        {
                            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.golem_grindstone.carry_backwards"));
                        }
                        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.golem_grindstone.carry"));
                    }
                    return event.setAndContinue(RawAnimation.begin().thenLoop("animation.golem_grindstone.walk"));
                }
                else if (hasPassengers)
                    return event.setAndContinue(RawAnimation.begin().thenLoop("animation.golem_grindstone.carry_idle"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.golem_grindstone.idle"));
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }
}
