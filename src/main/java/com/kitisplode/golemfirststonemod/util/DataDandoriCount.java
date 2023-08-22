package com.kitisplode.golemfirststonemod.util;

import com.kitisplode.golemfirststonemod.entity.entity.EntityPawn;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemFirstBrick;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemFirstDiorite;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemFirstOak;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemFirstStone;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.SnowGolemEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataDandoriCount
{
    private static final double dandoriSeeRange = 36;
    public enum FOLLOWER_TYPE {IRON, SNOW, FIRST_STONE, FIRST_OAK, FIRST_BRICK, FIRST_DIORITE, PAWN_BLUE, PAWN_RED, PAWN_YELLOW};
    private Map<FOLLOWER_TYPE, Integer> followerCounts = new HashMap<>();
    private int totalCount = 0;

    public void updateNumberOfFollowers(LivingEntity player)
    {
        // Get the list of followers.
        List<LivingEntity> followerList = player.getWorld().getEntitiesByClass(LivingEntity.class, player.getBoundingBox().expand(dandoriSeeRange), entity ->
        {
            if (!(entity instanceof IEntityDandoriFollower)) return false;
            if (!((IEntityDandoriFollower) entity).getDandoriState()) return false;
            return ((IEntityDandoriFollower) entity).isOwner(player);
        });
        // Clear the current follower map.
        followerCounts.clear();
        totalCount = 0;
        // Go through the list and update the follower map.
        for (LivingEntity entity : followerList)
        {
            FOLLOWER_TYPE key = FOLLOWER_TYPE.IRON;
            if (entity instanceof SnowGolemEntity) key = FOLLOWER_TYPE.SNOW;
            if (entity instanceof EntityGolemFirstStone) key = FOLLOWER_TYPE.FIRST_STONE;
            if (entity instanceof EntityGolemFirstOak) key = FOLLOWER_TYPE.FIRST_OAK;
            if (entity instanceof EntityGolemFirstBrick) key = FOLLOWER_TYPE.FIRST_BRICK;
            if (entity instanceof EntityGolemFirstDiorite) key = FOLLOWER_TYPE.FIRST_DIORITE;

            if (entity instanceof EntityPawn)
            {
                int pawnType = ((EntityPawn) entity).getPawnType();
                if (pawnType == EntityPawn.PAWN_TYPES.PIK_BLUE.ordinal()) key = FOLLOWER_TYPE.PAWN_BLUE;
                if (pawnType == EntityPawn.PAWN_TYPES.PIK_PINK.ordinal()) key = FOLLOWER_TYPE.PAWN_RED;
                if (pawnType == EntityPawn.PAWN_TYPES.PIK_YELLOW.ordinal()) key = FOLLOWER_TYPE.PAWN_YELLOW;
            }
            totalCount++;
            followerCounts.merge(key, 1, Integer::sum);
        }
    }

    public int getTotalCount()
    {
        return totalCount;
    }

    public int getFollowerCount(FOLLOWER_TYPE followerType)
    {
        Integer count = followerCounts.get(followerType);
        if (count == null) return 0;
        return count;
    }
}
