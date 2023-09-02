package com.kitisplode.golemfirststonemod.entity.goal.action;

import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityCanAttackBlocks;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDelayedMeleeAttack;
import com.kitisplode.golemfirststonemod.util.ExtraMath;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class MultiStageAttackBlockGoalRanged extends MeleeAttackGoal
{
    private Path path;
    private long lastUpdateTime;

    private final IEntityWithDelayedMeleeAttack actor;
    private final IEntityCanAttackBlocks attackBlockser;
    private int attackState;
    private int attackTimer;
    private final double attackRange;
    private final double speed;
    private final int[] attackStages;
    private final int turnDuringState;

    public MultiStageAttackBlockGoalRanged(IEntityWithDelayedMeleeAttack pMob, double pSpeed, boolean pauseWhenMobIdle, double pAttackRange, int[] pAttackStages, int pTurnDuringState)
    {
        super((PathAwareEntity) pMob,pSpeed, pauseWhenMobIdle);
        assert(pMob instanceof IEntityCanAttackBlocks);
        actor = pMob;
        attackBlockser = (IEntityCanAttackBlocks) pMob;
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

    @Override
    public boolean canStart()
    {
        if (this.mob.hasPassengers()) return false;

        long l = this.mob.getWorld().getTime();
        if (l - this.lastUpdateTime < 20L) {
            return false;
        }
        this.lastUpdateTime = l;
        BlockPos targetPos = this.attackBlockser.getBlockTarget();
        if (targetPos == null) {
            return false;
        }
        if (!this.attackBlockser.canTargetBlock(targetPos)) {
            return false;
        }
        this.path = this.mob.getNavigation().findPathTo(targetPos, 0);
        return this.path != null;
    }

    @Override
    public boolean shouldContinue()
    {
        if (attackState > 0) return true;
        return this.attackBlockser.canTargetBlock(this.attackBlockser.getBlockTarget());
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

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick()
    {
        BlockPos targetPos = this.attackBlockser.getBlockTarget();
        // If we're not attacking, try to attack if we can.
        if (attackTimer <= 0)
        {
            if (targetPos == null)
            {
                return;
            }
            Vec3d targetCenter = targetPos.toCenterPos();
            double distanceToTarget = this.mob.squaredDistanceTo(targetCenter);
            BlockHitResult ray = this.mob.getWorld().raycast(new RaycastContext(this.mob.getEyePos(), targetCenter, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.ANY, this.mob));
            boolean canSeeTarget = !ray.getBlockPos().isWithinDistance(this.mob.getEyePos(), distanceToTarget - 1);
//             Approach the target if we're not in attack range (can't beat them up without getting closer)
            if (distanceToTarget > attackRange || !canSeeTarget)
            {
                if (path == null)
                {
                    path = this.mob.getNavigation().findPathTo(targetPos, 1);
                    if (path != null)
                    {
                        this.mob.getNavigation().startMovingAlong(path, speed);
                    }
                }
            }
            // Otherwise, start the attack!
            else
            {
                mob.getNavigation().stop();
                path = null;
                attackTimer = getTickCount(attackStages[0]);
            }
        }
        else
        {
            attackTimer--;
        }
        // Turn towards the target.
        if (attackState <= turnDuringState && targetPos != null)
        {
//            this.mob.getLookControl().lookAt(target, 30.0f, 30.0f);
            turnTowardsTarget(targetPos.toCenterPos());
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

    private void turnTowardsTarget(Vec3d pos)
    {
        double targetAngle = ExtraMath.getYawBetweenPoints(mob.getPos(), pos) * MathHelper.DEGREES_PER_RADIAN;
        mob.setYaw((float)targetAngle);
        mob.setBodyYaw(mob.getYaw());
    }

    private void attack()
    {
        attackBlockser.tryAttackBlock();
    }

    @Override
    protected double getSquaredMaxAttackDistance(LivingEntity entity) {
        if (attackRange <= 9)
            return this.mob.getWidth() * 2.0f * (this.mob.getWidth() * 2.0f) + entity.getWidth();
        return attackRange;
    }
}
