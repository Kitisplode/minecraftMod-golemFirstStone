package com.kitisplode.golemfirststonemod.entity.entity.golem.vote;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.effect.EntitySoundRepeated;
import com.kitisplode.golemfirststonemod.entity.entity.golem.AbstractGolemDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemMossy;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDelayedMeleeAttack;
import com.kitisplode.golemfirststonemod.entity.entity.projectile.EntityProjectileAoEOwnerAware;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriFollowHardGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriFollowSoftGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriMoveToDeployPositionGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.MultiStageAttackGoalRanged;
import com.kitisplode.golemfirststonemod.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class EntityGolemTuff extends AbstractGolemDandoriFollower implements GeoEntity, IEntityDandoriFollower
{
    private static final EntityDataAccessor<Integer> SLEEP_STATUS = SynchedEntityData.defineId(EntityGolemMossy.class, EntityDataSerializers.INT);
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    protected static final double dandoriMoveRange = 3;
    private static final Vec3i ITEM_PICKUP_REACH = new Vec3i(1, 1, 1);
    private int itemPickupTimer = 0;
    private int sleepAnimationTimer = 0;
    private static final int sleepStartTime = 50;
    private static final int sleepEndTime = 30;
    private boolean wantsToSleep = false;

    static final Predicate<ItemEntity> ALLOWED_ITEMS = itemEntity -> !itemEntity.hasPickUpDelay() && itemEntity.isAlive();

    private static final ResourceLocation TEXTURE = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/vote/tuff/golem_tuff.png");
    private static final ResourceLocation TEXTURE_SLEEPING = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/vote/tuff/golem_tuff_sleep.png");

    public EntityGolemTuff(EntityType<? extends IronGolem> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
        this.setCanPickUpLoot(true);
        this.setMaxUpStep(0.9F);
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 35.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.20f)
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
        if (!this.entityData.hasItem(SLEEP_STATUS)) this.entityData.define(SLEEP_STATUS, 0);
    }
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        int sleepStatus = this.getSleepStatus();
        if (sleepStatus == 0 || sleepStatus == 3) pCompound.putInt("SleepStatus", 0);
        if (sleepStatus == 1 || sleepStatus == 2) pCompound.putInt("SleepStatus", 2);
    }
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);;
        if (pCompound.contains("SleepStatus")) this.setSleepStatus(pCompound.getInt("SleepStatus"));
    }
    public int getSleepStatus()
    {
        return this.entityData.get(SLEEP_STATUS);
    }
    public void setSleepStatus(int pInt)
    {
        this.entityData.set(SLEEP_STATUS, pInt);
    }
    public boolean isSleeping()
    {
        int sleepStatus = this.entityData.get(SLEEP_STATUS);
        return sleepStatus > 0;
    }
    public void startSleep()
    {
        this.sleepAnimationTimer = sleepStartTime;
        this.setSleepStatus(1);
        EntitySoundRepeated sound = new EntitySoundRepeated(this.level(), this.getSoundSource());
        sound.setPos(this.position());
        sound.addSoundNode(SoundEvents.IRON_GOLEM_REPAIR, 0, 0.1f, 3.6f);
        sound.addSoundNode(SoundEvents.IRON_GOLEM_REPAIR, 3, 0.1f, 3.0f);
        sound.addSoundNode(SoundEvents.IRON_GOLEM_REPAIR, 7, 0.1f, 2.6f);
        sound.addSoundNode(SoundEvents.IRON_GOLEM_REPAIR, 12, 0.1f, 2.2f);
        sound.addSoundNode(SoundEvents.IRON_GOLEM_REPAIR, 18, 0.1f, 1.8f);
        sound.addSoundNode(SoundEvents.IRON_GOLEM_REPAIR, 24, 0.1f, 1.4f);
        sound.addSoundNode(SoundEvents.IRON_GOLEM_REPAIR, 30, 0.1f, 1.0f);
        this.level().addFreshEntity(sound);
    }
    public void endSleep()
    {
        this.sleepAnimationTimer = sleepEndTime;
        this.setSleepStatus(3);
        EntitySoundRepeated sound = new EntitySoundRepeated(this.level(), this.getSoundSource());
        sound.setPos(this.position());
        sound.addSoundNode(SoundEvents.IRON_GOLEM_REPAIR, 0, 0.1f, 1.0f);
        sound.addSoundNode(SoundEvents.IRON_GOLEM_REPAIR, 3, 0.1f, 1.4f);
        sound.addSoundNode(SoundEvents.IRON_GOLEM_REPAIR, 7, 0.1f, 1.8f);
        sound.addSoundNode(SoundEvents.IRON_GOLEM_REPAIR, 12, 0.1f, 2.2f);
        sound.addSoundNode(SoundEvents.IRON_GOLEM_REPAIR, 18, 0.1f, 2.6f);
        sound.addSoundNode(SoundEvents.IRON_GOLEM_REPAIR, 24, 0.1f, 3.0f);
        sound.addSoundNode(SoundEvents.IRON_GOLEM_REPAIR, 30, 0.1f, 3.6f);
        this.level().addFreshEntity(sound);
    }

    @Override
    public double getEyeY()
    {
        return getY() + 0.6f;
    }
    @Override
    public int getMaxHeadYRot()
    {
        return 20;
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(0, new DandoriFollowHardGoal(this, 1.4, dandoriMoveRange, dandoriSeeRange));
        this.goalSelector.addGoal(1, new DandoriFollowSoftGoal(this, 1.2, dandoriMoveRange, 6));

        this.goalSelector.addGoal(2, new PickupItemGoal(this, 1.0));
        this.goalSelector.addGoal(3, new DandoriMoveToDeployPositionGoal(this, 2.0f, 1.0f));

        this.goalSelector.addGoal(4, new DandoriFollowSoftGoal(this, 1.2, dandoriMoveRange, 0));


        this.goalSelector.addGoal(5, new AvoidEntityGoal<>(this, Monster.class, 16, 0.9D, 1));
        this.goalSelector.addGoal(5, new PanicGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new DelayedCalmDownGoal(this, 200, 60 * 5));
        this.goalSelector.addGoal(7, new MoveToFavoredPosition(this, 0.8D, 12));
        this.goalSelector.addGoal(8, new GolemRandomStrollInVillageGoal(this, 0.8D));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, AbstractVillager.class, 6.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
    }

    @Override
    public void tick()
    {
        super.tick();
        if (this.isSleeping() || this.isImmobile()) {
            this.xxa = 0.0F;
            this.zza = 0.0F;
        }

        if (itemPickupTimer > 0) itemPickupTimer--;
        if (sleepAnimationTimer > 0)
        {
            sleepAnimationTimer--;
            if (sleepAnimationTimer <= 0)
            {
                if (this.getSleepStatus() == 1) this.setSleepStatus(2);
                else this.setSleepStatus(0);
            }
        }
    }

    public boolean canTakeItem(ItemStack pItemstack) {
        EquipmentSlot equipmentslot = Mob.getEquipmentSlotForItem(pItemstack);
        if (!this.getItemBySlot(equipmentslot).isEmpty()) {
            return false;
        } else {
            return equipmentslot == EquipmentSlot.MAINHAND && super.canTakeItem(pItemstack);
        }
    }

    public boolean canHoldItem(ItemStack pStack) {
        return !isHoldingItem();
    }

    public boolean isHoldingItem()
    {
        return !this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty();
    }
    @NotNull
    protected Vec3i getPickupReach() {
        return ITEM_PICKUP_REACH;
    }

    private void dropItemStack(ItemStack pStack) {
        ItemEntity itementity = new ItemEntity(this.level(), this.getX(), this.getY(), this.getZ(), pStack);
        this.level().addFreshEntity(itementity);
    }

    protected void pickUpItem(ItemEntity pItemEntity) {
        ItemStack itemstack = pItemEntity.getItem();
        if (this.canHoldItem(itemstack)) {
            int i = itemstack.getCount();
            if (i > 1) {
                this.dropItemStack(itemstack.split(i - 1));
            }
            this.onItemPickup(pItemEntity);
            this.setItemSlot(EquipmentSlot.MAINHAND, itemstack.split(1));
            this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
            this.take(pItemEntity, itemstack.getCount());
            pItemEntity.discard();
        }
    }

    @NotNull
    protected InteractionResult mobInteract(@NotNull Player pPlayer, @NotNull InteractionHand pHand)
    {
        if (getSleepStatus() == 2)
        {
            this.endSleep();
            return InteractionResult.SUCCESS;
        }
        if (isSleeping()) return InteractionResult.PASS;
        ItemStack itemStack = pPlayer.getItemInHand(pHand);
        ItemStack itemStack2 = this.getItemInHand(InteractionHand.MAIN_HAND);
        if (!itemStack2.isEmpty() && pHand == InteractionHand.MAIN_HAND && itemStack.isEmpty())
        {
            soundPickup();
            this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
            pPlayer.addItem(itemStack2);
            return InteractionResult.SUCCESS;
        }
        else if (itemStack2.isEmpty() && pHand == InteractionHand.MAIN_HAND && !itemStack.isEmpty())
        {
            EntitySoundRepeated sound = new EntitySoundRepeated(this.level(), this.getSoundSource());
            sound.setPos(this.position());
            sound.addSoundNode(SoundEvents.IRON_GOLEM_REPAIR, 0, 0.25f, 3.0f);
            sound.addSoundNode(SoundEvents.IRON_GOLEM_REPAIR, 3, 0.25f, 1.5f);
            this.level().addFreshEntity(sound);

            this.setItemInHand(InteractionHand.MAIN_HAND, itemStack.copyWithCount(1));
            this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
            this.removeInteractionItem(pPlayer, itemStack);
            return InteractionResult.SUCCESS;
        }
        else if (!itemStack2.isEmpty() && pHand == InteractionHand.MAIN_HAND && !itemStack.isEmpty())
        {
            soundPickup();
            pPlayer.addItem(itemStack2);
            this.setItemInHand(InteractionHand.MAIN_HAND, itemStack.copyWithCount(1));
            this.setGuaranteedDrop(EquipmentSlot.MAINHAND);
            this.removeInteractionItem(pPlayer, itemStack);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(pPlayer, pHand);
    }
    private void removeInteractionItem(Player pPlayer, ItemStack pStack) {
        if (!pPlayer.getAbilities().instabuild) {
            pStack.shrink(1);
        }
    }

    private void soundPickup()
    {
        EntitySoundRepeated sound = new EntitySoundRepeated(this.level(), this.getSoundSource());
        sound.setPos(this.position());
        sound.addSoundNode(SoundEvents.IRON_GOLEM_REPAIR, 0, 0.25f, 2.0f);
        sound.addSoundNode(SoundEvents.IRON_GOLEM_REPAIR, 3, 0.25f, 3.0f);
        this.level().addFreshEntity(sound);
    }

    public ResourceLocation getTexture()
    {
        if (getSleepStatus() > 0) return TEXTURE_SLEEPING;
        return TEXTURE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, event ->
        {
            EntityGolemTuff pGolem = event.getAnimatable();
            int sleepStatus = pGolem.getSleepStatus();
            if (sleepStatus > 0)
            {
                switch (sleepStatus)
                {
                    case 1: return event.setAndContinue(RawAnimation.begin().then("animation.golem_tuff.sit_start", Animation.LoopType.HOLD_ON_LAST_FRAME));
                    case 2: return event.setAndContinue(RawAnimation.begin().then("animation.golem_tuff.sit", Animation.LoopType.HOLD_ON_LAST_FRAME));
                    case 3: return event.setAndContinue(RawAnimation.begin().then("animation.golem_tuff.sit_stop", Animation.LoopType.HOLD_ON_LAST_FRAME));
                    default: break;
                }
            }
            event.getController().setAnimationSpeed(1.00);
            if (pGolem.getDeltaMovement().horizontalDistanceSqr() > 0.001D || event.isMoving())
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.golem_tuff.walk"));
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.golem_tuff.idle"));
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }

    class PickupItemGoal extends Goal {
        private final EntityGolemTuff mob;
        private final double speed;
        private ItemEntity targetItem = null;
        public PickupItemGoal(EntityGolemTuff mob, double speed) {
            this.mob = mob;
            this.speed = speed;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }
        public boolean canUse() {
            if (this.mob.isHoldingItem()) return false;
            List<ItemEntity> list = this.mob.level().getEntitiesOfClass(ItemEntity.class, this.mob.getBoundingBox().inflate(8.0, 8.0, 8.0), ALLOWED_ITEMS);
            if (list.isEmpty()) return false;
            targetItem = this.getNearestItem(list, this.mob.getX(), this.mob.getY(), this.mob.getZ());
            return targetItem != null && this.targetItem.isAlive();
        }
        public boolean canContinueToUse()
        {
            if (this.mob.isHoldingItem()) return false;
            return this.targetItem != null && this.targetItem.isAlive();
        }
        public void tick() {
            if (this.targetItem != null && this.targetItem.isAlive()) {
                double d = this.mob.distanceToSqr(this.targetItem);
                if (d < 4)
                {
                    this.mob.pickUpItem(this.targetItem);
                    this.targetItem = null;
                }
                else
                {
                    this.mob.getNavigation().moveTo(this.targetItem, this.speed);
                }
            }
        }
        public void start() {
            if (this.targetItem != null && this.targetItem.isAlive())
            {
                this.mob.getNavigation().moveTo(this.targetItem, this.speed);
            }
        }
        private ItemEntity getNearestItem(List<ItemEntity> entityList, double x, double y, double z)
        {
            double d = -1.0;
            ItemEntity itemEntity = null;
            for (ItemEntity itemInList : entityList) {
                double e = itemInList.distanceToSqr(x, y, z);
                if (d != -1.0 && !(e < d)) continue;
                if (this.mob.getNavigation().createPath(itemInList, 1) == null) continue;
                if (!this.mob.hasLineOfSight(itemInList)) continue;
                d = e;
                itemEntity = itemInList;
            }
            return itemEntity;
        }
    }

    abstract class CalmDownGoal extends Goal {
        private final EntityGolemTuff mob;
        CalmDownGoal(EntityGolemTuff mob)
        {
            this.mob = mob;
        }
        protected boolean isAtFavoredPosition() {
            BlockPos blockPos = BlockPos.containing(this.mob.getX(), this.mob.getBoundingBox().maxY, this.mob.getZ());
            boolean hasBlockNextTo = false;
            for (int i = 0; i < 4; i++)
            {
                BlockPos bp;
                switch (i)
                {
                    case 0 -> bp = new BlockPos(blockPos.getX() + 1, blockPos.getY(), blockPos.getZ());
                    case 1 -> bp = new BlockPos(blockPos.getX() - 1, blockPos.getY(), blockPos.getZ());
                    case 2 -> bp = new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ() + 1);
                    default -> bp = new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ() - 1);
                }
                BlockState bs = this.mob.level().getBlockState(bp);
                if (bs.canOcclude())
                {
                    hasBlockNextTo = true;
                    break;
                }
            }
            return hasBlockNextTo;
        }
        protected boolean canCalmDown() {
            if (this.mob.isDandoriOn()) return false;
            if (this.mob.getSleepStatus() == 3) return false;
            if (this.mob.getLastAttacker() != null)
                return Math.abs(this.mob.getLastHurtByMobTimestamp() - this.mob.tickCount) >= 10;
            return true;
        }
    }

    class DelayedCalmDownGoal
            extends CalmDownGoal
    {
        private final int maxCalmDownTime;
        private int timer;
        private final EntityGolemTuff mob;
        private final int maxSleepTime;
        private int sleepTimer;

        public DelayedCalmDownGoal(EntityGolemTuff mob, int maxCalmDownTime, int maxSleepTime) {
            super(mob);
            this.mob = mob;
            this.maxCalmDownTime = reducedTickDelay(maxCalmDownTime);
            this.maxSleepTime = maxSleepTime;
            this.timer = this.mob.random.nextInt(this.maxCalmDownTime);
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            if (this.mob.xxa != 0.0f || this.mob.yya != 0.0f || this.mob.zza != 0.0f) {
                return false;
            }
            int sleepStatus = this.mob.getSleepStatus();
            return this.canNotCalmDown() || sleepStatus == 1 || sleepStatus == 2;
        }

        @Override
        public boolean canContinueToUse() {
            int sleepStatus = this.mob.getSleepStatus();
            if (this.sleepTimer > 0)
            {
                --this.sleepTimer;
                if (this.sleepTimer <= 0) return false;
            }
            return this.canNotCalmDown() || sleepStatus == 1 || sleepStatus == 2;
        }

        private boolean canNotCalmDown() {
            if (this.timer > 0) {
                --this.timer;
                return false;
            }
            this.mob.wantsToSleep = true;
            return this.canCalmDown() && isAtFavoredPosition();
        }

        private void turnAwayFromWalls()
        {
            BlockPos blockPos = BlockPos.containing(this.mob.getX(), this.mob.getBoundingBox().maxY, this.mob.getZ());
            // Find an angle that does have a wall, then face away from it if there isn't a wall in that direction, too.
            for (int i = 0; i < 4; i++)
            {
                Vec3i vi;
                switch (i)
                {
                    case 0 -> vi = new Vec3i(1,0,0);
                    case 1 -> vi = new Vec3i(-1,0,0);
                    case 2 -> vi = new Vec3i(0,0,1);
                    default -> vi = new Vec3i(0,0,-1);
                }
                BlockPos bp = new BlockPos(blockPos.getX() + vi.getX(),
                        blockPos.getY() + vi.getY(),
                        blockPos.getZ() + vi.getZ());
                BlockState bs = this.mob.level().getBlockState(bp);
                if (bs.canOcclude())
                {
                    Vec3i oppositeVi = vi.multiply(-1);
                    BlockPos oppositeBp = new BlockPos(blockPos.getX() + oppositeVi.getX(),
                            blockPos.getY() + oppositeVi.getY(),
                            blockPos.getZ() + oppositeVi.getZ());
                    BlockState oppositeBs = this.mob.level().getBlockState(oppositeBp);
                    if (!oppositeBs.canOcclude())
                    {
                        this.mob.getLookControl().setLookAt(this.mob.getX() + oppositeVi.getX(),
                                this.mob.getEyeY() + oppositeVi.getY(),
                                this.mob.getZ() + oppositeVi.getZ(),
                                180,0);
                    }
                }
            }
        }

        @Override
        public void start() {
            if (!this.mob.isSleeping()) this.mob.startSleep();
            this.mob.getNavigation().stop();
            this.mob.getMoveControl().setWantedPosition(this.mob.getX(), this.mob.getY(), this.mob.getZ(), 0.0D);
            this.turnAwayFromWalls();
            this.sleepTimer = this.mob.random.nextIntBetweenInclusive(this.maxSleepTime, this.maxSleepTime * 10);
        }

        @Override
        public void stop() {
            this.timer = this.mob.random.nextIntBetweenInclusive(this.maxCalmDownTime, this.maxCalmDownTime * 2);
            if (this.mob.getSleepStatus() != 3) this.mob.endSleep();
            this.mob.wantsToSleep = false;
        }
    }

    class MoveToFavoredPosition extends MoveToBlockGoal
    {
        private final EntityGolemTuff golem;
        private boolean reachedTarget = false;
        public MoveToFavoredPosition(EntityGolemTuff pMob, double pSpeedModifier, int pSearchRange)
        {
            super(pMob, pSpeedModifier, pSearchRange);
            golem = pMob;
        }

        public double acceptedDistance() {
            return 0.1D;
        }
        public boolean shouldRecalculatePath() {
            return this.tryTicks % 100 == 0;
        }
        protected BlockPos getMoveToTarget() {
            return this.blockPos;
        }

        @Override
        protected boolean isValidTarget(LevelReader pLevel, BlockPos pPos)
        {
            return false;
        }
        @Override
        public void tick() {
            BlockPos blockpos = this.getMoveToTarget();
            if (blockPos.getCenter().distanceToSqr(this.mob.position()) > Mth.square(this.acceptedDistance())) {
                this.reachedTarget = false;
                ++this.tryTicks;
                if (this.shouldRecalculatePath()) {
                    this.mob.getNavigation().moveTo((double)((float)blockpos.getX()) + 0.5D, (double)blockpos.getY(), (double)((float)blockpos.getZ()) + 0.5D, this.speedModifier);
                }
            } else {
                this.reachedTarget = true;
                --this.tryTicks;
            }
        }
        protected boolean isReachedTarget() {
            return this.reachedTarget;
        }

        @Override
        public boolean canUse() {
            return this.golem.wantsToSleep && !this.mob.isSleeping() && super.canUse();
        }
    }
}
