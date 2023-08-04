package com.kitisplode.golemfirststonemod.entity.goal.target;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class PassiveTargetGoal<T extends LivingEntity> extends ActiveTargetGoal
{

    public PassiveTargetGoal(MobEntity mob, Class<T> targetClass, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate, @Nullable Predicate<LivingEntity> targetPredicate) {
        super(mob, targetClass, reciprocalChance, checkVisibility, checkCanNavigate, targetPredicate);
        this.targetPredicate = TargetPredicate.createNonAttackable().setBaseMaxDistance(this.getFollowRange()).setPredicate(targetPredicate);
    }
}
