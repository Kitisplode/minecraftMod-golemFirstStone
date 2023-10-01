package com.kitisplode.golemfirststonemod.entity.entity.golem.dungeons;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.entity.entity.golem.AbstractGolemDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemCobble;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriFollowHardGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriFollowSoftGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriMoveToDeployPositionGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.PassiveAvoidEntityGoal;
import com.kitisplode.golemfirststonemod.item.ModItems;
import com.kitisplode.golemfirststonemod.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

public class EntityGolemKey extends AbstractGolemDandoriFollower implements IEntityDandoriFollower, GeoEntity
{
    private static final ResourceLocation MODEL = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "geo/item/golem_key.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/dungeons/golem_key.png");
    private static final ResourceLocation TEXTURE_SCARED = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/dungeons/golem_key_scared.png");
    private static final ResourceLocation ANIMATIONS = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "animations/item/golem_key.animation.json");
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final RawAnimation ANIMATION_IDLE = RawAnimation.begin().thenLoop("animation.golem_key.idle");
    private static final RawAnimation ANIMATION_WALK = RawAnimation.begin().thenLoop("animation.golem_key.walk");

    private static final EntityDataAccessor<Boolean> SCARED = SynchedEntityData.defineId(EntityGolemCobble.class, EntityDataSerializers.BOOLEAN);

    public EntityGolemKey(EntityType<? extends IronGolem> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.25f)
                .add(Attributes.ATTACK_DAMAGE, 1.0f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.25f);
    }

    public static AttributeSupplier setAttributes()
    {
        return createAttributes().build();
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        if (!this.entityData.hasItem(SCARED)) this.entityData.define(SCARED, false);
    }
    public boolean getScared()
    {
        return this.entityData.get(SCARED);
    }
    public void setScared(boolean pBoolean)
    {
        this.entityData.set(SCARED, pBoolean);
    }

    @Override
    public double getEyeY()
    {
        return getY() + 0.5f;
    }

    @Override
    public boolean isThrowable()
    {
        return true;
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(0, new DandoriFollowHardGoal(this, 1.2,2, 6));
        this.goalSelector.addGoal(1, new DandoriFollowSoftGoal(this, 1.2, 2, 4));

        this.goalSelector.addGoal(2, new PassiveAvoidEntityGoal<>(this, Player.class, 8.0F, 1.5D, 2.0D, (player) -> this.getOwner() != player));
        this.goalSelector.addGoal(2, new KeyGolemPanicGoal(this, 1.5D));
        this.goalSelector.addGoal(3, new DandoriMoveToDeployPositionGoal(this, 2.0f, 1.0f));

        this.goalSelector.addGoal(4, new DandoriFollowSoftGoal(this, 1.2, 2, 0));

        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    protected void updateDeployPosition()
    {
        if (this.getDeployPosition() == null && this.isDandoriOff()) this.setDeployPosition(this.getOnPos());
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand)
    {
        if (this.interactIsPlayerHoldingDandoriCall(pPlayer)) return InteractionResult.PASS;
        ItemStack playerItem = pPlayer.getItemInHand(pHand);
        if (playerItem.isEmpty())
        {
            ItemStack golemItem = this.makeItem();
            this.level().playSound(this, this.blockPosition(), ModSounds.ENTITY_GOLEM_KEY_PICKUP.get(), SoundSource.NEUTRAL, 1.0f, this.level().getRandom().nextFloat() * 0.4F + 0.8F);
            pPlayer.setItemInHand(pHand, golemItem);
            this.remove(RemovalReason.DISCARDED);
        }
        return InteractionResult.PASS;
    }

    private ItemStack makeItem()
    {
        ItemStack golemItem = new ItemStack(ModItems.ITEM_GOLEM_KEY.get());
        if (this.hasCustomName()) golemItem.setHoverName(this.getCustomName());
        CompoundTag tag = golemItem.getOrCreateTag();
        if (this.getDeployPosition() != null) tag.put("DeployPos", NbtUtils.writeBlockPos(this.getDeployPosition()));
        if (this.getOwnerUUID() != null) tag.putUUID("Owner", this.getOwnerUUID());
        return golemItem;
    }

    @Override
    public void remove(Entity.RemovalReason pReason)
    {
        if (pReason == RemovalReason.KILLED && this.getDeployPosition() != null)
        {
            // respawn it at its deploy position
            EntityGolemKey newGolem = ModEntities.ENTITY_GOLEM_KEY.get().create(this.level());
            if (newGolem != null)
            {
                newGolem.setOwner(this.getOwner());
                newGolem.setPos(this.getDeployPosition().above().getCenter());
                this.level().addFreshEntity(newGolem);

                AreaEffectCloud dust = new AreaEffectCloud(level(), newGolem.getX(), newGolem.getY(), newGolem.getZ());
                dust.setParticle(ParticleTypes.POOF);
                dust.setRadius(1.0f);
                dust.setDuration(1);
                dust.setPos(dust.getX(), dust.getY(), dust.getZ());
                level().addFreshEntity(dust);
            }
        }
        super.remove(pReason);
    }

    public ResourceLocation getModelLocation()
    {
        return MODEL;
    }
    public ResourceLocation getTextureLocation()
    {
        if (this.getScared()) return TEXTURE_SCARED;
        else return TEXTURE;
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
            event.getController().setAnimationSpeed(1.00);
            if (getDeltaMovement().horizontalDistanceSqr() > 0.001D || event.isMoving())
                return event.setAndContinue(ANIMATION_WALK);
        return event.setAndContinue(ANIMATION_IDLE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }

    //==================================================================================================================
    private class KeyGolemPanicGoal extends PanicGoal
    {
        public KeyGolemPanicGoal(PathfinderMob pMob, double pSpeedModifier)
        {
            super(pMob, pSpeedModifier);
        }

        @Override
        public void start()
        {
            super.start();
            if (this.mob instanceof EntityGolemKey keyGolem)
            {
                keyGolem.setScared(true);
            }
        }
        @Override
        public void stop()
        {
            super.stop();
            if (this.mob instanceof EntityGolemKey keyGolem)
            {
                keyGolem.setScared(false);
            }
        }
    }
}
