package com.kitisplode.golemfirststonemod.entity.goal.action;

import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntitySummoner;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDelayedMeleeAttack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class DandoriFollowSoftGoal extends Goal
{
    protected final PathAwareEntity mob;
    private final IEntityDandoriFollower pik;
    private final double speed;
    @Nullable
    protected LivingEntity owner;
    private boolean active;
    private final double moveRange;
    private final double seeRange;

    public DandoriFollowSoftGoal(PathAwareEntity entity, double pSpeed, double pRange, double pSeeRange)
    {
        this.mob = entity;
        pik = (IEntityDandoriFollower) entity;
        this.speed = pSpeed;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        this.moveRange = MathHelper.square(pRange);
        this.seeRange = pSeeRange;
    }

    @Override
    public boolean canStart()
    {
        if (this.mob.isSleeping() || this.pik.isImmobile()) return false;
        // Dandori only things that are in dandori mode.
        if (pik.isDandoriOff()) return false;
        if (this.mob instanceof IEntityWithDelayedMeleeAttack mobDelayedAttack && mobDelayedAttack.getAttackState() != 0) return false;
        if (this.mob instanceof IEntitySummoner mobDelayedAttack && mobDelayedAttack.getSummonState() != 0) return false;
        // Get the nearest player that we should follow.
        this.owner = this.pik.getOwner();
        if (this.owner == null) return false;

        return (this.seeRange > 0 && this.mob.squaredDistanceTo(this.owner) > MathHelper.square(this.seeRange)) || this.seeRange <= 0;
    }

    @Override
    public boolean shouldContinue()
    {
        return this.canStart();
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
