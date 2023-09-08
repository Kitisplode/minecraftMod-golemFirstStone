package com.kitisplode.golemfirststonemod.entity.goal.action;

import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class DandoriMoveToDeployPositionGoal extends Goal
{
    private final IEntityDandoriFollower dandoriFollower;
    private final MobEntity mob;
    private final double proximityDistance;
    private final double speed;

    public DandoriMoveToDeployPositionGoal(MobEntity mob, double proximityDistance, double speed)
    {
        this.mob = mob;
        assert(mob instanceof IEntityDandoriFollower);
        this.dandoriFollower = (IEntityDandoriFollower) mob;
        this.proximityDistance = proximityDistance;
        this.speed = speed;
        this.setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart()
    {
        BlockPos bp = this.dandoriFollower.getDeployPosition();
        return bp != null && this.isTooFarFrom(bp, this.proximityDistance);
    }

    @Override
    public void stop() {
        this.dandoriFollower.setDeployPosition(null);
        this.mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        BlockPos blockPos = this.dandoriFollower.getDeployPosition();
        if (blockPos != null && this.mob.getNavigation().isIdle()) {
            if (this.isTooFarFrom(blockPos, 10.0)) {
                Vec3d vec3d = new Vec3d((double)blockPos.getX() - this.mob.getX(), (double)blockPos.getY() - this.mob.getY(), (double)blockPos.getZ() - this.mob.getZ()).normalize();
                Vec3d vec3d2 = vec3d.multiply(10.0).add(this.mob.getX(), this.mob.getY(), this.mob.getZ());
                this.mob.getNavigation().startMovingTo(vec3d2.x, vec3d2.y, vec3d2.z, this.speed);
            } else {
                this.mob.getNavigation().startMovingTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), this.speed);
            }
        }
    }

    private boolean isTooFarFrom(BlockPos pos, double proximityDistance)
    {
        return !pos.isWithinDistance(this.mob.getPos(), proximityDistance);
    }
}
