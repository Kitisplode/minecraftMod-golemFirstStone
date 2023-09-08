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
import com.kitisplode.golemfirststonemod.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ButtonBlock;
import net.minecraft.client.util.ParticleUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.World;
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
    private static final TrackedData<Integer> ATTACK_STATE = DataTracker.registerData(EntityGolemCopper.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> OXIDATION = DataTracker.registerData(EntityGolemCopper.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> WAXED = DataTracker.registerData(EntityGolemCopper.class, TrackedDataHandlerRegistry.BOOLEAN);
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    protected static final double dandoriMoveRange = 3;

    private int nextOxidationCounter = 0;
    private static final int nextOxidationCount = 100;
    private static final int oxidationChance = 5;

    private BlockPos blockTarget;
    private static final Predicate<BlockState> bsPredicate = blockState -> blockState != null
            && ((blockState.isIn(BlockTags.BUTTONS)));

    private static final Identifier TEXTURE = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/vote/copper/golem_copper.png");
    private static final Identifier TEXTURE_EXPOSED = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/vote/copper/golem_copper_exposed.png");
    private static final Identifier TEXTURE_WEATHERED = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/vote/copper/golem_copper_weathered.png");
    private static final Identifier TEXTURE_OXIDIZED = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/vote/copper/golem_copper_oxidized.png");

    public EntityGolemCopper(EntityType<? extends IronGolemEntity> pEntityType, World pLevel)
    {
        super(pEntityType, pLevel);
    }

    public static DefaultAttributeContainer.Builder setAttributes()
    {
        return GolemEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 35.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2.5f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.25f)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 24);
    }

    @Override
    public boolean isThrowable()
    {
        return true;
    }

    @Override
    protected void initDataTracker()
    {
        super.initDataTracker();
        if (!this.dataTracker.containsKey(ATTACK_STATE)) this.dataTracker.startTracking(ATTACK_STATE, 0);
        if (!this.dataTracker.containsKey(OXIDATION)) this.dataTracker.startTracking(OXIDATION, 0);
        if (!this.dataTracker.containsKey(WAXED)) this.dataTracker.startTracking(WAXED, false);
    }
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt)
    {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Oxidation", this.getOxidation());
        nbt.putBoolean("Waxed", this.getWaxed());
    }
    @Override
    public void readCustomDataFromNbt(NbtCompound nbt)
    {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("Oxidation")) this.setOxidation(nbt.getInt("Oxidation"));
        if (nbt.contains("Waxed")) this.setWaxed(nbt.getBoolean("Waxed"));
    }
    public int getAttackState()
    {
        return this.dataTracker.get(ATTACK_STATE);
    }
    public void setAttackState(int pInt)
    {
        this.dataTracker.set(ATTACK_STATE, pInt);
    }
    public int getOxidation()
    {
        return this.dataTracker.get(OXIDATION);
    }
    public void setOxidation(int pInt)
    {
        this.dataTracker.set(OXIDATION, pInt);
    }
    public boolean getWaxed()
    {
        return this.dataTracker.get(WAXED);
    }
    public void setWaxed(boolean pBoolean)
    {
        this.dataTracker.set(WAXED, pBoolean);
    }

    @Override
    public double getEyeY()
    {
        return getY() + 0.7f;
    }

    @Override
    public int getMaxHeadRotation()
    {
        int oxidation = this.getOxidation();
        if (oxidation == 3) return 0;
        return super.getMaxHeadRotation();
    }
    @Override
    public int getMaxLookYawChange()
    {
        int oxidation = this.getOxidation();
        if (oxidation == 3) return 0;
        return super.getMaxLookYawChange();
    }
    public float getMovementSpeed()
    {
        int oxidation = this.getOxidation();
        float speed = (float)this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if (oxidation == 0) return speed;
        if (oxidation == 1) return speed * 0.75f;
        if (oxidation == 2) return speed * 0.5f;
        return 0.0f;
    }
    @Override
    public boolean isImmobile()
    {
        int oxidation = this.getOxidation();
        if (oxidation == 3) return true;
        return super.isImmobile();
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new DandoriFollowHardGoal(this, 1.2, dandoriMoveRange, dandoriSeeRange));
        this.goalSelector.add(2, new DandoriMoveToDeployPositionGoal(this, 2.0f, 1.0f));

        this.goalSelector.add(3, new DandoriFollowSoftGoal(this, 1.2, dandoriMoveRange, dandoriSeeRange));

        this.goalSelector.add(3, new CopperGolemFleeEntityGoal<HostileEntity>(this, HostileEntity.class, 16.0f, 0.9, 1.0));
        this.goalSelector.add(3, new CopperGolemEscapeDangerGoal(this, 1.0));
        this.goalSelector.add(4, new MultiStageAttackBlockGoalRanged(this, 1.0, true, 9.0D, new int[]{40, 25, 10}));
        this.goalSelector.add(5, new CopperGolemWanderAroundGoal(this, 0.8));
        this.goalSelector.add(7, new CopperGolemLookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(7, new CopperGolemLookAtEntityGoal(this, MerchantEntity.class, 6.0F));
        this.goalSelector.add(8, new CopperGolemLookAroundGoal(this));
        this.targetSelector.add(1, new BlockTargetGoal(this, 16, 75, true, true, bsPredicate, true));
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
            if (this.isTouchingWaterOrRain()) this.nextOxidationCounter++;
            if (this.nextOxidationCounter >= nextOxidationCount)
            {
                this.nextOxidationCounter = 0;
                this.setOxidation(oxidation + 1);
            }
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
            BlockState bs = this.getWorld().getBlockState(bt);
            Block block = bs.getBlock();
            if (block instanceof ButtonBlock button) button.powerOn(bs, this.getWorld(), bt);
            setBlockTarget(null);
        }
        return true;
    }
    public int blockPreference(BlockState bs)
    {
        if (bs == null) return 100;
        if (bs.isOf(ModBlocks.BLOCK_BUTTON_COPPER)) return 75;
        if (bs.isIn(BlockTags.STONE_BUTTONS)) return 50;
        if (bs.isIn(BlockTags.WOODEN_BUTTONS)) return 25;
        return 0;
    }
    @Override
    public void setBlockTarget(BlockPos pBlockPos)
    {
        if (pBlockPos != null && pBlockPos.getY() <= this.getWorld().getBottomY()) return;
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
        if (pBlockPos.getY() <= this.getWorld().getBottomY()) return false;
        return bsPredicate.test(this.getWorld().getBlockState(pBlockPos));
    }

    @Override
    public boolean isPushable()
    {
        return getAttackState() == 0;
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand)
    {
        ItemStack playerItem = player.getStackInHand(hand);
        if (playerItem.isIn(ItemTags.AXES) && hand == Hand.MAIN_HAND)
        {
            if (this.getWaxed())
            {
                this.getWorld().playSound(player, this.getBlockPos(), SoundEvents.ITEM_AXE_WAX_OFF, SoundCategory.BLOCKS, 1.0f, 1.0f);
                this.setWaxed(false);
                ParticleUtil.spawnParticle(this.getWorld(), this.getBlockPos(), ParticleTypes.WAX_OFF, UniformIntProvider.create(3, 5));
            }
            else
            {
                int oxidation = this.getOxidation();
                if (oxidation == 0) return ActionResult.PASS;
                this.getWorld().playSound(player, this.getBlockPos(), SoundEvents.ITEM_AXE_SCRAPE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                this.setOxidation(oxidation - 1);
                ParticleUtil.spawnParticle(this.getWorld(), this.getBlockPos(), ParticleTypes.SCRAPE, UniformIntProvider.create(3, 5));
            }
            this.nextOxidationCounter = 0;

            return ActionResult.SUCCESS;
        }
        if (playerItem.isOf(Items.HONEYCOMB) && hand == Hand.MAIN_HAND)
        {
            if (this.getWaxed()) return ActionResult.PASS;
            this.setWaxed(true);
            this.getWorld().playSound(player, this.getBlockPos(), SoundEvents.ITEM_HONEYCOMB_WAX_ON, SoundCategory.BLOCKS, 1.0f, 1.0f);
            ParticleUtil.spawnParticle(this.getWorld(), this.getBlockPos(), ParticleTypes.WAX_ON, UniformIntProvider.create(3, 5));

            return ActionResult.SUCCESS;
        }
        return super.interactMob(player, hand);
    }
    @Override
    public boolean damage(DamageSource source, float amount)
    {
        if (source.isIn(DamageTypeTags.IS_LIGHTNING)) return false;
        return super.damage(source, amount);
    }

    public void onStruckByLightning(ServerWorld world, LightningEntity lightning)
    {
        lightning.setCosmetic(true);
        if (!this.getWaxed())
        {
            if (this.getOxidation() == 0) return;
            this.setOxidation(0);
            ParticleUtil.spawnParticle(this.getWorld(), this.getBlockPos(), ParticleTypes.SCRAPE, UniformIntProvider.create(3, 5));
        }
    }

    public Identifier getTexture()
    {
        int oxidation = this.getOxidation();
        if (oxidation == 0) return TEXTURE;
        if (oxidation == 1) return TEXTURE_EXPOSED;
        if (oxidation == 2) return TEXTURE_WEATHERED;
        return TEXTURE_OXIDIZED;
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
                    return event.setAndContinue(RawAnimation.begin().then("animation.golem_copper.attack_windup", Animation.LoopType.HOLD_ON_LAST_FRAME));
                }
                if (pGolem.getAttackState() == 2)
                {
                    event.getController().setAnimationSpeed(2.00);
                    return event.setAndContinue(RawAnimation.begin().then("animation.golem_copper.attack", Animation.LoopType.HOLD_ON_LAST_FRAME));
                }
                event.getController().setAnimationSpeed(2.00);
                return event.setAndContinue(RawAnimation.begin().then("animation.golem_copper.attack_end", Animation.LoopType.HOLD_ON_LAST_FRAME));
            }
            else
            {
                pGolem.setAttackState(0);
                if (getVelocity().horizontalLengthSquared() > 0.001D || event.isMoving())
                {
                    if (oxidation == 0)
                    {
                        event.getController().setAnimationSpeed(1.00);
                        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.golem_copper.walk"));
                    }
                    if (oxidation == 1)
                    {
                        event.getController().setAnimationSpeed(0.75);
                        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.golem_copper.walk_exposed"));
                    }
                    event.getController().setAnimationSpeed(0.50);
                    return event.setAndContinue(RawAnimation.begin().thenLoop("animation.golem_copper.walk_weathered"));
                }
            }
            event.getController().setAnimationSpeed(1.00);
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.golem_copper.idle"));
        }));
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }

    class CopperGolemFleeEntityGoal<T extends LivingEntity> extends FleeEntityGoal
    {
        private final EntityGolemCopper golem;
        public CopperGolemFleeEntityGoal(EntityGolemCopper pGolem, Class fleeFromType, float distance, double slowSpeed, double fastSpeed)
        {
            super(pGolem, fleeFromType, distance, slowSpeed, fastSpeed);
            golem = pGolem;
        }
        public boolean canStart()
        {
            if (this.golem.isImmobile()) return false;
            return super.canStart();
        }
    }

    class CopperGolemEscapeDangerGoal extends EscapeDangerGoal
    {
        private final EntityGolemCopper golem;
        public CopperGolemEscapeDangerGoal(EntityGolemCopper pGolem, double speed)
        {
            super(pGolem, speed);
            golem = pGolem;
        }
        public boolean canStart()
        {
            if (this.golem.isImmobile()) return false;
            return super.canStart();
        }
    }

    class CopperGolemWanderAroundGoal extends IronGolemWanderAroundGoal
    {
        private final EntityGolemCopper golem;
        public CopperGolemWanderAroundGoal(EntityGolemCopper pGolem, double d)
        {
            super(pGolem, d);
            golem = pGolem;
        }
        public boolean canStart()
        {
            if (this.golem.isImmobile()) return false;
            return super.canStart();
        }
    }

    class CopperGolemLookAtEntityGoal extends LookAtEntityGoal
    {
        private final EntityGolemCopper golem;

        public CopperGolemLookAtEntityGoal(EntityGolemCopper pGolem, Class<? extends LivingEntity> targetType, float range)
        {
            super(pGolem, targetType, range);
            golem = pGolem;
        }
        public boolean canStart()
        {
            if (this.golem.isImmobile()) return false;
            return super.canStart();
        }
    }

    class CopperGolemLookAroundGoal extends LookAroundGoal
    {
        private final EntityGolemCopper golem;
        public CopperGolemLookAroundGoal(EntityGolemCopper pGolem)
        {
            super(pGolem);
            golem = pGolem;
        }
        public boolean canStart()
        {
            if (this.golem.isImmobile()) return false;
            return super.canStart();
        }
    }
}
