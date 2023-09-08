package com.kitisplode.golemfirststonemod.entity.entity.golem.vote;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.effect.EntitySoundRepeated;
import com.kitisplode.golemfirststonemod.entity.entity.golem.AbstractGolemDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriFollowHardGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriMoveToDeployPositionGoal;
import com.kitisplode.golemfirststonemod.item.ModItems;
import com.kitisplode.golemfirststonemod.util.ExtraMath;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class EntityGolemTuff extends AbstractGolemDandoriFollower implements GeoEntity, IEntityDandoriFollower
{
    private static final TrackedData<Integer> SLEEP_STATUS = DataTracker.registerData(EntityGolemTuff.class, TrackedDataHandlerRegistry.INTEGER);
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    protected static final double dandoriMoveRange = 3;
    private static final Vec3i ITEM_PICKUP_RANGE_EXPANDER = new Vec3i(1, 1, 1);
    private int itemPickupTimer = 0;
    private int sleepAnimationTimer = 0;
    private static final int sleepStartTime = 50;
    private static final int sleepEndTime = 30;
    private boolean wantsToSleep = false;

    private static final Identifier TEXTURE = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/vote/tuff/golem_tuff.png");
    private static final Identifier TEXTURE_SLEEPING = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/vote/tuff/golem_tuff_sleep.png");

    private static final Predicate<ItemEntity> PICKABLE_DROP_FILTER = item -> !item.cannotPickup() && item.isAlive();

    public EntityGolemTuff(EntityType<? extends IronGolemEntity> pEntityType, World pLevel)
    {
        super(pEntityType, pLevel);
        this.setCanPickUpLoot(true);
        this.setStepHeight(0.9f);
    }

    public static DefaultAttributeContainer.Builder setAttributes()
    {
        return GolemEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 35.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.20f)
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
        if (!this.dataTracker.containsKey(SLEEP_STATUS)) this.dataTracker.startTracking(SLEEP_STATUS, 0);
    }
    @Override
    public void writeCustomDataToNbt(NbtCompound nbt)
    {
        super.writeCustomDataToNbt(nbt);
        int sleepStatus = this.getSleepStatus();
        if (sleepStatus == 0 || sleepStatus == 3) nbt.putInt("SleepStatus", 0);
        if (sleepStatus == 1 || sleepStatus == 2) nbt.putInt("SleepStatus", 2);
    }
    @Override
    public void readCustomDataFromNbt(NbtCompound nbt)
    {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("SleepStatus")) this.setSleepStatus(nbt.getInt("SleepStatus"));
    }
    private void setSleepStatus(int sleepStatus)
    {
        this.dataTracker.set(SLEEP_STATUS, sleepStatus);
    }
    public int getSleepStatus()
    {
        return this.dataTracker.get(SLEEP_STATUS);
    }
    public boolean isSleeping()
    {
        int sleepStatus = this.dataTracker.get(SLEEP_STATUS);
        return sleepStatus > 0;
    }
    public void startSleep()
    {
        this.sleepAnimationTimer = sleepStartTime;
        this.setSleepStatus(1);
        EntitySoundRepeated sound = new EntitySoundRepeated(this.getWorld(), this.getSoundCategory());
        sound.setPosition(this.getPos());
        sound.addSoundNode(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 0, 0.1f, 3.6f);
        sound.addSoundNode(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 3, 0.1f, 3.0f);
        sound.addSoundNode(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 7, 0.1f, 2.6f);
        sound.addSoundNode(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 12, 0.1f, 2.2f);
        sound.addSoundNode(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 18, 0.1f, 1.8f);
        sound.addSoundNode(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 24, 0.1f, 1.4f);
        sound.addSoundNode(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 30, 0.1f, 1.0f);
        this.getWorld().spawnEntity(sound);
    }
    public void endSleep()
    {
        this.sleepAnimationTimer = sleepEndTime;
        this.setSleepStatus(3);
        EntitySoundRepeated sound = new EntitySoundRepeated(this.getWorld(), this.getSoundCategory());
        sound.setPosition(this.getPos());
        sound.addSoundNode(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 0, 0.1f, 1.0f);
        sound.addSoundNode(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 3, 0.1f, 1.4f);
        sound.addSoundNode(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 7, 0.1f, 1.8f);
        sound.addSoundNode(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 12, 0.1f, 2.2f);
        sound.addSoundNode(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 18, 0.1f, 2.6f);
        sound.addSoundNode(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 24, 0.1f, 3.0f);
        sound.addSoundNode(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 30, 0.1f, 3.6f);
        this.getWorld().spawnEntity(sound);
    }

    @Override
    public double getEyeY()
    {
        return getY() + 0.7f;
    }
    @Override
    public int getMaxHeadRotation()
    {
        return 20;
    }

    public float getMovementSpeed()
    {
        if (this.isSleeping()) return 0.0f;
        return (float)this.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new DandoriFollowHardGoal(this, 1.4, Ingredient.ofItems(ModItems.ITEM_DANDORI_CALL, ModItems.ITEM_DANDORI_ATTACK), dandoriMoveRange, dandoriSeeRange));

        this.goalSelector.add(2, new PickupItemGoal(this, 1.0));
        this.goalSelector.add(2, new DandoriMoveToDeployPositionGoal(this, 2.0f, 1.0f));

        this.goalSelector.add(2, new FleeEntityGoal<HostileEntity>(this, HostileEntity.class, 16.0f, 0.9, 1.0));
        this.goalSelector.add(2, new EscapeDangerGoal(this, 1.0));
        this.goalSelector.add(3, new DelayedCalmDownGoal(this, 200, 60 * 5));
        this.goalSelector.add(4, new MoveToFavoredPositionGoal(this, 0.8, 12));
        this.goalSelector.add(5, new IronGolemWanderAroundGoal(this, 0.8));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
        this.goalSelector.add(7, new LookAtEntityGoal(this, MerchantEntity.class, 8.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
    }

    @Override
    public void tick()
    {
        super.tick();
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

    @Override
    public boolean canPickUpLoot() {
        return !this.isItemPickupCoolingDown();
    }

    public boolean isHoldingItem() {
        return !this.getStackInHand(Hand.MAIN_HAND).isEmpty();
    }

    @Override
    public boolean canEquip(ItemStack stack) {
        return false;
    }

    private boolean isItemPickupCoolingDown() {
        return itemPickupTimer > 0;
    }

    @Override
    protected Vec3i getItemPickUpRangeExpander() {
        return ITEM_PICKUP_RANGE_EXPANDER;
    }

    @Override
    public boolean canGather(ItemStack stack) {
        ItemStack itemStack = this.getStackInHand(Hand.MAIN_HAND);
        return !itemStack.isEmpty() && this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING) && this.areItemsEqual(itemStack, stack);
    }

    private boolean areItemsEqual(ItemStack stack, ItemStack stack2) {
        return ItemStack.areItemsEqual(stack, stack2);
    }

    public boolean canPickupItem(ItemStack stack) {
        return !this.isHoldingItem();
    }

    private void dropItem(ItemStack stack) {
        ItemEntity itemEntity = new ItemEntity(this.getWorld(), this.getX(), this.getY(), this.getZ(), stack);
        this.getWorld().spawnEntity(itemEntity);
    }

    @Override
    public void loot(ItemEntity item) {
        ItemStack itemStack = item.getStack();
        if (this.canPickupItem(itemStack)) {
            int i = itemStack.getCount();
            if (i > 1) {
                this.dropItem(itemStack.split(i - 1));
            }
            this.triggerItemPickedUpByEntityCriteria(item);
            this.equipStack(EquipmentSlot.MAINHAND, itemStack.split(1));
            this.updateDropChances(EquipmentSlot.MAINHAND);
            this.sendPickup(item, itemStack.getCount());
            item.discard();

            soundPickup();
        }
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (getSleepStatus() == 2)
        {
            this.endSleep();
            return ActionResult.SUCCESS;
        }
        if (isSleeping()) return ActionResult.PASS;
        ItemStack playerItem = player.getStackInHand(hand);
        ItemStack golemItem = this.getStackInHand(Hand.MAIN_HAND);
        if (!golemItem.isEmpty() && hand == Hand.MAIN_HAND && playerItem.isEmpty())
        {
            soundPickup();

            this.playSound(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 1.0f, 1.5f);
            this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            player.giveItemStack(golemItem);
            return ActionResult.SUCCESS;
        }
        else if (golemItem.isEmpty() && hand == Hand.MAIN_HAND && !playerItem.isEmpty())
        {
            EntitySoundRepeated sound = new EntitySoundRepeated(this.getWorld(), this.getSoundCategory());
            sound.setPosition(this.getPos());
            sound.addSoundNode(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 0, 0.25f, 3.0f);
            sound.addSoundNode(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 3, 0.25f, 1.5f);
            this.getWorld().spawnEntity(sound);

            this.equipStack(EquipmentSlot.MAINHAND, playerItem.copyWithCount(1));
            this.updateDropChances(EquipmentSlot.MAINHAND);
            this.decrementStackUnlessInCreative(player, playerItem);
            return ActionResult.SUCCESS;
        }
        else if (!golemItem.isEmpty() && hand == Hand.MAIN_HAND && !playerItem.isEmpty())
        {
            soundPickup();

            player.giveItemStack(golemItem);
            this.equipStack(EquipmentSlot.MAINHAND, playerItem.copyWithCount(1));
            this.updateDropChances(EquipmentSlot.MAINHAND);
            this.decrementStackUnlessInCreative(player, playerItem);
            return ActionResult.SUCCESS;
        }
        return super.interactMob(player, hand);
    }

    private void soundPickup()
    {
        EntitySoundRepeated sound = new EntitySoundRepeated(this.getWorld(), this.getSoundCategory());
        sound.setPosition(this.getPos());
        sound.addSoundNode(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 0, 0.25f, 2.0f);
        sound.addSoundNode(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 3, 0.25f, 3.0f);
        this.getWorld().spawnEntity(sound);
    }

    private void decrementStackUnlessInCreative(PlayerEntity player, ItemStack stack) {
        if (!player.getAbilities().creativeMode) {
            stack.decrement(1);
        }
    }

    public Identifier getTexture()
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
            if (pGolem.getVelocity().horizontalLengthSquared() > 0.001D || event.isMoving())
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.golem_tuff.walk"));
            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.golem_tuff.idle"));
        }));
    }

    @Override public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }


    class PickupItemGoal
            extends Goal {
        private final EntityGolemTuff mob;
        private final double speed;
        private ItemEntity targetItem = null;
        public PickupItemGoal(EntityGolemTuff mob, double speed) {
            this.mob = mob;
            this.speed = speed;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if (this.mob.isHoldingItem()) return false;
            List<ItemEntity> list = this.mob.getWorld().getEntitiesByClass(ItemEntity.class, this.mob.getBoundingBox().expand(8.0, 8.0, 8.0), PICKABLE_DROP_FILTER);
            if (list.isEmpty()) return false;
            targetItem = this.getNearestItem(list, this.mob.getX(), this.mob.getY(), this.mob.getZ());
            return targetItem != null && this.targetItem.isAlive();
        }

        @Override
        public boolean shouldContinue()
        {
            if (this.mob.isHoldingItem()) return false;
            return this.targetItem != null && this.targetItem.isAlive();
        }

        @Override
        public void tick() {
            if (this.targetItem != null && this.targetItem.isAlive()) {
                double d = this.mob.squaredDistanceTo(this.targetItem);
                if (d < 4)
                {
                    this.mob.loot(this.targetItem);
                    this.targetItem = null;
                }
                else
                {
                    this.mob.getNavigation().startMovingTo(this.targetItem, this.speed);
                }
            }
        }

        @Override
        public void start() {
            if (this.targetItem != null && this.targetItem.isAlive())
            {
                this.mob.getNavigation().startMovingTo(this.targetItem, this.speed);
            }
        }

        private ItemEntity getNearestItem(List<ItemEntity> entityList, double x, double y, double z)
        {
            double d = -1.0;
            ItemEntity itemEntity = null;
            for (ItemEntity itemInList : entityList) {
                double e = itemInList.squaredDistanceTo(x, y, z);
                if (d != -1.0 && !(e < d)) continue;
                if (this.mob.getNavigation().findPathTo(itemInList, 1) == null) continue;
                if (!this.mob.canSee(itemInList)) continue;
                d = e;
                itemEntity = itemInList;
            }
            return itemEntity;
        }
    }

    abstract class CalmDownGoal
            extends Goal {
        private final EntityGolemTuff mob;

        CalmDownGoal(EntityGolemTuff mob) {

            this.mob = mob;
        }

        protected boolean isAtFavoredLocation() {
            BlockPos blockPos = BlockPos.ofFloored(this.mob.getX(), this.mob.getBoundingBox().maxY, this.mob.getZ());
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
                BlockState bs = this.mob.getWorld().getBlockState(bp);
                if (bs.isOpaque())
                {
                    hasBlockNextTo = true;
                    break;
                }
            }
            return hasBlockNextTo;
        }

        protected boolean canCalmDown() {
            if (this.mob.getDandoriState()) return false;
            if (this.mob.getSleepStatus() == 3) return false;
            if (this.mob.getLastAttacker() != null)
                return Math.abs(this.mob.getLastAttackedTime() - this.mob.age) >= 10;
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
            this.maxCalmDownTime = EntityGolemTuff.DelayedCalmDownGoal.toGoalTicks(maxCalmDownTime);
            this.maxSleepTime = maxSleepTime;
            this.timer = this.mob.random.nextInt(this.maxCalmDownTime);
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK, Goal.Control.JUMP));
        }

        @Override
        public boolean canStart() {
            if (this.mob.sidewaysSpeed != 0.0f || this.mob.upwardSpeed != 0.0f || this.mob.forwardSpeed != 0.0f) {
                return false;
            }
            int sleepStatus = this.mob.getSleepStatus();
            return this.canNotCalmDown() || sleepStatus == 1 || sleepStatus == 2;
        }

        @Override
        public boolean shouldContinue() {
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
            return this.canCalmDown() && this.isAtFavoredLocation();
        }

        private void turnAwayFromWalls()
        {
            BlockPos blockPos = BlockPos.ofFloored(this.mob.getX(), this.mob.getBoundingBox().maxY, this.mob.getZ());
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
                BlockState bs = this.mob.getWorld().getBlockState(bp);
                if (bs.isOpaque())
                {
                    Vec3i oppositeVi = vi.multiply(-1);
                    BlockPos oppositeBp = new BlockPos(blockPos.getX() + oppositeVi.getX(),
                            blockPos.getY() + oppositeVi.getY(),
                            blockPos.getZ() + oppositeVi.getZ());
                    BlockState oppositeBs = this.mob.getWorld().getBlockState(oppositeBp);
                    if (!oppositeBs.isOpaque())
                    {
                        this.mob.getLookControl().lookAt(this.mob.getX() + oppositeVi.getX(),
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
            this.mob.getMoveControl().moveTo(this.mob.getX(), this.mob.getY(), this.mob.getZ(), 0.0);
            this.turnAwayFromWalls();
            this.sleepTimer = this.mob.random.nextBetween(this.maxSleepTime, this.maxSleepTime * 10);
        }

        @Override
        public void stop() {
            this.timer = this.mob.random.nextBetween(this.maxCalmDownTime, this.maxCalmDownTime * 2);
            if (this.mob.getSleepStatus() != 3) this.mob.endSleep();
            this.mob.wantsToSleep = false;
        }
    }

    class MoveToFavoredPositionGoal
        extends MoveToTargetPosGoal
    {
        private final EntityGolemTuff golem;
        private boolean reached = false;
        public MoveToFavoredPositionGoal(EntityGolemTuff mob, double speed, int range)
        {
            super(mob, speed, range);
            golem = mob;
        }

        @Override
        public double getDesiredDistanceToTarget() {
            return 0.1d;
        }

        @Override
        public boolean shouldResetPath() {
            return this.tryingTime % 100 == 0;
        }

        protected BlockPos getTargetPos() {
            return this.targetPos;
        }

        @Override
        protected boolean isTargetPos(WorldView world, BlockPos pos)
        {
            if (Math.abs(pos.toCenterPos().getY() - this.mob.getY()) >= 1) return false;
            {
                BlockState bs = world.getBlockState(pos);
                if (!bs.isAir()) return false;
            }
            boolean hasBlockNextTo = false;
            for (int i = 0; i < 4; i++)
            {
                BlockPos bp;
                switch (i)
                {
                    case 0 -> bp = new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ());
                    case 1 -> bp = new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ());
                    case 2 -> bp = new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1);
                    default -> bp = new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1);
                }
                BlockState bs = world.getBlockState(bp);
                if (bs.isOpaque())
                {
                    hasBlockNextTo = true;
                    break;
                }
            }
            if (!hasBlockNextTo) return false;
            {
                BlockPos bp = new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());
                BlockState bs = world.getBlockState(bp);
                return bs.isOpaque() && !bs.isAir();
            }
        }

        @Override
        public void tick() {
            BlockPos blockPos = this.getTargetPos();
            if (blockPos.toCenterPos().squaredDistanceTo(this.mob.getPos()) > MathHelper.square(this.getDesiredDistanceToTarget())) {
                this.reached = false;
                ++this.tryingTime;
                if (this.shouldResetPath()) {
                    this.mob.getNavigation().startMovingTo((double)blockPos.getX() + 0.5, blockPos.getY(), (double)blockPos.getZ() + 0.5, this.speed);
                }
            } else {
                this.reached = true;
                --this.tryingTime;
            }
        }

        protected boolean hasReached() {
            return this.reached;
        }

        @Override
        public boolean canStart() {
            return this.golem.wantsToSleep && !this.mob.isSleeping() && super.canStart();
        }
    }
}
