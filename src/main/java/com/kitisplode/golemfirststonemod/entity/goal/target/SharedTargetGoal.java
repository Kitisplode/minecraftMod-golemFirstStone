package com.kitisplode.golemfirststonemod.entity.goal.target;

import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SharedTargetGoal<R extends MobEntity, T extends LivingEntity> extends ActiveTargetGoalBiggerY<T>
{
    private final Class<R> sharedClass;
    private final IEntityDandoriFollower dandoriFollower;

    public SharedTargetGoal(MobEntity mob, Class<R> sharedClass, Class<T> targetClass, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate, @Nullable Predicate<LivingEntity> targetPredicate, float yRange )
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
        if (this.targetClass == PlayerEntity.class || this.targetClass == ServerPlayerEntity.class)
        {
            entityList = (List<T>) this.mob.getWorld().getPlayers();
        }
        else
        {
            entityList = this.mob.getWorld().getEntitiesByClass(this.targetClass, this.getSearchBox(this.getFollowRange()), livingEntity -> true);
        }
        for (T entity : entityList)
        {
            if (!this.targetPredicate.test(this.mob, entity)) continue;
            double d = this.mob.getEyePos().squaredDistanceTo(entity.getPos());
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
            List<R> sharedEntityList = this.mob.getWorld().getEntitiesByClass(this.sharedClass,
                    this.getSearchBox(this.getFollowRange()),
                    entity -> entity instanceof IEntityDandoriFollower entityDandori
                            && entity.getTarget() == node.entity
                            && entityDandori.getOwner() == this.dandoriFollower.getOwner());
            if (sharedEntityList.isEmpty())
            {
                this.targetEntity = node.entity;
                return;
            }
        }
        if (!nodeList.isEmpty())
        {
            this.targetEntity = nodeList.get(0).entity;
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
