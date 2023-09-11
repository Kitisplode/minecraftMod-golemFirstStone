package com.kitisplode.golemfirststonemod.entity.goal.action;

import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntitySummoner;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDelayedMeleeAttack;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class DandoriFollowSoftGoal extends Goal
{
    protected final PathfinderMob mob;
    private final IEntityDandoriFollower pik;
    private final double speedModifier;
    @Nullable
    protected LivingEntity owner;
    private boolean isRunning;
    private final double moveRange;
    private final double seeRange;

    public DandoriFollowSoftGoal(PathfinderMob entity, double pSpeed, double pRange, double pSeeRange)
    {
        this.mob = entity;
        pik = (IEntityDandoriFollower) entity;
        this.speedModifier = pSpeed;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.moveRange = Mth.square(pRange);
        this.seeRange = pSeeRange;
    }

    @Override
    public boolean canUse()
    {
        if (this.mob.isSleeping() || this.pik.isImmobile()) return false;
        // Dandori only things that are in dandori mode.
        if (pik.isDandoriOff()) return false;
        if (this.mob instanceof IEntityWithDelayedMeleeAttack mobDelayedAttack && mobDelayedAttack.getAttackState() != 0) return false;
        if (this.mob instanceof IEntitySummoner mobDelayedAttack && mobDelayedAttack.getSummonState() != 0) return false;
        // Get the nearest player that we should follow.
        this.owner = this.pik.getOwner();
        if (this.owner == null) return false;

        return (this.seeRange > 0 && this.mob.distanceToSqr(this.owner) > Mth.square(this.seeRange)) || this.seeRange <= 0;
    }

    public void start()
    {
        this.isRunning = true;
        this.mob.level().broadcastEntityEvent(this.mob, IEntityDandoriFollower.ENTITY_EVENT_DANDORI_START);
    }

    public void stop()
    {
        this.owner = null;
        this.mob.getNavigation().stop();
        this.isRunning = false;
    }

    public void tick() {
        if (this.owner == null) return;
        this.mob.getLookControl().setLookAt(this.owner, (float)(this.mob.getMaxHeadYRot() + 20), (float)this.mob.getMaxHeadXRot());
        if (this.mob.distanceToSqr(this.owner) < moveRange) {
            this.mob.getNavigation().stop();
        } else {
            this.mob.getNavigation().moveTo(this.owner, this.speedModifier);
        }
    }

    public boolean isRunning() {
        return this.isRunning;
    }
}
