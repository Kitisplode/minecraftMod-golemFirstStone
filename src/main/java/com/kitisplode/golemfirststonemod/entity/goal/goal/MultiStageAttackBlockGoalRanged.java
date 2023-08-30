package com.kitisplode.golemfirststonemod.entity.goal.goal;

import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityCanAttackBlocks;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDelayedMeleeAttack;
import com.kitisplode.golemfirststonemod.util.ExtraMath;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class MultiStageAttackBlockGoalRanged extends MeleeAttackGoal
{
    private Path path;
    private long lastUpdateTime;

    private final IEntityWithDelayedMeleeAttack actor;
    private final IEntityCanAttackBlocks blockAttacker;
    private int attackState;
    private int attackTimer;
    private final double attackRange;
    private final double speed;
    private final int[] attackStages;
    private final int turnDuringState;

    public MultiStageAttackBlockGoalRanged(IEntityWithDelayedMeleeAttack pMob, double pSpeed, boolean pauseWhenMobIdle, double pAttackRange, int[] pAttackStages, int pTurnDuringState)
    {
        super((PathfinderMob) pMob,pSpeed, pauseWhenMobIdle);
        assert(pMob instanceof IEntityCanAttackBlocks);
        actor = pMob;
        blockAttacker = (IEntityCanAttackBlocks) pMob;
        speed = pSpeed;
        attackState = 0;
        attackTimer = 0;
        attackRange = pAttackRange;
        attackStages = pAttackStages.clone();
        turnDuringState = pTurnDuringState;
    }

    public MultiStageAttackBlockGoalRanged(IEntityWithDelayedMeleeAttack pMob, double pSpeed, boolean pauseWhenMobIdle, double pAttackRange, int[] pAttackStages)
    {
        this(pMob, pSpeed, pauseWhenMobIdle, pAttackRange, pAttackStages, 0);
    }

    public boolean canUse()
    {
        if (!this.mob.getPassengers().isEmpty()) return false;

        long i = this.mob.level().getGameTime();
        if (i - this.lastUpdateTime < 20L) {
            return false;
        }
        this.lastUpdateTime = i;
        BlockPos targetPos = this.blockAttacker.getBlockTarget();
        if (targetPos == null) {
            return false;
        }
        if (!this.blockAttacker.canTargetBlock(targetPos)) {
            return false;
        }
        this.path = this.mob.getNavigation().createPath(targetPos, 0);
        return this.path != null;

    }

    @Override
    public boolean canContinueToUse()
    {
        if (attackState > 0) return true;
        return this.blockAttacker.canTargetBlock(this.blockAttacker.getBlockTarget());
    }

    @Override
    public void start()
    {
        super.start();
        attackTimer = 0;
        actor.setAttackState(0);
        path = null;
    }

    @Override
    public void stop()
    {
        super.stop();
        attackTimer = 0;
        actor.setAttackState(0);
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick()
    {
        BlockPos targetPos = this.blockAttacker.getBlockTarget();
        // If we're not attacking, try to attack if we can.
        if (attackTimer <= 0)
        {
            if (targetPos == null)
            {
                return;
            }
            Vec3 targetCenter = targetPos.getCenter();
            double distanceToTarget = this.mob.distanceToSqr(targetCenter);
            BlockHitResult ray = this.mob.level().clip(new ClipContext(this.mob.getEyePosition(), targetCenter, ClipContext.Block.OUTLINE, ClipContext.Fluid.ANY, this.mob));
            boolean canSeeTarget = !ray.getBlockPos().closerToCenterThan(this.mob.getEyePosition(), distanceToTarget - 1);
            // Approach the target if we're not in attack range (can't beat them up without getting closer)
            if (distanceToTarget > attackRange || !canSeeTarget)
            {
                if (path == null)
                {
                    mob.getNavigation().createPath(targetPos, 1);
                    if (path != null) mob.getNavigation().moveTo(path, speed);
                }
            }
            // Otherwise, start the attack!
            else
            {
                mob.getNavigation().stop();
                path = null;
                attackTimer = adjustedTickDelay(attackStages[0]);
            }
        }
        else
        {
            attackTimer--;
        }
        // Turn towards the target.
        if (attackState <= turnDuringState && targetPos != null)
        {
            turnTowardsTarget(targetPos.getCenter());
        }
        int previousAttackState = attackState;
        attackState = calculateCurrentAttackState(attackTimer);
        actor.setAttackState(attackState);
        // When we actually change state to one where we should attack, do the actual attack.
        if (previousAttackState != attackState)
        {
            attack();
        }
    }

    private int calculateCurrentAttackState(int pAttackTimer)
    {
        if (pAttackTimer <= 0)
            return 0;
        for (int i = 1; i < attackStages.length; i++)
        {
            if (attackState > i) continue;
            if (pAttackTimer >= attackStages[i])
                return i;
        }
        return attackStages.length;
    }

    private void turnTowardsTarget(Vec3 pos)
    {
        double targetAngle = ExtraMath.getYawBetweenPoints(mob.position(), pos) * Mth.RAD_TO_DEG;
        mob.setYRot((float)targetAngle);
        mob.setYBodyRot(mob.getYRot());
    }

    private void attack() {
        actor.tryAttack();
    }

    @Override
    protected double getAttackReachSqr(LivingEntity pAttackTarget) {
        if (attackRange <= 9)
            return (this.mob.getBbWidth() * 2.0F * this.mob.getBbWidth() * 2.0F + pAttackTarget.getBbWidth());
        return attackRange;
    }
}
