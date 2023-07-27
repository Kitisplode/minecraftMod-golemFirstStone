package com.kitisplode.golemfirststonemod.entity.goal;

import com.kitisplode.golemfirststonemod.entity.custom.IEntityWithDelayedMeleeAttack;
import com.kitisplode.golemfirststonemod.util.ExtraMath;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.MathHelper;

public class MultiStageAttackGoal extends MeleeAttackGoal
{
    private final IEntityWithDelayedMeleeAttack actor;
    private int attackState;
    private int attackTimer;
    private final double attackRange;
    private final double speed;
    private Double targetX;
    private Double targetY;
    private Double targetZ;
    private final int[] attackStages;

    public MultiStageAttackGoal(IEntityWithDelayedMeleeAttack pMob, double pSpeed, boolean pauseWhenMobIdle, double pAttackRange, int[] pAttackStages)
    {
        super((PathAwareEntity) pMob,pSpeed, pauseWhenMobIdle);
        actor = pMob;
        speed = pSpeed;
        attackState = 0;
        attackTimer = 0;
        attackRange = pAttackRange;
        attackStages = pAttackStages.clone();
    }

    @Override
    public boolean canStart()
    {
        return super.canStart();
    }

    @Override
    public boolean shouldContinue()
    {
        if (attackState > 0) return true;
        return super.shouldContinue();
    }

    @Override
    public void start()
    {
        super.start();
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
            turnTowardsTarget(target);
            this.mob.getLookControl().lookAt(target, 30.0f, 30.0f);
            double distanceToTarget = this.mob.getSquaredDistanceToAttackPosOf(target);
            // Approach the target if we're not in attack range (can't beat them up without getting closer)
            if (distanceToTarget > attackRange)
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
            attackTimer--;// = Math.max(attackTimer - 1, 0);
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
        double targetAngle = ExtraMath.getYawBetweenPoints(mob.getPos(), target.getPos());
        mob.setBodyYaw(MathHelper.lerp(0.5f, (float)mob.getBodyYaw(), (float)targetAngle * MathHelper.RADIANS_PER_DEGREE));
    }

    private void attack() {
        actor.tryAttack();
    }


}
