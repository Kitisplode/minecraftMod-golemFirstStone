package com.kitisplode.golemfirststonemod.entity.goal.goal;

import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDelayedMeleeAttack;
import com.kitisplode.golemfirststonemod.util.ExtraMath;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

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
        super((PathfinderMob) pMob,pSpeed, pauseWhenMobIdle);
        actor = pMob;
        speed = pSpeed;
        attackState = 0;
        attackTimer = 0;
        attackRange = pAttackRange;
        attackStages = pAttackStages.clone();
    }

    @Override
    public boolean canContinueToUse()
    {
        if (attackState > 0) return true;
        return super.canContinueToUse();
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
            turnTowardsTarget(target);
            this.mob.getLookControl().setLookAt(target, 30.0f, 30.0f);
            double distanceToTarget = this.mob.getPerceivedTargetDistanceSquareForMeleeAttack(target);
            // Approach the target if we're not in attack range (can't beat them up without getting closer)
            if (distanceToTarget > attackRange)
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
        double targetAngle = ExtraMath.getYawBetweenPoints(mob.position(), target.position());
        mob.setYRot(Mth.lerp(0.5f, (float)mob.getYRot(), (float)targetAngle * Mth.RAD_TO_DEG));
    }

    private void attack() {
        actor.tryAttack();
    }
}
