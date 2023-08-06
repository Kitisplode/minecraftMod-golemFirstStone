package com.kitisplode.golemfirststonemod.entity.goal.target;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class PassiveTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal
{
    public PassiveTargetGoal(Mob pMob, Class<T> pTargetType, int pRandomInterval, boolean pMustSee, boolean pMustReach, @Nullable Predicate<LivingEntity> pTargetPredicate)
    {
        super(pMob, pTargetType, pRandomInterval, pMustSee, pMustReach, pTargetPredicate);
        this.targetConditions = TargetingConditions.forNonCombat().range(this.getFollowDistance()).selector(pTargetPredicate);
    }
}
