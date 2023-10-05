package com.kitisplode.golemfirststonemod.entity.goal.target;

import com.kitisplode.golemfirststonemod.sound.ModSounds;
import com.kitisplode.golemfirststonemod.util.ExtraMath;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class PrisonGolemTargetSpotlightGoal<T extends LivingEntity> extends NearestAttackableTargetGoal
{
    public PrisonGolemTargetSpotlightGoal(Mob pMob, Class<T> pTargetType, boolean pMustSee)
    {
        super(pMob, pTargetType, pMustSee);
    }

    public PrisonGolemTargetSpotlightGoal(Mob pMob, Class<T> pTargetType, boolean pMustSee, Predicate pTargetPredicate)
    {
        super(pMob, pTargetType, pMustSee, pTargetPredicate);
    }

    public PrisonGolemTargetSpotlightGoal(Mob pMob, Class<T> pTargetType, boolean pMustSee, boolean pMustReach)
    {
        super(pMob, pTargetType, pMustSee, pMustReach);
    }

    public PrisonGolemTargetSpotlightGoal(Mob pMob, Class<T> pTargetType, int pRandomInterval, boolean pMustSee, boolean pMustReach, @Nullable Predicate pTargetPredicate)
    {
        super(pMob, pTargetType, pRandomInterval, pMustSee, pMustReach, pTargetPredicate);
    }

    public void start()
    {
        super.start();
        this.mob.playSound(ModSounds.ENTITY_GOLEM_PRISON_ALERT.get(), 1.25f, 1.0f);
    }

    protected void findTarget() {
//        if (this.targetType != Player.class && this.targetType != ServerPlayer.class)
        {
            this.target = this.mob.level().getNearestEntity(this.mob.level().getEntitiesOfClass(this.targetType, this.getTargetSearchArea(6), (entity) ->
            {
                LivingEntity livingEntity = (LivingEntity) entity;
                if (!this.mob.canAttack(livingEntity)) return false;
                double angleRange = 45;
                if (livingEntity.isCrouching()) angleRange = 30;
                double angle = ExtraMath.getYawBetweenPoints(this.mob.position(), entity.position()) * Mth.RAD_TO_DEG;
                return (Math.abs(ExtraMath.getAngleDiff(this.mob.getYHeadRot(), angle)) < angleRange);
            }), this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
        }
//        else {
//            this.target = this.mob.level().getNearestPlayer(this.targetConditions, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
//        }

    }
}
