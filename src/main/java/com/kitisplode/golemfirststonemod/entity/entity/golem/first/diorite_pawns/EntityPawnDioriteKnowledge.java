package com.kitisplode.golemfirststonemod.entity.entity.golem.first.diorite_pawns;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemPlank;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDelayedMeleeAttack;
import com.kitisplode.golemfirststonemod.entity.entity.projectile.EntityProjectileAoEOwnerAware;
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

public class EntityPawnDioriteKnowledge extends EntityGolemPlank implements GeoEntity, IEntityWithDelayedMeleeAttack, IEntityDandoriFollower
{
    private static final ResourceLocation MODEL = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "geo/diorite_knowledge.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/pawn/diorite/diorite_knowledge.png");
    public static final ResourceLocation GLOW_TEXTURE = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/pawn/diorite/diorite_knowledge_glowmask.png");
    private static final ResourceLocation ANIMATIONS = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "animations/entity/golem/first/pawn/diorite_knowledge.animation.json");

    private static final RawAnimation ANIMATION_ATTACK_WINDUP = RawAnimation.begin().then("animation.diorite_knowledge.attack_windup", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation ANIMATION_ATTACK = RawAnimation.begin().then("animation.diorite_knowledge.attack", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation ANIMATION_WALK = RawAnimation.begin().thenLoop("animation.diorite_knowledge.walk");
    private static final RawAnimation ANIMATION_IDLE = RawAnimation.begin().thenLoop("animation.diorite_knowledge.idle");

    private LivingEntity owner;

    public EntityPawnDioriteKnowledge(EntityType<? extends IronGolem> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 15.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.35f)
                .add(Attributes.ATTACK_DAMAGE, 1.0f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5f)
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
            if (this.tickCount > 5 && this.getOwner() == null) discard();
            this.setDandoriState(DANDORI_STATES.SOFT.ordinal());
        }
        if (this.getTarget() != null)
        {
            if (this.getTarget() instanceof Mob mob && mob.getTarget() == this.getOwner())
            {
                mob.setTarget(this);
            }
        }
    }

    protected EntityProjectileAoEOwnerAware createProjectile()
    {
        return ModEntities.ENTITY_PROJECTILE_DIORITE_KNOWLEDGE.get().create(this.level());
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
}
