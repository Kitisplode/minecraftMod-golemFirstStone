package com.kitisplode.golemfirststonemod.entity.entity.golem.first.diorite_pawn;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemCobble;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDelayedMeleeAttack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

public class EntityPawnDioriteAction extends EntityGolemCobble implements GeoEntity, IEntityWithDelayedMeleeAttack, IEntityDandoriFollower
{
    public static final Identifier MODEL = new Identifier(GolemFirstStoneMod.MOD_ID, "geo/diorite_action.geo.json");
    public static final Identifier TEXTURE = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/pawn/diorite/diorite_action.png");
    public static final Identifier GLOW_TEXTURE = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/pawn/diorite/diorite_action_glowmask.png");
    public static final Identifier ANIMATIONS = new Identifier(GolemFirstStoneMod.MOD_ID, "animations/diorite_action.animation.json");

    private LivingEntity owner;

    public EntityPawnDioriteAction(EntityType<? extends IronGolemEntity> pEntityType, World pLevel)
    {
        super(pEntityType, pLevel);
    }

    public static DefaultAttributeContainer.Builder setAttributes()
    {
        return GolemEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 30.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.5f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.75f)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 12);
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
    public void tick()
    {
        super.tick();
        if (!this.getWorld().isClient())
        {
            if (this.age > 5 && this.getOwner() == null) discard();
            this.setDandoriState(DANDORI_STATES.SOFT.ordinal());

            if (this.getTarget() != null)
            {
                if (this.getTarget() instanceof MobEntity mob && mob.getTarget() == this.getOwner())
                {
                    mob.setTarget(this);
                }
            }
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
            EntityPawnDioriteAction pGolem = event.getAnimatable();
            if (pGolem.getAttackState() > 0)
            {
                if (pGolem.getAttackState() == 1)
                {
                    event.getController().setAnimationSpeed(4.00);
                    if (!this.getLeftArm()) return event.setAndContinue(RawAnimation.begin().then("animation.diorite_action_2.attack_windup_right", Animation.LoopType.HOLD_ON_LAST_FRAME));
                    return event.setAndContinue(RawAnimation.begin().then("animation.diorite_action_2.attack_windup_left", Animation.LoopType.HOLD_ON_LAST_FRAME));
                }
                event.getController().setAnimationSpeed(4.00);
                if (!this.getLeftArm()) return event.setAndContinue(RawAnimation.begin().then("animation.diorite_action_2.attack_right", Animation.LoopType.HOLD_ON_LAST_FRAME));
                return event.setAndContinue(RawAnimation.begin().then("animation.diorite_action_2.attack_left", Animation.LoopType.HOLD_ON_LAST_FRAME));
            }
            else
            {
                event.getController().setAnimationSpeed(1.00);
                pGolem.setAttackState(0);
                if (getVelocity().horizontalLengthSquared() > 0.001D || event.isMoving())
                    return event.setAndContinue(RawAnimation.begin().thenLoop("animation.diorite_action_2.walk"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.diorite_action_2.idle"));
        }));
    }
}
