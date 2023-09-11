package com.kitisplode.golemfirststonemod.entity.entity.golem.first.diorite_pawns;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemCobble;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDelayedMeleeAttack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;


public class EntityPawnDioriteAction extends EntityGolemCobble implements GeoEntity, IEntityWithDelayedMeleeAttack, IEntityDandoriFollower
{
    private static final ResourceLocation MODEL = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "geo/diorite_action.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/pawn/diorite/diorite_action.png");
    private static final ResourceLocation ANIMATIONS = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "animations/diorite_action.animation.json");

    private LivingEntity owner;

    public EntityPawnDioriteAction(EntityType<? extends IronGolem> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.35f)
                .add(Attributes.ATTACK_DAMAGE, 1.5f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75f)
                .add(Attributes.FOLLOW_RANGE, 12);
    }
    public static AttributeSupplier setAttributes()
    {
        return createAttributes().build();
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
        if (!this.level().isClientSide())
        {
            if (this.tickCount > 20 && this.getOwner() == null) discard();
            this.setDandoriState(DANDORI_STATES.SOFT.ordinal());
        }
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
                if (getDeltaMovement().horizontalDistanceSqr() > 0.001D || event.isMoving())
                    return event.setAndContinue(RawAnimation.begin().thenLoop("animation.diorite_action_2.walk"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.diorite_action_2.idle"));
        }));
    }
}
