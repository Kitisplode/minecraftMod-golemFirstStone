package com.kitisplode.golemfirststonemod.entity.goal.goal;

import com.kitisplode.golemfirststonemod.entity.entity.IEntityDandoriFollower;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class DandoriFollowGoal extends Goal
{
    private static final TargetPredicate TEMPTING_ENTITY_PREDICATE = TargetPredicate.createNonAttackable().setBaseMaxDistance(10.0);
    private final TargetPredicate predicate;
    protected final PathAwareEntity mob;
    private final IEntityDandoriFollower pik;
    private final double speed;
    @Nullable
    protected PlayerEntity closestPlayer;
    private boolean active;
    private final Ingredient food;
    private final double moveRange;
    private final double seeRange;

    public DandoriFollowGoal(IEntityDandoriFollower entity, double pSpeed, Ingredient food, double pRange, double pSeeRange)
    {
        this.mob = (PathAwareEntity) entity;
        pik = entity;
        this.speed = pSpeed;
        this.food = food;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
        this.moveRange = MathHelper.square(pRange);
        this.seeRange = pSeeRange;
        this.predicate = TEMPTING_ENTITY_PREDICATE.copy().setPredicate(this::isTemptedBy).setBaseMaxDistance(Math.max(10.0, this.seeRange));
    }

    @Override
    public boolean canStart()
    {
        // If it's an iron golem, only dandori player created golems.
        if (pik instanceof IronGolemEntity)
        {
            if (!((IronGolemEntity)mob).isPlayerCreated()) return false;
        }
        // Dandori only things that are in dandori mode.
        if (!pik.getDandoriState()) return false;
        // Get the nearest player that we should follow.
        this.closestPlayer = this.mob.getWorld().getClosestPlayer(this.predicate, this.mob);
        return this.closestPlayer != null;
    }

    private boolean isTemptedBy(LivingEntity entity)
    {
        return this.food.test(entity.getMainHandStack()) || this.food.test(entity.getOffHandStack());
    }

    @Override
    public boolean shouldContinue()
    {
        return this.canStart();
    }

    @Override
    public void start()
    {
        this.active = true;
        this.mob.getWorld().sendEntityStatus(this.mob, EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES);
    }

    @Override
    public void stop()
    {
        this.closestPlayer = null;
        this.mob.getNavigation().stop();
        this.active = false;
        this.pik.setDandoriState(false);
    }

    @Override
    public void tick()
    {
        this.mob.getLookControl().lookAt(this.closestPlayer, this.mob.getMaxHeadRotation() + 20, this.mob.getMaxLookPitchChange());
        if (this.mob.squaredDistanceTo(this.closestPlayer) < moveRange) {
            this.mob.getNavigation().stop();
        } else {
            this.mob.getNavigation().startMovingTo(this.closestPlayer, this.speed);
        }
    }

    public boolean isActive()
    {
        return this.active;
    }
}
