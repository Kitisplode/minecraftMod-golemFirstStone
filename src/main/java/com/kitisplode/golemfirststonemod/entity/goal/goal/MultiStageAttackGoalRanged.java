package com.kitisplode.golemfirststonemod.entity.goal.goal;

import com.kitisplode.golemfirststonemod.entity.entity.IEntityWithDelayedMeleeAttack;
import com.kitisplode.golemfirststonemod.util.ExtraMath;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MultiStageAttackGoalRanged extends MeleeAttackGoal
{
    private Path path;
    private long lastUpdateTime;

    private long targetOutVisionTimer;
    private final int targetOutVisionTime = 20 * 5;

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

    public MultiStageAttackGoalRanged(IEntityWithDelayedMeleeAttack pMob, double pSpeed, boolean pauseWhenMobIdle, double pAttackRange, int[] pAttackStages, int pTurnDuringState, int pCooldownTime)
    {
        super((PathAwareEntity) pMob,pSpeed, pauseWhenMobIdle);
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
        this(pMob, pSpeed, pauseWhenMobIdle, pAttackRange, pAttackStages, 0, 0);
    }

    @Override
    public boolean canStart()
    {
        long l = this.mob.getWorld().getTime();
        if (l - this.lastUpdateTime < 20L) {
            return false;
        }
        this.lastUpdateTime = l;
        LivingEntity target = this.mob.getTarget();
        if (target == null) {
            return false;
        }
        if (!target.isAlive()) {
            return false;
        }
        this.path = this.mob.getNavigation().findPathTo(target, 0);
        if (this.path != null) {
            return true;
        }

        Vec3d distanceFlattened = new Vec3d(target.getX() - this.mob.getX(), 0, target.getZ() - this.mob.getZ());
		double distanceFlatSquared = distanceFlattened.lengthSquared();
        return this.getSquaredMaxAttackDistance(target) >= distanceFlatSquared;//this.mob.squaredDistanceTo(target.getX(), target.getY(), target.getZ());
    }

    @Override
    public boolean shouldContinue()
    {
        if (attackState > 0) return true;
        if (targetOutVisionTimer >= targetOutVisionTime) return false;
        return super.shouldContinue();
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

    @Override
    public boolean shouldRunEveryTick() {
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
            boolean canSeeTarget = this.mob.canSee(target);
            if (!canSeeTarget)
            {
                targetOutVisionTimer++;
            }
            else
                targetOutVisionTimer = 0;
            double distanceToTarget = this.mob.getSquaredDistanceToAttackPosOf(target);
            // Approach the target if we're not in attack range (can't beat them up without getting closer)
            if (distanceToTarget > attackRange || !canSeeTarget)
            {
                if (targetX == null || targetY == null || targetZ == null || target.squaredDistanceTo(targetX, targetY, targetZ) >= 1.0)
                {
                    targetX = target.getX();
                    targetY = target.getY();
                    targetZ = target.getZ();
                    mob.getNavigation().startMovingTo(target, speed);
                }
            }
            // Otherwise, start the attack!
            else
            {
                mob.getNavigation().stop();
                targetX = null;
                targetY = null;
                targetZ = null;
                attackTimer = getTickCount(attackStages[0]);
            }
        }
        else
        {
            attackTimer--;
        }
        // Turn towards the target.
        if (attackState <= turnDuringState && target != null)
        {
            this.mob.getLookControl().lookAt(target, 30.0f, 30.0f);
            turnTowardsTarget(target);
            if (Math.abs(mob.getBodyYaw() - ExtraMath.getYawBetweenPoints(mob.getPos(), target.getPos()) * MathHelper.DEGREES_PER_RADIAN) > 15)
            {
                attackTimer++;
            }
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
        double targetAngle = ExtraMath.getYawBetweenPoints(mob.getPos(), target.getPos()) * MathHelper.DEGREES_PER_RADIAN;
        mob.setYaw((float)targetAngle);
        mob.setBodyYaw(mob.getYaw());
    }

    private void attack()
    {
        actor.tryAttack();
    }

    @Override
    protected double getSquaredMaxAttackDistance(LivingEntity entity) {
        if (attackRange <= 9)
            return this.mob.getWidth() * 2.0f * (this.mob.getWidth() * 2.0f) + entity.getWidth();
        return attackRange;
    }
}
