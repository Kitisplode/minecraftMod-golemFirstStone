package com.kitisplode.golemfirststonemod.mixin.entity;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDandoriCount;
import com.kitisplode.golemfirststonemod.util.DataDandoriCount;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(value = Player.class)
public abstract class MixinPlayer extends LivingEntity implements IEntityWithDandoriCount
{
    private static final MobEffectInstance weaknessEffect = new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 5 * 20, 3);

    private static final EntityDataAccessor<Integer> DANDORI_TOTAL = SynchedEntityData.defineId(MixinPlayer.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DANDORI_PAWN_BLUE = SynchedEntityData.defineId(MixinPlayer.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DANDORI_PAWN_RED = SynchedEntityData.defineId(MixinPlayer.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DANDORI_PAWN_YELLOW = SynchedEntityData.defineId(MixinPlayer.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DANDORI_IRON = SynchedEntityData.defineId(MixinPlayer.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DANDORI_SNOW = SynchedEntityData.defineId(MixinPlayer.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DANDORI_COBBLE = SynchedEntityData.defineId(MixinPlayer.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DANDORI_FIRST_STONE = SynchedEntityData.defineId(MixinPlayer.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DANDORI_FIRST_OAK = SynchedEntityData.defineId(MixinPlayer.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DANDORI_FIRST_BRICK = SynchedEntityData.defineId(MixinPlayer.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DANDORI_FIRST_DIORITE = SynchedEntityData.defineId(MixinPlayer.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DANDORI_CURRENT_TYPE = SynchedEntityData.defineId(MixinPlayer.class, EntityDataSerializers.INT);

    private DataDandoriCount dandoriCount = new DataDandoriCount();
    private boolean recountDandori = false;

    private static final DataDandoriCount.FOLLOWER_TYPE[] FOLLOWER_TYPES_VALUES = DataDandoriCount.FOLLOWER_TYPE.values();

    protected MixinPlayer(EntityType<? extends LivingEntity> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
    }

    @Inject(method = ("defineSynchedData"), at = @At("TAIL"))
    protected void inject_defineSynchedData(CallbackInfo ci)
    {
        if (!this.entityData.hasItem(DANDORI_TOTAL)) this.entityData.define(DANDORI_TOTAL, 0);
        if (!this.entityData.hasItem(DANDORI_PAWN_BLUE)) this.entityData.define(DANDORI_PAWN_BLUE, 0);
        if (!this.entityData.hasItem(DANDORI_PAWN_RED)) this.entityData.define(DANDORI_PAWN_RED, 0);
        if (!this.entityData.hasItem(DANDORI_PAWN_YELLOW)) this.entityData.define(DANDORI_PAWN_YELLOW, 0);
        if (!this.entityData.hasItem(DANDORI_IRON)) this.entityData.define(DANDORI_IRON, 0);
        if (!this.entityData.hasItem(DANDORI_SNOW)) this.entityData.define(DANDORI_SNOW, 0);
        if (!this.entityData.hasItem(DANDORI_COBBLE)) this.entityData.define(DANDORI_COBBLE, 0);
        if (!this.entityData.hasItem(DANDORI_FIRST_STONE)) this.entityData.define(DANDORI_FIRST_STONE, 0);
        if (!this.entityData.hasItem(DANDORI_FIRST_OAK)) this.entityData.define(DANDORI_FIRST_OAK, 0);
        if (!this.entityData.hasItem(DANDORI_FIRST_BRICK)) this.entityData.define(DANDORI_FIRST_BRICK, 0);
        if (!this.entityData.hasItem(DANDORI_FIRST_DIORITE)) this.entityData.define(DANDORI_FIRST_DIORITE, 0);
        if (!this.entityData.hasItem(DANDORI_CURRENT_TYPE)) this.entityData.define(DANDORI_CURRENT_TYPE, 0);
    }

    @Override
    public void push(Entity entity)
    {
        // Don't push away from our followers.
        if (entity instanceof IEntityDandoriFollower && ((IEntityDandoriFollower) entity).getOwner() == this) return;
        super.push(entity);
    }

    @Inject(method="tick", at = @At("tail"))
    protected void inject_tick(CallbackInfo ci)
    {
        // Apply weakness effect to players under the bedrock.
        if (this.getY() < -64)
        {
            this.addEffect(new MobEffectInstance(weaknessEffect));
        }
    }

    public void recountDandori()
    {
        if (!recountDandori) return;
        dandoriCount.updateNumberOfFollowers(this);
        this.setTrackedInt(DANDORI_TOTAL, dandoriCount.getTotalCount());
        this.setTrackedInt(DANDORI_PAWN_BLUE, dandoriCount.getFollowerCount(DataDandoriCount.FOLLOWER_TYPE.PAWN_BLUE));
        this.setTrackedInt(DANDORI_PAWN_RED, dandoriCount.getFollowerCount(DataDandoriCount.FOLLOWER_TYPE.PAWN_RED));
        this.setTrackedInt(DANDORI_PAWN_YELLOW, dandoriCount.getFollowerCount(DataDandoriCount.FOLLOWER_TYPE.PAWN_YELLOW));
        this.setTrackedInt(DANDORI_IRON, dandoriCount.getFollowerCount(DataDandoriCount.FOLLOWER_TYPE.IRON));
        this.setTrackedInt(DANDORI_SNOW, dandoriCount.getFollowerCount(DataDandoriCount.FOLLOWER_TYPE.SNOW));
        this.setTrackedInt(DANDORI_COBBLE, dandoriCount.getFollowerCount(DataDandoriCount.FOLLOWER_TYPE.COBBLE));
        this.setTrackedInt(DANDORI_FIRST_STONE, dandoriCount.getFollowerCount(DataDandoriCount.FOLLOWER_TYPE.FIRST_STONE));
        this.setTrackedInt(DANDORI_FIRST_OAK, dandoriCount.getFollowerCount(DataDandoriCount.FOLLOWER_TYPE.FIRST_OAK));
        this.setTrackedInt(DANDORI_FIRST_BRICK, dandoriCount.getFollowerCount(DataDandoriCount.FOLLOWER_TYPE.FIRST_BRICK));
        this.setTrackedInt(DANDORI_FIRST_DIORITE, dandoriCount.getFollowerCount(DataDandoriCount.FOLLOWER_TYPE.FIRST_DIORITE));
        DataDandoriCount.FOLLOWER_TYPE currentType = this.getDandoriCurrentType();
        if (currentType != null)
        {
            int newType = dandoriCount.getNextCountWithFollowers(currentType.ordinal());
            if (newType == -1) newType = dandoriCount.getPrevCountWithFollowers(currentType.ordinal());
            this.setTrackedInt(DANDORI_CURRENT_TYPE, newType);
        }
        recountDandori = false;
    }


    public void setRecountDandori()
    {
        recountDandori = true;
    }

    public int getTotalDandoriCount()
    {
        return this.entityData.get(DANDORI_TOTAL);
    }
    public int getDandoriCountBlue()
    {
        return this.entityData.get(DANDORI_PAWN_BLUE);
    }
    public int getDandoriCountRed()
    {
        return this.entityData.get(DANDORI_PAWN_RED);
    }
    public int getDandoriCountYellow()
    {
        return this.entityData.get(DANDORI_PAWN_YELLOW);
    }
    public int getDandoriCountIron()
    {
        return this.entityData.get(DANDORI_IRON);
    }
    public int getDandoriCountSnow()
    {
        return this.entityData.get(DANDORI_SNOW);
    }
    public int getDandoriCountCobble()
    {
        return this.entityData.get(DANDORI_COBBLE);
    }
    public int getDandoriCountFirstStone()
    {
        return this.entityData.get(DANDORI_FIRST_STONE);
    }
    public int getDandoriCountFirstOak()
    {
        return this.entityData.get(DANDORI_FIRST_OAK);
    }
    public int getDandoriCountFirstBrick()
    {
        return this.entityData.get(DANDORI_FIRST_BRICK);
    }
    public int getDandoriCountFirstDiorite()
    {
        return this.entityData.get(DANDORI_FIRST_DIORITE);
    }

    public void nextDandoriCurrentType()
    {
        int value = this.entityData.get(DANDORI_CURRENT_TYPE);
        value++;
        if (value >= FOLLOWER_TYPES_VALUES.length) value = -1;
        if (value >= 0) this.setTrackedInt(DANDORI_CURRENT_TYPE, dandoriCount.getNextCountWithFollowers(value));
        else this.setTrackedInt(DANDORI_CURRENT_TYPE, value);
    }
    public DataDandoriCount.FOLLOWER_TYPE getDandoriCurrentType()
    {
        int value = this.entityData.get(DANDORI_CURRENT_TYPE);
        if (value >= 0 && value < FOLLOWER_TYPES_VALUES.length)
            return FOLLOWER_TYPES_VALUES[value];
        return null;
    }

    private void setTrackedInt(EntityDataAccessor<Integer> key, int count)
    {
        this.entityData.set(key, count);
    }
}
