package com.kitisplode.golemfirststonemod.entity.entity.golem.first.diorite_pawn;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemPlank;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDelayedMeleeAttack;
import com.kitisplode.golemfirststonemod.entity.entity.projectile.EntityProjectileAoEOwnerAware;
import com.kitisplode.golemfirststonemod.entity.entity.projectile.EntityProjectileDioriteKnowledge;
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

public class EntityPawnDioriteKnowledge extends EntityGolemPlank implements GeoEntity, IEntityWithDelayedMeleeAttack, IEntityDandoriFollower
{
    private static final Identifier MODEL = new Identifier(GolemFirstStoneMod.MOD_ID, "geo/diorite_knowledge.geo.json");
    private static final Identifier TEXTURE = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/pawn/diorite/diorite_knowledge.png");
    public static final Identifier GLOW_TEXTURE = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/pawn/diorite/diorite_knowledge_glowmask.png");
    private static final Identifier ANIMATIONS = new Identifier(GolemFirstStoneMod.MOD_ID, "animations/diorite_knowledge.animation.json");

    private LivingEntity owner;

    public EntityPawnDioriteKnowledge(EntityType<? extends IronGolemEntity> pEntityType, World pLevel)
    {
        super(pEntityType, pLevel);
    }

    public static DefaultAttributeContainer.Builder setAttributes()
    {
        return GolemEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 15.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5f)
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
        }
        if (this.getTarget() != null)
        {
            if (this.getTarget() instanceof MobEntity mob && mob.getTarget() == this.getOwner())
            {
                mob.setTarget(this);
            }
        }
    }

    @Override
    protected EntityProjectileAoEOwnerAware createProjectile()
    {
        return ModEntities.ENTITY_PROJECTILE_DIORITE_KNOWLEDGE.create(this.getWorld());
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
            EntityGolemPlank pGolem = event.getAnimatable();
            if (pGolem.getAttackState() > 0)
            {
                if (pGolem.getAttackState() == 1)
                {
                    event.getController().setAnimationSpeed(2.00);
                    return event.setAndContinue(RawAnimation.begin().then("animation.diorite_knowledge.attack_windup", Animation.LoopType.HOLD_ON_LAST_FRAME));
                }
                event.getController().setAnimationSpeed(2.00);
                return event.setAndContinue(RawAnimation.begin().then("animation.diorite_knowledge.attack", Animation.LoopType.HOLD_ON_LAST_FRAME));
            }
            else
            {
                event.getController().setAnimationSpeed(1.00);
                pGolem.setAttackState(0);
                if (getVelocity().horizontalLengthSquared() > 0.001D || event.isMoving())
                    return event.setAndContinue(RawAnimation.begin().thenLoop("animation.diorite_knowledge.walk"));
            }
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.diorite_knowledge.idle"));
        }));
    }

}
