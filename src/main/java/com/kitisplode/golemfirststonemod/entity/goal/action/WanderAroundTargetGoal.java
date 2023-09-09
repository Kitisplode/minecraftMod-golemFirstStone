package com.kitisplode.golemfirststonemod.entity.goal.action;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class WanderAroundTargetGoal
    extends Goal
{
    private final PathfinderMob mob;
    @Nullable
    private LivingEntity target;
    private double x;
    private double y;
    private double z;
    private final double speed;
    private final float maxDistance;

    public WanderAroundTargetGoal(PathfinderMob mob, double speed, float maxDistance) {
        this.mob = mob;
        this.speed = speed;
        this.maxDistance = maxDistance;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        this.target = this.mob.getTarget();
        if (this.target == null) {
            return false;
        }
        if (this.target.distanceToSqr(this.mob) < (double)(this.maxDistance * this.maxDistance)) {
            return false;
        }
        Vec3 vec3d = DefaultRandomPos.getPosTowards(this.mob, 16, 7, this.target.position(), 1.5707963705062866);
        if (vec3d == null) {
            return false;
        }
        this.x = vec3d.x;
        this.y = vec3d.y;
        this.z = vec3d.z;
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.mob.getNavigation().isDone() && this.target.isAlive() && this.target.distanceToSqr(this.mob) > (double)(this.maxDistance * this.maxDistance);
    }

    @Override
    public void stop() {
        this.target = null;
    }

    @Override
    public void start() {
        this.mob.getNavigation().moveTo(this.x, this.y, this.z, this.speed);
    }
}
