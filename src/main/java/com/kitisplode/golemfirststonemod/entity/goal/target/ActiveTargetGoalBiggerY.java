package com.kitisplode.golemfirststonemod.entity.goal.target;

import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class ActiveTargetGoalBiggerY<T extends LivingEntity> extends NearestAttackableTargetGoal
{
    private final float yRange;


    public ActiveTargetGoalBiggerY(Mob pMob, Class pTargetType, int pRandomInterval, boolean pMustSee, boolean pMustReach, @Nullable Predicate pTargetPredicate,
                                   float pYRange)
    {
        super(pMob, pTargetType, pRandomInterval, pMustSee, pMustReach, pTargetPredicate);
        yRange = pYRange;
    }

    protected AABB getTargetSearchArea(double pTargetDistance) {
        return this.mob.getBoundingBox().inflate(pTargetDistance, yRange, pTargetDistance);
    }

    protected double getFollowDistance()
    {
        if (this.mob instanceof IEntityDandoriFollower) return ((IEntityDandoriFollower)this.mob).getTargetRange();
        return this.mob.getAttributeValue(Attributes.FOLLOW_RANGE);
    }
}
