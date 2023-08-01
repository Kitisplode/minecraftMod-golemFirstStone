package com.kitisplode.golemfirststonemod.entity.goal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class ActiveTargetGoalBiggerY<T extends LivingEntity> extends ActiveTargetGoal
{
    private float yRange;

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
}
