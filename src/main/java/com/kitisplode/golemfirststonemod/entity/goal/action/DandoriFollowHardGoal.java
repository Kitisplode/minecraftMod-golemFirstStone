package com.kitisplode.golemfirststonemod.entity.goal.action;

import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntitySummoner;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDelayedMeleeAttack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class DandoriFollowHardGoal extends Goal
{
    protected final PathAwareEntity mob;
    private final IEntityDandoriFollower pik;
    private final double speed;
    @Nullable
    protected LivingEntity owner;
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
        if (this.mob instanceof IEntityWithDelayedMeleeAttack mobDelayedAttack && mobDelayedAttack.getAttackState() != 0) return false;
        if (this.mob instanceof IEntitySummoner mobDelayedAttack && mobDelayedAttack.getSummonState() != 0) return false;
        // Get the nearest player that we should follow.
        this.owner = this.pik.getOwner();//this.mob.getWorld().getClosestPlayer(this.predicate, this.mob);
        return this.owner != null;
    }

    private boolean isTemptedBy(LivingEntity entity)
    {
        return entity == this.pik.getOwner();
//        this.owner = this.pik.getOwner();
//        return this.owner != null;
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
        this.owner = null;
        this.mob.getNavigation().stop();
        this.active = false;
        this.pik.setDandoriState(IEntityDandoriFollower.DANDORI_STATES.SOFT.ordinal());
    }

    @Override
    public void tick()
    {
        this.mob.getLookControl().lookAt(this.owner, this.mob.getMaxHeadRotation() + 20, this.mob.getMaxLookPitchChange());
        if (this.mob.squaredDistanceTo(this.owner) < moveRange) {
            this.mob.getNavigation().stop();
        } else {
            this.mob.getNavigation().startMovingTo(this.owner, this.speed);
        }
    }

    public boolean isActive()
    {
        return this.active;
    }
}
