package com.kitisplode.golemfirststonemod.entity.custom;

import com.kitisplode.golemfirststonemod.entity.goal.MultiStageAttackGoal;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.DefendVillageTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.List;


public class EntityGolemFirstStone extends IronGolem implements GeoEntity, IEntityWithDelayedMeleeAttack
{
    private static final EntityDataAccessor<Integer> ATTACK_STATE = SynchedEntityData.defineId(EntityGolemFirstStone.class, EntityDataSerializers.INT);
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final float attackAOERange = 4.0f;
    private final float attackKnockbackAmount = 2.0f;
    private final float attackKnockbackAmountVertical = 0.25f;
    private final float attackVerticalRange = 1.5f;

    public EntityGolemFirstStone(EntityType<? extends IronGolem> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier setAttributes()
    {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25f)
                .add(Attributes.ATTACK_DAMAGE, 30.0f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0f)
                .build();
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(ATTACK_STATE, 0);
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
    protected void registerGoals()
    {
        this.goalSelector.addGoal(1, new MultiStageAttackGoal(this, 1.0, true, 6.0D, new int[]{70, 30, 25}));
        this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.3D, 32.0F));
        this.goalSelector.addGoal(2, new MoveBackToVillageGoal(this, 0.2D, false));
        this.goalSelector.addGoal(4, new GolemRandomStrollInVillageGoal(this, 0.2D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new DefendVillageTargetGoal(this));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 5, false, false, (p_28879_) -> {
            return p_28879_ instanceof Enemy && !(p_28879_ instanceof Creeper);
        }));
        this.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal<>(this, false));
    }

    @Override
    public boolean tryAttack()
    {
        if (getAttackState() != 3) return false;

        this.level().broadcastEntityEvent(this, (byte)4);
        this.playSound(SoundEvents.GENERIC_EXPLODE, 1.0F, 1.0F);
        attackDust();
        attackAOE();
        return true;
    }

    private void attackDust()
    {
        AreaEffectCloud dust = new AreaEffectCloud(level(), getX(),getY(),getZ());
        dust.setParticle(ParticleTypes.SMOKE);
        dust.setRadius(5.0f);
        dust.setDuration(1);
        dust.setPos(getX(),getY(),getZ());
        level().addFreshEntity(dust);
    }

    private void attackAOE()
    {
        List<LivingEntity> targetList = level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(attackAOERange));
        for (LivingEntity target : targetList)
        {
            // Do not damage ourselves.
            if (target == this) continue;
            // Do not damage targets that are villagers or golems.
            if (target instanceof AbstractVillager) continue;
            if (target instanceof Merchant) continue;
            if (target instanceof AbstractGolem) continue;
            // Do not damage players if the golem is player made.
            if (target instanceof Player && isPlayerCreated()) continue;
            // Do not damage targets that are too far on the y axis.
            if (Math.abs(getY() - target.getY()) > attackVerticalRange) continue;

            // Apply damage.
            float forceMultiplier = Math.abs((attackAOERange - this.distanceTo(target)) / attackAOERange);
            float totalDamage = getAttackDamage() * forceMultiplier;
            target.hurt(this.damageSources().mobAttack(this), totalDamage);
            // Apply knockback.
            double knockbackResistance = Math.max(0.0, 1.0 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            double knockbackForce = knockbackResistance * attackKnockbackAmount;
            Vec3 knockbackDirection = target.position().subtract(position()).normalize().add(0,attackKnockbackAmountVertical,0);
            target.setDeltaMovement(target.getDeltaMovement().add(knockbackDirection.scale(knockbackForce)));
        }
    }

    @Override
    protected InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (!itemstack.is(Items.STONE)) {
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
    public boolean isPushable()
    {
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, event ->
        {
            EntityGolemFirstStone pGolem = event.getAnimatable();
            if (pGolem.getAttackState() > 0)
            {
                switch (pGolem.getAttackState())
                {
                    case 1:
                        event.getController().setAnimationSpeed(0.5);
                        return event.setAndContinue(RawAnimation.begin().then("animation.first_stone.attack_windup", Animation.LoopType.HOLD_ON_LAST_FRAME));
                    case 2:
                        event.getController().setAnimationSpeed(1.00);
                        return event.setAndContinue(RawAnimation.begin().then("animation.first_stone.attack", Animation.LoopType.HOLD_ON_LAST_FRAME));
                    default:
                        event.getController().setAnimationSpeed(1.00);
                        return event.setAndContinue(RawAnimation.begin().then("animation.first_stone.attack_end", Animation.LoopType.HOLD_ON_LAST_FRAME));
                }
            }
            else
            {
                event.getController().setAnimationSpeed(1.00);
                pGolem.setAttackState(0);
                if (getDeltaMovement().horizontalDistanceSqr() > 0.001D || event.isMoving())
                    return event.setAndContinue(RawAnimation.begin().thenLoop("animation.first_stone.walk"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.first_stone.idle"));
        }));
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }
}
