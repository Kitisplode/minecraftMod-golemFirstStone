package com.kitisplode.golemfirststonemod.entity.goal.action;

import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class DandoriFollowHardGoal extends Goal
{
    protected final PathAwareEntity mob;
    private final IEntityDandoriFollower pik;
    private final double speed;
    @Nullable
    protected PlayerEntity closestPlayer;
    private boolean active;
    private final double moveRange;

    public DandoriFollowHardGoal(PathAwareEntity entity, double pSpeed, double pRange, double pSeeRange)
    {
        this.mob = entity;
        pik = (IEntityDandoriFollower) entity;
        this.speed = pSpeed;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        this.moveRange = MathHelper.square(pRange);
    }

    @Override
    public boolean canStart()
    {
        if (this.mob.isSleeping() || this.pik.isImmobile()) return false;
        // Dandori only things that are in dandori mode.
        if (!pik.isDandoriHard()) return false;
        // Get the nearest player that we should follow.
        this.closestPlayer = (PlayerEntity)this.pik.getOwner();//this.mob.getWorld().getClosestPlayer(this.predicate, this.mob);
        return this.closestPlayer != null;
    }

    private boolean isTemptedBy(LivingEntity entity)
    {
        return entity == this.pik.getOwner();
//        return this.food.test(entity.getMainHandStack()) || this.food.test(entity.getOffHandStack());
    }

    @Override
    public boolean shouldContinue()
    {
        if (!this.canStart()) return false;
        return this.mob.getTarget() != null;
    }

    @Override
    public void start()
    {
        this.active = true;
        this.mob.getWorld().sendEntityStatus(this.mob, IEntityDandoriFollower.ENTITY_EVENT_DANDORI_START);
    }

    @Override
    public void stop()
    {
        this.closestPlayer = null;
        this.mob.getNavigation().stop();
        this.active = false;
        this.pik.setDandoriState(IEntityDandoriFollower.DANDORI_STATES.SOFT.ordinal());
    }

    @Override
    public void tick()
    {
        this.mob.getLookControl().lookAt(this.closestPlayer, this.mob.getMaxHeadRotation() + 20, this.mob.getMaxLookPitchChange());
        if (this.mob.squaredDistanceTo(this.closestPlayer) < moveRange) {
            this.mob.getNavigation().stop();
        } else {
            this.mob.getNavigation().startMovingTo(this.closestPlayer, this.speed);
        }
    }

    public boolean isActive()
    {
        return this.active;
    }
}
