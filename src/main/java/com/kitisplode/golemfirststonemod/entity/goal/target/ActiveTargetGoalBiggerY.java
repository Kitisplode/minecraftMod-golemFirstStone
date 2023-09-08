package com.kitisplode.golemfirststonemod.entity.goal.target;

import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class ActiveTargetGoalBiggerY<T extends LivingEntity> extends ActiveTargetGoal
{
    private final float yRange;

    public ActiveTargetGoalBiggerY(MobEntity mob, Class<T> targetClass, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate, @Nullable Predicate<LivingEntity> targetPredicate,
                                   float pYRange)
    {
        super(mob, targetClass, reciprocalChance, checkVisibility, checkCanNavigate, targetPredicate);
        yRange = pYRange;
    }

    @Override
    protected Box getSearchBox(double distance) {
        return this.mob.getBoundingBox().expand(distance, yRange, distance);
    }

    @Override
    protected double getFollowRange() {
        if (this.mob instanceof IEntityDandoriFollower) return ((IEntityDandoriFollower)this.mob).getTargetRange();
        return this.mob.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
    }
}
