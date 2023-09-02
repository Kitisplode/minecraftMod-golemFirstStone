package com.kitisplode.golemfirststonemod.entity.goal.target;

import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityTargetsItems;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class ItemTargetGoal<T extends ItemEntity> extends Goal
{
    protected final MobEntity mob;
    protected final IEntityTargetsItems mobThatTargetsItems;
    private static final int DEFAULT_RECIPROCAL_CHANCE = 10;
    protected final int reciprocalChance;
    protected final Class<T> targetClass;
    @Nullable
    protected ItemEntity targetEntity;
    protected Predicate<T> targetPredicate;

    private final boolean checkCanNavigate;
    private int canNavigateFlag;
    private int checkCanNavigateCooldown;
    protected final boolean checkVisibility;
    private int timeWithoutVisibility;
    protected int maxTimeWithoutVisibility = 60;

    public ItemTargetGoal(MobEntity mob, Class<T> targetClass, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate, @Nullable Predicate<T> targetPredicate)
    {
        assert(mob instanceof IEntityTargetsItems);
        this.mob = mob;
        this.mobThatTargetsItems = (IEntityTargetsItems) mob;
        this.targetPredicate = targetPredicate;
        this.targetClass = targetClass;
        this.reciprocalChance = ActiveTargetGoal.toGoalTicks(reciprocalChance);
        this.setControls(EnumSet.of(Goal.Control.TARGET));
        this.checkCanNavigate = checkCanNavigate;
        this.checkVisibility = checkVisibility;
    }


    @Override
    public boolean canStart()
    {
        if (this.reciprocalChance > 0 && this.mob.getRandom().nextInt(this.reciprocalChance) != 0) {
            return false;
        }
        this.findClosestTarget();
        return this.targetEntity != null;
    }


    @Override
    public boolean shouldContinue() {
        ItemEntity itemEntity = this.mobThatTargetsItems.getItemTarget();
        if (itemEntity == null) {
            itemEntity = this.targetEntity;
        }
        if (itemEntity == null) {
            return false;
        }
        if (!this.mobThatTargetsItems.canTargetItem(itemEntity)) {
            return false;
        }
        double d = this.getFollowRange();
        if (this.mob.squaredDistanceTo(itemEntity) > MathHelper.square(d)) {
            return false;
        }
        if (this.checkVisibility) {
            if (this.mob.getVisibilityCache().canSee(itemEntity)) {
                this.timeWithoutVisibility = 0;
            } else if (++this.timeWithoutVisibility > TrackTargetGoal.toGoalTicks(this.maxTimeWithoutVisibility)) {
                return false;
            }
        }
        this.mobThatTargetsItems.setItemTarget(itemEntity);
        return true;
    }

    @Override
    public void start() {
        this.canNavigateFlag = 0;
        this.checkCanNavigateCooldown = 0;
        this.timeWithoutVisibility = 0;
        this.mobThatTargetsItems.setItemTarget(this.targetEntity);
        super.start();
    }

    @Override
    public void stop() {
        this.mobThatTargetsItems.setItemTarget(null);
        this.targetEntity = null;
    }

    public void setTargetEntity(@Nullable T targetEntity) {
        this.targetEntity = targetEntity;
    }

    protected void findClosestTarget() {
        List<T> items = this.mob.getWorld().getEntitiesByClass(this.targetClass, this.getSearchBox(this.getFollowRange()), itemEntity -> true);
        this.targetEntity = this.getClosestEntity(items, this.targetPredicate, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
    }

    protected T getClosestEntity(List<T> list, Predicate<T> tp, double x, double y, double z)
    {
        double d = -1.0;
        T currentTargetItem = null;
        for (T itemInList : list) {
            if (!tp.test(itemInList)) continue;
            double e = itemInList.squaredDistanceTo(x, y, z);
            if (d != -1.0 && !(e < d)) continue;
            d = e;
            currentTargetItem = itemInList;
        }
        return (T)currentTargetItem;
    }

    protected Box getSearchBox(double distance) {
        return this.mob.getBoundingBox().expand(distance, 4.0, distance);
    }

    protected double getFollowRange() {
        return this.mob.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
    }

    protected boolean canTrack(@Nullable T target, Predicate<T> targetPredicate) {
        if (target == null) {
            return false;
        }
        if (!targetPredicate.test(target)) {
            return false;
        }
        if (!this.mob.isInWalkTargetRange(target.getBlockPos())) {
            return false;
        }
        if (this.checkCanNavigate) {
            if (--this.checkCanNavigateCooldown <= 0) {
                this.canNavigateFlag = 0;
            }
            if (this.canNavigateFlag == 0) {
                int n = this.canNavigateFlag = this.canNavigateToEntity(target) ? 1 : 2;
            }
            if (this.canNavigateFlag == 2) {
                return false;
            }
        }
        return true;
    }

    private boolean canNavigateToEntity(T entity) {
        int j;
        this.checkCanNavigateCooldown = TrackTargetGoal.toGoalTicks(10 + this.mob.getRandom().nextInt(5));
        Path path = this.mob.getNavigation().findPathTo(entity, 0);
        if (path == null) {
            return false;
        }
        PathNode pathNode = path.getEnd();
        if (pathNode == null) {
            return false;
        }
        int i = pathNode.x - entity.getBlockX();
        return (double)(i * i + (j = pathNode.z - entity.getBlockZ()) * j) <= 2.25;
    }

    public ItemTargetGoal<T> setMaxTimeWithoutVisibility(int time) {
        this.maxTimeWithoutVisibility = time;
        return this;
    }
}
