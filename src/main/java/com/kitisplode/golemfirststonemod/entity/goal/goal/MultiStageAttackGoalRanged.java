package com.kitisplode.golemfirststonemod.entity.goal.goal;

import com.kitisplode.golemfirststonemod.entity.entity.IEntityWithDelayedMeleeAttack;
import com.kitisplode.golemfirststonemod.util.ExtraMath;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class MultiStageAttackGoalRanged extends MeleeAttackGoal
{
    private Path path;
    private long lastUpdateTime;

    private long targetOutVisionTimer;
    private int targetOutVisionTime = 20 * 5;

    private final IEntityWithDelayedMeleeAttack actor;
    private int attackState;
    private int attackTimer;
    private final double attackRange;
    private final double speed;
    private Double targetX;
    private Double targetY;
    private Double targetZ;
    private final int[] attackStages;
    private final int turnDuringState;

    public MultiStageAttackGoalRanged(IEntityWithDelayedMeleeAttack pMob, double pSpeed, boolean pauseWhenMobIdle, double pAttackRange, int[] pAttackStages, int pTurnDuringState)
    {
        super((PathfinderMob) pMob,pSpeed, pauseWhenMobIdle);
        actor = pMob;
        speed = pSpeed;
        attackState = 0;
        attackTimer = 0;
        attackRange = pAttackRange;
        attackStages = pAttackStages.clone();
        turnDuringState = pTurnDuringState;
    }

    public MultiStageAttackGoalRanged(IEntityWithDelayedMeleeAttack pMob, double pSpeed, boolean pauseWhenMobIdle, double pAttackRange, int[] pAttackStages)
    {
        this(pMob, pSpeed, pauseWhenMobIdle, pAttackRange, pAttackStages, 0);
    }

    public boolean canUse() {
        long i = this.mob.level().getGameTime();
        if (i - this.lastUpdateTime < 20L) {
            return false;
        }
        this.lastUpdateTime = i;
        LivingEntity target = this.mob.getTarget();
        if (target == null) {
            return false;
        }
        if (!target.isAlive()) {
            return false;
        }
        this.path = this.mob.getNavigation().createPath(target, 0);
        if (this.path != null) {
            return true;
        }

        Vec3 distanceFlattened = new Vec3(target.getX() - this.mob.getX(), 0, target.getZ() - this.mob.getZ());
        double distanceFlatSquared = distanceFlattened.lengthSqr();
        return this.getAttackReachSqr(target) >= distanceFlatSquared;//this.mob.distanceToSqr(target.getX(), target.getY(), target.getZ());

    }

    @Override
    public boolean canContinueToUse()
    {
        if (attackState > 0) return true;
        if (targetOutVisionTimer >= targetOutVisionTime) return false;
        return super.canContinueToUse();
    }

    @Override
    public void start()
    {
        super.start();
        targetOutVisionTimer = 0;
        attackTimer = 0;
        actor.setAttackState(0);
        targetX = null;
        targetY = null;
        targetZ = null;
    }

    @Override
    public void stop()
    {
        super.stop();
        targetOutVisionTimer = 0;
        attackTimer = 0;
        actor.setAttackState(0);
        targetX = null;
        targetY = null;
        targetZ = null;
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick()
    {
        LivingEntity target = this.mob.getTarget();
        // If we're not attacking, try to attack if we can.
        if (attackTimer <= 0)
        {
            if (target == null)
            {
                return;
            }
            // If we can't see the target, count down the timer
            boolean canSeeTarget = this.mob.hasLineOfSight(target);
            if (!canSeeTarget)
            {
                targetOutVisionTimer++;
            }
            else
                targetOutVisionTimer = 0;
            double distanceToTarget = this.mob.getPerceivedTargetDistanceSquareForMeleeAttack(target);
            // Approach the target if we're not in attack range (can't beat them up without getting closer)
            if (distanceToTarget > attackRange || !canSeeTarget)
            {
                if (targetX == null || targetY == null || targetZ == null || target.distanceToSqr(targetX, targetY, targetZ) >= 1.0)
                {
                    targetX = target.getX();
                    targetY = target.getY();
                    targetZ = target.getZ();
                    mob.getNavigation().moveTo(target, speed);
                }
            }
            // Otherwise, start the attack!
            else
            {
                mob.getNavigation().stop();
                targetX = null;
                targetY = null;
                targetZ = null;
                attackTimer = adjustedTickDelay(attackStages[0]);
            }
        }
        else
        {
            attackTimer--;
        }
        // Turn towards the target.
        if (attackState <= turnDuringState && target != null)
        {
            this.mob.getLookControl().setLookAt(target, 30.0f, 30.0f);
            turnTowardsTarget(target);
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

    private void turnTowardsTarget(LivingEntity target)
    {
        double targetAngle = ExtraMath.getYawBetweenPoints(mob.position(), target.position()) * Mth.RAD_TO_DEG;
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
