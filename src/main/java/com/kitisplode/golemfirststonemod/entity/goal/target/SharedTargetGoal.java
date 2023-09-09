package com.kitisplode.golemfirststonemod.entity.goal.target;

import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SharedTargetGoal<R extends Mob, T extends LivingEntity> extends ActiveTargetGoalBiggerY<T>
{
    private final Class<R> sharedClass;
    private final IEntityDandoriFollower dandoriFollower;

    public SharedTargetGoal(Mob mob, Class<R> sharedClass, Class<T> targetClass, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate, @Nullable Predicate<LivingEntity> targetPredicate, float yRange )
    {
        super(mob, targetClass, reciprocalChance, checkVisibility, checkCanNavigate, targetPredicate, yRange);
        assert(mob instanceof IEntityDandoriFollower);
        this.dandoriFollower = (IEntityDandoriFollower) mob;
        this.sharedClass = sharedClass;
    }

    protected void findClosestTarget()
    {
        ArrayList<EntityDistanceNode> nodeList = new ArrayList<>();
        List<T> entityList;
        if (this.targetType == Player.class || this.targetType == ServerPlayer.class)
        {
            entityList = (List<T>) this.mob.level().players();
        }
        else
        {
            entityList = this.mob.level().getEntitiesOfClass(this.targetType, this.getTargetSearchArea(this.getFollowDistance()), livingEntity -> true);
        }
        for (T entity : entityList)
        {
            if (!this.targetConditions.test(this.mob, entity)) continue;
            double d = this.mob.getEyePosition().distanceToSqr(entity.position());
            // Loop through the node list and find the place to put this entity.
            int i = 0;
            for (; i < nodeList.size(); i++)
            {
                if (d < nodeList.get(i).distance) break;
            }
            nodeList.add(i, new EntityDistanceNode(entity, d));
        }
        // Now that we have the node list, go through them and see if we can find one that is not claimed.
        for (EntityDistanceNode node : nodeList)
        {
            List<R> sharedEntityList = this.mob.level().getEntitiesOfClass(this.sharedClass,
                    this.getTargetSearchArea(this.getFollowDistance()),
                    entity -> entity instanceof IEntityDandoriFollower entityDandori
                            && entity.getTarget() == node.entity
                            && entityDandori.getOwner() == this.dandoriFollower.getOwner());
            if (sharedEntityList.isEmpty())
            {
                this.target = node.entity;
                return;
            }
        }
        if (!nodeList.isEmpty())
        {
            this.target = nodeList.get(0).entity;
        }
    }

    class EntityDistanceNode
    {
        public final T entity;
        public final double distance;
        public EntityDistanceNode(T entity, double distance)
        {
            this.entity = entity;
            this.distance = distance;
        }
    }
}
