package com.kitisplode.golemfirststonemod.entity.entity.golem;

import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.projectile.EntityProjectileFirstOak;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDelayedMeleeAttack;
import com.kitisplode.golemfirststonemod.entity.goal.goal.DandoriFollowGoal;
import com.kitisplode.golemfirststonemod.entity.goal.target.ActiveTargetGoalBiggerY;
import com.kitisplode.golemfirststonemod.entity.goal.goal.MultiStageAttackGoalRanged;
import com.kitisplode.golemfirststonemod.item.ModItems;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;


public class EntityGolemFirstOak extends AbstractGolemDandoriFollower implements GeoEntity, IEntityWithDelayedMeleeAttack, IEntityDandoriFollower
{
    private static final EntityDataAccessor<Integer> ATTACK_STATE = SynchedEntityData.defineId(EntityGolemFirstOak.class, EntityDataSerializers.INT);
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final float attackAOERange = 4.0f;
    private static final float projectileSpeed = 2.0f;
    private static final double dandoriMoveRange = 6;
    private static final double dandoriSeeRange = 36;

    public EntityGolemFirstOak(EntityType<? extends IronGolem> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 500.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.25f)
                .add(Attributes.ATTACK_DAMAGE, 10.0f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0f)
                .add(Attributes.FOLLOW_RANGE, 32);
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

    private float getAttackDamage() {
        return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
    }

    @Override
    public double getEyeY()
    {
        return getY() + 2.2d;
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(1, new DandoriFollowGoal(this, 1.0, Ingredient.of(ModItems.ITEM_DANDORI_CALL.get(), ModItems.ITEM_DANDORI_ATTACK.get()), dandoriMoveRange, dandoriSeeRange));
        this.goalSelector.addGoal(2, new MultiStageAttackGoalRanged(this, 1.0, true, 1024.0, new int[]{40, 18, 13}, 0));
        this.goalSelector.addGoal(3, new MoveTowardsTargetGoal(this, 0.8D, 48.0F));
        this.goalSelector.addGoal(4, new GolemRandomStrollInVillageGoal(this, 0.8D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new ActiveTargetGoalBiggerY<>(this, Mob.class, 5, true, false, entity -> entity instanceof Enemy, 32));

    }

    @Override
    public boolean tryAttack()
    {
        if (getAttackState() != 2) return false;

        // If we still don't have a target, maybe we shouldn't do anything? lol
        if (this.getTarget() == null) return false;

        this.level().broadcastEntityEvent(this, (byte)4);
        this.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0F, 1.0F);
        attackDust();
        attack();
        return true;
    }

    private void attackDust()
    {
//        AreaEffectCloud dust = new AreaEffectCloud(level(), getX(),getY(),getZ());
//        dust.setParticle(ParticleTypes.SMOKE);
//        dust.setRadius(5.0f);
//        dust.setDuration(1);
//        dust.setPos(getX(),getY(),getZ());
//        level().addFreshEntity(dust);
    }

    private void attack()
    {
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) return;

        // Spawn the projectile.
        if (!this.level().isClientSide())
        {
            EntityProjectileFirstOak arrow = new EntityProjectileFirstOak(this.level(), this, attackAOERange, getAttackDamage());
            Vec3 shootingVelocity = target.getEyePosition().subtract(this.getEyePosition()).normalize().scale(projectileSpeed);
            arrow.setDeltaMovement(shootingVelocity);
            arrow.tickCount = 35;
            arrow.setBaseDamage(getAttackDamage());
            arrow.setNoGravity(true);
            this.level().addFreshEntity(arrow);
        }
    }

    @Override
    protected InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (!itemstack.is(Items.OAK_WOOD)) {
            return InteractionResult.PASS;
        } else {
            float f = this.getHealth();
            this.heal(25.0F);
            if (this.getHealth() == f) {
                return InteractionResult.PASS;
            } else {
                float f1 = 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F;
                this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, f1);
                if (!pPlayer.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, event ->
        {
            EntityGolemFirstOak pGolem = event.getAnimatable();
            if (pGolem.getAttackState() > 0)
            {
                switch (pGolem.getAttackState())
                {
                    case 1:
                        event.getController().setAnimationSpeed(0.5);
                        return event.setAndContinue(RawAnimation.begin().then("animation.first_oak.attack_windup", Animation.LoopType.HOLD_ON_LAST_FRAME));
                    case 2:
                        event.getController().setAnimationSpeed(1.00);
                        return event.setAndContinue(RawAnimation.begin().then("animation.first_oak.attack", Animation.LoopType.HOLD_ON_LAST_FRAME));
                    default:
                        event.getController().setAnimationSpeed(1.00);
                        return event.setAndContinue(RawAnimation.begin().then("animation.first_oak.attack_end", Animation.LoopType.HOLD_ON_LAST_FRAME));
                }
            }
            else
            {
                event.getController().setAnimationSpeed(1.00);
                pGolem.setAttackState(0);
                if (getDeltaMovement().horizontalDistanceSqr() > 0.001D || event.isMoving())
                    return event.setAndContinue(RawAnimation.begin().thenLoop("animation.first_oak.walk"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.first_oak.idle"));
        }));
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }
}
