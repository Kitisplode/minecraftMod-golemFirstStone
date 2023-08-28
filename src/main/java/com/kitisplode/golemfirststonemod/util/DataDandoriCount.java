package com.kitisplode.golemfirststonemod.util;

import com.kitisplode.golemfirststonemod.entity.entity.golem.*;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SnowGolemEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataDandoriCount
{
    private static final double dandoriSeeRange = 36;
    public enum FOLLOWER_TYPE {FIRST_STONE, FIRST_OAK, FIRST_BRICK, FIRST_DIORITE, SNOW, IRON, COBBLE, PLANK, MOSSY, PAWN_RED, PAWN_YELLOW, PAWN_BLUE};
    private Map<FOLLOWER_TYPE, Integer> followerCounts = new HashMap<>();
    private int totalCount = 0;

    public void updateNumberOfFollowers(LivingEntity player)
    {
        // Get the list of followers.
        List<LivingEntity> followerList = player.getWorld().getEntitiesByClass(LivingEntity.class, player.getBoundingBox().expand(dandoriSeeRange), entity ->
        {
            if (!(entity instanceof IEntityDandoriFollower)) return false;
            if (!((IEntityDandoriFollower) entity).getDandoriState()) return false;
            return ((IEntityDandoriFollower) entity).getOwner() == player;
        });
        // Clear the current follower map.
        followerCounts.clear();
        totalCount = 0;
        // Go through the list and update the follower map.
        for (LivingEntity entity : followerList)
        {
            FOLLOWER_TYPE key = FOLLOWER_TYPE.IRON;
            if (entity instanceof SnowGolemEntity) key = FOLLOWER_TYPE.SNOW;
            if (entity instanceof EntityGolemCobble) key = FOLLOWER_TYPE.COBBLE;
            if (entity instanceof EntityGolemPlank) key = FOLLOWER_TYPE.PLANK;
            if (entity instanceof EntityGolemMossy) key = FOLLOWER_TYPE.MOSSY;
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

    public int getNextCountWithFollowers(int currentType)
    {
        final DataDandoriCount.FOLLOWER_TYPE[] FOLLOWER_TYPES_VALUES = DataDandoriCount.FOLLOWER_TYPE.values();
        if (currentType < 0 || currentType >= FOLLOWER_TYPES_VALUES.length) return -1;
        if (getTotalCount() <= 0) return -1;
        int i = currentType;
        for (; i < FOLLOWER_TYPES_VALUES.length; i++)
        {
            if (getFollowerCount(FOLLOWER_TYPES_VALUES[i]) > 0) break;
        }
        if (i >= FOLLOWER_TYPES_VALUES.length)
        {
            return -1;
        }
        return i;
    }

    public int getPrevCountWithFollowers(int currentType)
    {
        final DataDandoriCount.FOLLOWER_TYPE[] FOLLOWER_TYPES_VALUES = DataDandoriCount.FOLLOWER_TYPE.values();
        if (currentType < 0 || currentType >= FOLLOWER_TYPES_VALUES.length) return -1;
        if (getTotalCount() <= 0) return -1;
        int i = currentType;
        for (; i >= 0; i--)
        {
            if (getFollowerCount(FOLLOWER_TYPES_VALUES[i]) > 0) break;
        }
        return i;
    }

    public static boolean entityIsOfType(FOLLOWER_TYPE type, LivingEntity entity)
    {
        if (type == null) return true;
        if (type == FOLLOWER_TYPE.IRON && entity instanceof IronGolemEntity) return true;
        if (type == FOLLOWER_TYPE.SNOW && entity instanceof SnowGolemEntity) return true;
        if (type == FOLLOWER_TYPE.COBBLE && entity instanceof EntityGolemCobble) return true;
        if (type == FOLLOWER_TYPE.PLANK && entity instanceof EntityGolemPlank) return true;
        if (type == FOLLOWER_TYPE.MOSSY && entity instanceof EntityGolemMossy) return true;
        if (type == FOLLOWER_TYPE.FIRST_STONE && entity instanceof EntityGolemFirstStone) return true;
        if (type == FOLLOWER_TYPE.FIRST_OAK && entity instanceof EntityGolemFirstOak) return true;
        if (type == FOLLOWER_TYPE.FIRST_BRICK && entity instanceof EntityGolemFirstBrick) return true;
        if (type == FOLLOWER_TYPE.FIRST_DIORITE && entity instanceof EntityGolemFirstDiorite) return true;
        if (entity instanceof EntityPawn)
        {
            int pawnType = ((EntityPawn) entity).getPawnType();
            if (type == FOLLOWER_TYPE.PAWN_BLUE && pawnType == EntityPawn.PAWN_TYPES.PIK_BLUE.ordinal()) return true;
            if (type == FOLLOWER_TYPE.PAWN_RED && pawnType == EntityPawn.PAWN_TYPES.PIK_PINK.ordinal()) return true;
            if (type == FOLLOWER_TYPE.PAWN_YELLOW && pawnType == EntityPawn.PAWN_TYPES.PIK_YELLOW.ordinal()) return true;
            return false;
        }
        return false;
    }
}
