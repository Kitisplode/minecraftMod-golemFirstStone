package com.kitisplode.golemfirststonemod.entity.entity.golem.vote;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.block.ModBlocks;
import com.kitisplode.golemfirststonemod.entity.entity.golem.AbstractGolemDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityCanAttackBlocks;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDelayedMeleeAttack;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriFollowHardGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriFollowSoftGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriMoveToDeployPositionGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.MultiStageAttackBlockGoalRanged;
import com.kitisplode.golemfirststonemod.entity.goal.target.BlockTargetGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.function.Predicate;

public class EntityGolemCopper extends AbstractGolemDandoriFollower implements GeoEntity, IEntityWithDelayedMeleeAttack, IEntityDandoriFollower, IEntityCanAttackBlocks
{
    private static final ResourceLocation MODEL = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "geo/golem_copper.geo.json");
    private static final ResourceLocation TEXTURE = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/vote/copper/golem_copper.png");
    private static final ResourceLocation TEXTURE_EXPOSED = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/vote/copper/golem_copper_exposed.png");
    private static final ResourceLocation TEXTURE_WEATHERED = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/vote/copper/golem_copper_weathered.png");
    private static final ResourceLocation TEXTURE_OXIDIZED = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/vote/copper/golem_copper_oxidized.png");
    private static final ResourceLocation ANIMATIONS = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "animations/entity/golem/vote/golem_copper.animation.json");

    private static final RawAnimation ANIMATION_ATTACK_WINDUP = RawAnimation.begin().then("animation.golem_copper.attack_windup", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation ANIMATION_ATTACK = RawAnimation.begin().then("animation.golem_copper.attack", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation ANIMATION_ATTACK_END = RawAnimation.begin().then("animation.golem_copper.attack_end", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation ANIMATION_WALK = RawAnimation.begin().thenLoop("animation.golem_copper.walk");
    private static final RawAnimation ANIMATION_WALK_EXPOSED = RawAnimation.begin().thenLoop("animation.golem_copper.walk_exposed");
    private static final RawAnimation ANIMATION_WALK_WEATHERED = RawAnimation.begin().thenLoop("animation.golem_copper.walk_weathered");
    private static final RawAnimation ANIMATION_IDLE = RawAnimation.begin().thenLoop("animation.golem_copper.idle");

    private static final EntityDataAccessor<Integer> ATTACK_STATE = SynchedEntityData.defineId(EntityGolemCopper.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> OXIDATION = SynchedEntityData.defineId(EntityGolemCopper.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> WAXED = SynchedEntityData.defineId(EntityGolemCopper.class, EntityDataSerializers.BOOLEAN);
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    protected static final double dandoriMoveRange = 3;

    private int nextOxidationCounter = 0;
    private static final int nextOxidationCount = 100;
    private static final int oxidationChance = 5;

    private BlockPos blockTarget;
    private static final Predicate<BlockState> bsPredicate = blockState -> blockState != null
            && ((blockState.is(BlockTags.BUTTONS)));

    public EntityGolemCopper(EntityType<? extends IronGolem> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 35.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.3f)
                .add(Attributes.ATTACK_DAMAGE, 2.5f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.25f)
                .add(Attributes.FOLLOW_RANGE, 24);
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
        if (!this.entityData.hasItem(OXIDATION)) this.entityData.define(OXIDATION, 0);
        if (!this.entityData.hasItem(WAXED)) this.entityData.define(WAXED, false);
    }
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Oxidation", this.getOxidation());
        pCompound.putBoolean("Waxed", this.getWaxed());
    }
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);;
        if (pCompound.contains("Oxidation")) this.setOxidation(pCompound.getInt("Oxidation"));
        if (pCompound.contains("Waxed")) this.setWaxed(pCompound.getBoolean("Waxed"));
    }
    public int getAttackState()
    {
        return this.entityData.get(ATTACK_STATE);
    }
    public void setAttackState(int pInt)
    {
        this.entityData.set(ATTACK_STATE, pInt);
    }
    public int getOxidation()
    {
        return this.entityData.get(OXIDATION);
    }
    public void setOxidation(int pInt)
    {
        this.entityData.set(OXIDATION, pInt);
    }
    public boolean getWaxed()
    {
        return this.entityData.get(WAXED);
    }
    public void setWaxed(boolean pBoolean)
    {
        this.entityData.set(WAXED, pBoolean);
    }

    @Override
    public double getEyeY()
    {
        return getY() + 0.7f;
    }
    @Override
    public int getMaxHeadXRot()
    {
        int oxidation = this.getOxidation();
        if (oxidation == 3) return 0;
        return super.getMaxHeadXRot();
    }
    @Override
    public int getMaxHeadYRot()
    {
        int oxidation = this.getOxidation();
        if (oxidation == 3) return 0;
        return super.getMaxHeadYRot();
    }
    @Override
    public boolean isImmobile()
    {
        int oxidation = this.getOxidation();
        if (oxidation == 3) return true;
        return super.isImmobile();
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(0, new DandoriFollowHardGoal(this, 1.2, dandoriMoveRange, dandoriSeeRange));
        this.goalSelector.addGoal(1, new DandoriFollowSoftGoal(this, 1.2, dandoriMoveRange, 6));

        this.goalSelector.addGoal(2, new DandoriMoveToDeployPositionGoal(this, 2.0f, 1.0f));
        this.goalSelector.addGoal(3, new MultiStageAttackBlockGoalRanged(this, 1.0, true, 9.0D, new int[]{40, 25, 10}));
        this.goalSelector.addGoal(4, new DandoriFollowSoftGoal(this, 1.2, dandoriMoveRange, 0));

        this.goalSelector.addGoal(5, new CopperGolemAvoidEntityGoal<>(this, Monster.class, 16, 0.9D, 1));
        this.goalSelector.addGoal(5, new CopperGolemPanicGoal(this, 1.0D));

        this.goalSelector.addGoal(6, new CopperGolemRandomStrollInVillageGoal(this, 0.8D));
        this.goalSelector.addGoal(7, new CopperGolemLookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new CopperGolemLookAtPlayerGoal(this, AbstractVillager.class, 6.0F));
        this.goalSelector.addGoal(8, new CopperGolemRandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new BlockTargetGoal(this, 16, 75, true, true, bsPredicate, true));
    }

    @Override
    public void tick()
    {
        super.tick();
        int oxidation = this.getOxidation();
        if (oxidation < 3 && !this.getWaxed())
        {
            if (this.random.nextInt(100) < oxidationChance)
                this.nextOxidationCounter++;
            if (this.isInWaterOrRain()) this.nextOxidationCounter++;
            if (this.nextOxidationCounter >= nextOxidationCount)
            {
                this.nextOxidationCounter = 0;
                this.setOxidation(oxidation + 1);
            }
        }

        oxidation = this.getOxidation();
        float speed = 1.0f;
        if (oxidation == 1) speed = 0.75f;
        if (oxidation == 2) speed = 0.5f;
        if (oxidation == 3 || this.isImmobile()) speed = 0.0f;
        this.xxa *= speed;
        this.zza *= speed;
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
    public boolean tryAttack()
    {
       return false;
    }
    @Override
    public boolean tryAttackBlock()
    {
        if (getAttackState() != 2) return false;
        BlockPos bt = getBlockTarget();
        if (bt != null)
        {
            BlockState bs = this.level().getBlockState(bt);
            Block block = bs.getBlock();
            if (block instanceof ButtonBlock button) button.press(bs, this.level(), bt);
            setBlockTarget(null);
        }
        return true;
    }
    public int blockPreference(BlockState bs)
    {
        if (bs == null) return 100;
        if (bs.is(ModBlocks.BLOCK_BUTTON_COPPER.get())) return 75;
        if (bs.is(BlockTags.STONE_BUTTONS)) return 50;
        if (bs.is(BlockTags.WOODEN_BUTTONS)) return 25;
        return 0;
    }
    @Override
    public void setBlockTarget(BlockPos pBlockPos)
    {
        if (pBlockPos != null && pBlockPos.getY() <= this.level().getMinBuildHeight()) return;
        blockTarget = pBlockPos;
    }
    @Override
    public BlockPos getBlockTarget()
    {
        return blockTarget;
    }
    @Override
    public boolean canTargetBlock(BlockPos pBlockPos)
    {
        if (pBlockPos == null) return false;
        if (pBlockPos.getY() <= this.level().getMinBuildHeight()) return false;
        return bsPredicate.test(this.level().getBlockState(pBlockPos));
    }

    @Override
    public boolean isPushable()
    {
        return getAttackState() == 0;
    }
    @Override
    protected InteractionResult mobInteract(@NotNull Player pPlayer, @NotNull InteractionHand pHand)
    {
        ItemStack playerItem = pPlayer.getItemInHand(pHand);
        if (playerItem.is(ItemTags.AXES) && pHand == InteractionHand.MAIN_HAND)
        {
            if (this.getWaxed())
            {
                this.level().playSound(pPlayer, this.getOnPos(0.5f), SoundEvents.AXE_WAX_OFF, SoundSource.NEUTRAL, 1.0f, 1.0f);
                this.setWaxed(false);
                ParticleUtils.spawnParticlesOnBlockFaces(this.level(), this.getOnPos(0.5f), ParticleTypes.WAX_OFF, UniformInt.of(3, 5));
            }
            else
            {
                int oxidation = this.getOxidation();
                if (oxidation == 0) return InteractionResult.PASS;
                this.level().playSound(pPlayer, this.getOnPos(), SoundEvents.AXE_SCRAPE, SoundSource.NEUTRAL, 1.0f, 1.0f);
                this.setOxidation(oxidation - 1);
                ParticleUtils.spawnParticlesOnBlockFaces(this.level(), this.getOnPos(0.5f), ParticleTypes.SCRAPE, UniformInt.of(3, 5));
            }
            this.nextOxidationCounter = 0;

            return InteractionResult.SUCCESS;
        }
        if (playerItem.is(Items.HONEYCOMB) && pHand == InteractionHand.MAIN_HAND)
        {
            if (this.getWaxed()) return InteractionResult.PASS;
            this.setWaxed(true);
            this.level().playSound(pPlayer, this.getOnPos(), SoundEvents.HONEYCOMB_WAX_ON, SoundSource.NEUTRAL, 1.0f, 1.0f);
            ParticleUtils.spawnParticlesOnBlockFaces(this.level(), this.getOnPos(), ParticleTypes.WAX_ON, UniformInt.of(3, 5));

            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(pPlayer, pHand);
    }
    @Override
    public boolean hurt(DamageSource source, float amount)
    {
        if (source.is(DamageTypeTags.IS_LIGHTNING)) return false;
        return super.hurt(source, amount);
    }
    public void thunderHit(ServerLevel pLevel, LightningBolt pLightning)
    {
        pLightning.setVisualOnly(true);
        if (!this.getWaxed())
        {
            if (this.getOxidation() == 0) return;
            this.setOxidation(0);
            ParticleUtils.spawnParticlesOnBlockFaces(this.level(), this.getOnPos(), ParticleTypes.SCRAPE, UniformInt.of(3, 5));
        }
    }

    public ResourceLocation getModelLocation()
    {
        return MODEL;
    }
    public ResourceLocation getTextureLocation()
    {
        int oxidation = this.getOxidation();
        if (oxidation == 0) return TEXTURE;
        if (oxidation == 1) return TEXTURE_EXPOSED;
        if (oxidation == 2) return TEXTURE_WEATHERED;
        return TEXTURE_OXIDIZED;
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
            EntityGolemCopper pGolem = event.getAnimatable();
            int oxidation = pGolem.getOxidation();
            if (oxidation == 3)
            {
                event.getController().setAnimationSpeed(0.00);
                return null;
            }
            if (pGolem.getAttackState() > 0)
            {
                if (pGolem.getAttackState() == 1)
                {
                    event.getController().setAnimationSpeed(2.00);
                    return event.setAndContinue(ANIMATION_ATTACK_WINDUP);
                }
                if (pGolem.getAttackState() == 2)
                {
                    event.getController().setAnimationSpeed(2.00);
                    return event.setAndContinue(ANIMATION_ATTACK);
                }
                event.getController().setAnimationSpeed(2.00);
                return event.setAndContinue(ANIMATION_ATTACK_END);
            }
            else
            {
                pGolem.setAttackState(0);
                if (pGolem.getDeltaMovement().horizontalDistanceSqr() > 0.001D || event.isMoving())
                {
                    if (oxidation == 0)
                    {
                        event.getController().setAnimationSpeed(1.00);
                        return event.setAndContinue(ANIMATION_WALK);
                    }
                    if (oxidation == 1)
                    {
                        event.getController().setAnimationSpeed(0.75);
                        return event.setAndContinue(ANIMATION_WALK_EXPOSED);
                    }
                    event.getController().setAnimationSpeed(0.50);
                    return event.setAndContinue(ANIMATION_WALK_WEATHERED);
                }
            }
            event.getController().setAnimationSpeed(1.00);
            return event.setAndContinue(ANIMATION_IDLE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }

    class CopperGolemAvoidEntityGoal<T extends LivingEntity> extends AvoidEntityGoal
    {
        private final EntityGolemCopper golem;
        public CopperGolemAvoidEntityGoal(EntityGolemCopper pMob, Class pEntityClassToAvoid, float pMaxDistance, double pWalkSpeedModifier, double pSprintSpeedModifier)
        {
            super(pMob, pEntityClassToAvoid, pMaxDistance, pWalkSpeedModifier, pSprintSpeedModifier);
            golem = pMob;
        }
        public boolean canUse()
        {
            if (this.golem.isImmobile()) return false;
            return super.canUse();
        }
    }
    class CopperGolemPanicGoal extends PanicGoal
    {
        private final EntityGolemCopper golem;
        public CopperGolemPanicGoal(EntityGolemCopper pMob, double pSpeedModifier)
        {
            super(pMob, pSpeedModifier);
            golem = pMob;
        }
        public boolean canUse()
        {
            if (this.golem.isImmobile()) return false;
            return super.canUse();
        }
    }
    class CopperGolemRandomStrollInVillageGoal extends GolemRandomStrollInVillageGoal
    {
        private final EntityGolemCopper golem;
        public CopperGolemRandomStrollInVillageGoal(EntityGolemCopper pMob, double pSpeedModifier)
        {
            super(pMob, pSpeedModifier);
            golem = pMob;
        }
        public boolean canUse()
        {
            if (this.golem.isImmobile()) return false;
            return super.canUse();
        }
    }
    class CopperGolemLookAtPlayerGoal extends LookAtPlayerGoal
    {
        private final EntityGolemCopper golem;
        public CopperGolemLookAtPlayerGoal(EntityGolemCopper pMob, Class<? extends LivingEntity> pLookAtType, float pLookDistance)
        {
            super(pMob, pLookAtType, pLookDistance);
            golem = pMob;
        }
        public boolean canUse()
        {
            if (this.golem.isImmobile()) return false;
            return super.canUse();
        }
    }
    class CopperGolemRandomLookAroundGoal extends RandomLookAroundGoal
    {
        private final EntityGolemCopper golem;
        public CopperGolemRandomLookAroundGoal(EntityGolemCopper pMob)
        {
            super(pMob);
            golem = pMob;
        }
        public boolean canUse()
        {
            if (this.golem.isImmobile()) return false;
            return super.canUse();
        }
    }
}
