package com.kitisplode.golemfirststonemod.entity.goal.action;

import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityTargetsItems;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;

public class CollectTargetItemGoal extends Goal
{
    private final MobEntity mob;
    private final IEntityTargetsItems mobThatTargetsItems;
    private final double speed;

    private Path path;
    private Double x;
    private Double y;
    private Double z;

    public CollectTargetItemGoal(MobEntity mob, double speed)
    {
        assert(mob instanceof IEntityTargetsItems);
        this.mob = mob;
        this.mobThatTargetsItems = (IEntityTargetsItems) mob;
        this.speed = speed;
    }

    @Override
    public boolean canStart()
    {
        ItemEntity target = mobThatTargetsItems.getItemTarget();
        if (target == null) return false;
        if (!mobThatTargetsItems.canTargetItem(target)) return false;

        this.path = this.mob.getNavigation().findPathTo(target, 0);
        return this.path != null;
    }

    @Override
    public boolean shouldContinue()
    {
        return this.canStart();
    }

    @Override
    public void start()
    {
        super.start();
        x = null;
        y = null;
        z = null;
    }

    @Override
    public void stop()
    {
        super.stop();
        x = null;
        y = null;
        z = null;
        mob.getNavigation().stop();
    }

    @Override
    public void tick()
    {
        ItemEntity target = this.mobThatTargetsItems.getItemTarget();
        if (target == null) return;
        double distanceToTarget = this.mob.squaredDistanceTo(target);
        if (distanceToTarget > 1)
        {
            if (x == null || y == null || z == null || target.squaredDistanceTo(x, y, z) >= 1.0)
            {
                x = target.getX();
                y = target.getY();
                z = target.getZ();
                mob.getNavigation().startMovingTo(target, speed);
            }
        }
        else
        {
            this.mobThatTargetsItems.loot(target);
            this.mobThatTargetsItems.setItemTarget(null);
            x = null; y = null; z = null;
        }
    }
}
