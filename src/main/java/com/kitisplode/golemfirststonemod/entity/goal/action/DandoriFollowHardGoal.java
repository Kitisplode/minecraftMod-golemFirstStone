package com.kitisplode.golemfirststonemod.entity.goal.action;

import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class DandoriFollowHardGoal extends Goal
{
    protected final PathfinderMob mob;
    private final IEntityDandoriFollower pik;
    private final double speedModifier;
    @Nullable
    protected Player closestPlayer;
    private boolean isRunning;
    private final double moveRange;
    private final double seeRange;

    public DandoriFollowHardGoal(PathfinderMob entity, double pSpeed, double pRange, double pSeeRange)
    {
        this.mob = entity;
        pik = (IEntityDandoriFollower) entity;
        this.speedModifier = pSpeed;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        this.moveRange = Mth.square(pRange);
        this.seeRange = pSeeRange;
    }

    @Override
    public boolean canUse()
    {
        if (this.mob.isSleeping() || this.pik.isImmobile()) return false;
        // Dandori only things that are in dandori mode.
        if (!pik.isDandoriHard()) return false;
        // Get the nearest player that we should follow.
        this.closestPlayer = (Player)this.pik.getOwner();
        return this.closestPlayer != null;
    }

    private boolean shouldFollow(LivingEntity targetEntity) {
        return targetEntity == this.pik.getOwner();
    }

    @Override
    public boolean canContinueToUse()
    {
        if (!this.canUse()) return false;
        return this.mob.getTarget() != null;
    }

    public void start()
    {
        this.isRunning = true;
        this.mob.level().broadcastEntityEvent(this.mob, IEntityDandoriFollower.ENTITY_EVENT_DANDORI_START);
    }

    public void stop()
    {
        this.closestPlayer = null;
        this.mob.getNavigation().stop();
        this.isRunning = false;
        this.pik.setDandoriState(IEntityDandoriFollower.DANDORI_STATES.SOFT.ordinal());
    }

    public void tick() {
        if (this.closestPlayer == null) return;
        this.mob.getLookControl().setLookAt(this.closestPlayer, (float)(this.mob.getMaxHeadYRot() + 20), (float)this.mob.getMaxHeadXRot());
        if (this.mob.distanceToSqr(this.closestPlayer) < moveRange) {
            this.mob.getNavigation().stop();
        } else {
            this.mob.getNavigation().moveTo(this.closestPlayer, this.speedModifier);
        }
    }

    public boolean isRunning() {
        return this.isRunning;
    }
}
