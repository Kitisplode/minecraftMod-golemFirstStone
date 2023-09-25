package com.kitisplode.golemfirststonemod.mixin.entity;

import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDandoriCount;
import com.kitisplode.golemfirststonemod.util.DataDandoriCount;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity implements IEntityWithDandoriCount
{
    private static final StatusEffectInstance weaknessEffect = new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 5 * 20, 3);

    private static final TrackedData<Integer> DANDORI_TOTAL = DataTracker.registerData(MixinPlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> DANDORI_PAWN_BLUE = DataTracker.registerData(MixinPlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> DANDORI_PAWN_RED = DataTracker.registerData(MixinPlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> DANDORI_PAWN_YELLOW = DataTracker.registerData(MixinPlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> DANDORI_IRON = DataTracker.registerData(MixinPlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> DANDORI_SNOW = DataTracker.registerData(MixinPlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> DANDORI_COBBLE = DataTracker.registerData(MixinPlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> DANDORI_PLANK = DataTracker.registerData(MixinPlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> DANDORI_MOSSY = DataTracker.registerData(MixinPlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> DANDORI_TUFF = DataTracker.registerData(MixinPlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> DANDORI_COPPER = DataTracker.registerData(MixinPlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> DANDORI_AGENT = DataTracker.registerData(MixinPlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> DANDORI_GRINDSTONE = DataTracker.registerData(MixinPlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> DANDORI_FIRST_STONE = DataTracker.registerData(MixinPlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> DANDORI_FIRST_OAK = DataTracker.registerData(MixinPlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> DANDORI_FIRST_BRICK = DataTracker.registerData(MixinPlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> DANDORI_FIRST_DIORITE = DataTracker.registerData(MixinPlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> DANDORI_CURRENT_TYPE = DataTracker.registerData(MixinPlayerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private DataDandoriCount dandoriCount = new DataDandoriCount();
    private boolean recountDandori = false;

    private static final DataDandoriCount.FOLLOWER_TYPE[] FOLLOWER_TYPES_VALUES = DataDandoriCount.FOLLOWER_TYPE.values();

    protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType, World world)
    {
        super(entityType, world);
    }

    @Inject(method = "", at = @At("tail"))
    protected void inject_initDataTracker(CallbackInfo ci)
    {
        if (!this.dataTracker.containsKey(DANDORI_TOTAL))
            this.dataTracker.startTracking(DANDORI_TOTAL, 0);
        if (!this.dataTracker.containsKey(DANDORI_PAWN_BLUE))
            this.dataTracker.startTracking(DANDORI_PAWN_BLUE, 0);
        if (!this.dataTracker.containsKey(DANDORI_PAWN_RED))
            this.dataTracker.startTracking(DANDORI_PAWN_RED, 0);
        if (!this.dataTracker.containsKey(DANDORI_PAWN_YELLOW))
            this.dataTracker.startTracking(DANDORI_PAWN_YELLOW, 0);
        if (!this.dataTracker.containsKey(DANDORI_IRON))
            this.dataTracker.startTracking(DANDORI_IRON, 0);
        if (!this.dataTracker.containsKey(DANDORI_SNOW))
            this.dataTracker.startTracking(DANDORI_SNOW, 0);
        if (!this.dataTracker.containsKey(DANDORI_COBBLE))
            this.dataTracker.startTracking(DANDORI_COBBLE, 0);
        if (!this.dataTracker.containsKey(DANDORI_PLANK))
            this.dataTracker.startTracking(DANDORI_PLANK, 0);
        if (!this.dataTracker.containsKey(DANDORI_MOSSY))
            this.dataTracker.startTracking(DANDORI_MOSSY, 0);
        if (!this.dataTracker.containsKey(DANDORI_GRINDSTONE))
            this.dataTracker.startTracking(DANDORI_GRINDSTONE, 0);
        if (!this.dataTracker.containsKey(DANDORI_TUFF))
            this.dataTracker.startTracking(DANDORI_TUFF, 0);
        if (!this.dataTracker.containsKey(DANDORI_COPPER))
            this.dataTracker.startTracking(DANDORI_COPPER, 0);
        if (!this.dataTracker.containsKey(DANDORI_AGENT))
            this.dataTracker.startTracking(DANDORI_AGENT, 0);
        if (!this.dataTracker.containsKey(DANDORI_FIRST_STONE))
            this.dataTracker.startTracking(DANDORI_FIRST_STONE, 0);
        if (!this.dataTracker.containsKey(DANDORI_FIRST_OAK))
            this.dataTracker.startTracking(DANDORI_FIRST_OAK, 0);
        if (!this.dataTracker.containsKey(DANDORI_FIRST_BRICK))
            this.dataTracker.startTracking(DANDORI_FIRST_BRICK, 0);
        if (!this.dataTracker.containsKey(DANDORI_FIRST_DIORITE))
            this.dataTracker.startTracking(DANDORI_FIRST_DIORITE, 0);
        if (!this.dataTracker.containsKey(DANDORI_CURRENT_TYPE))
            this.dataTracker.startTracking(DANDORI_CURRENT_TYPE, -1);
    }

    @Override
    public void pushAwayFrom(Entity entity)
    {
        // Don't push away from our followers.
        if (entity instanceof IEntityDandoriFollower && ((IEntityDandoriFollower) entity).getOwner() == this) return;
        super.pushAwayFrom(entity);
    }

    @Inject(method = "tick", at = @At("tail"))
    protected void inject_tick(CallbackInfo ci)
    {
        // Apply weakness effect to players under the bedrock.
        if (this.getY() < -64)
        {
            this.addStatusEffect(new StatusEffectInstance(weaknessEffect));
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
        this.setTrackedInt(DANDORI_PLANK, dandoriCount.getFollowerCount(DataDandoriCount.FOLLOWER_TYPE.PLANK));
        this.setTrackedInt(DANDORI_MOSSY, dandoriCount.getFollowerCount(DataDandoriCount.FOLLOWER_TYPE.MOSSY));
        this.setTrackedInt(DANDORI_GRINDSTONE, dandoriCount.getFollowerCount(DataDandoriCount.FOLLOWER_TYPE.GRINDSTONE));
        this.setTrackedInt(DANDORI_TUFF, dandoriCount.getFollowerCount(DataDandoriCount.FOLLOWER_TYPE.TUFF));
        this.setTrackedInt(DANDORI_COPPER, dandoriCount.getFollowerCount(DataDandoriCount.FOLLOWER_TYPE.COPPER));
        this.setTrackedInt(DANDORI_AGENT, dandoriCount.getFollowerCount(DataDandoriCount.FOLLOWER_TYPE.AGENT));
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
        return this.dataTracker.get(DANDORI_TOTAL);
    }
    public int getDandoriCountBlue()
    {
        return this.dataTracker.get(DANDORI_PAWN_BLUE);
    }
    public int getDandoriCountRed()
    {
        return this.dataTracker.get(DANDORI_PAWN_RED);
    }
    public int getDandoriCountYellow()
    {
        return this.dataTracker.get(DANDORI_PAWN_YELLOW);
    }
    public int getDandoriCountIron()
    {
        return this.dataTracker.get(DANDORI_IRON);
    }
    public int getDandoriCountSnow()
    {
        return this.dataTracker.get(DANDORI_SNOW);
    }
    public int getDandoriCountCobble()
    {
        return this.dataTracker.get(DANDORI_COBBLE);
    }
    public int getDandoriCountPlank()
    {
        return this.dataTracker.get(DANDORI_PLANK);
    }
    public int getDandoriCountMossy()
    {
        return this.dataTracker.get(DANDORI_MOSSY);
    }
    public int getDandoriCountGrindstone()
    {
        return this.dataTracker.get(DANDORI_GRINDSTONE);
    }
    public int getDandoriCountTuff()
    {
        return this.dataTracker.get(DANDORI_TUFF);
    }
    public int getDandoriCountCopper()
    {
        return this.dataTracker.get(DANDORI_COPPER);
    }
    public int getDandoriCountAgent()
    {
        return this.dataTracker.get(DANDORI_AGENT);
    }
    public int getDandoriCountFirstStone()
    {
        return this.dataTracker.get(DANDORI_FIRST_STONE);
    }
    public int getDandoriCountFirstOak()
    {
        return this.dataTracker.get(DANDORI_FIRST_OAK);
    }
    public int getDandoriCountFirstBrick()
    {
        return this.dataTracker.get(DANDORI_FIRST_BRICK);
    }
    public int getDandoriCountFirstDiorite()
    {
        return this.dataTracker.get(DANDORI_FIRST_DIORITE);
    }

    public void nextDandoriCurrentType()
    {
        int value = this.dataTracker.get(DANDORI_CURRENT_TYPE);
        value++;
        if (value >= FOLLOWER_TYPES_VALUES.length) value = -1;
        if (value >= 0) this.setTrackedInt(DANDORI_CURRENT_TYPE, dandoriCount.getNextCountWithFollowers(value));
        else this.setTrackedInt(DANDORI_CURRENT_TYPE, value);
    }
    public DataDandoriCount.FOLLOWER_TYPE getDandoriCurrentType()
    {
        int value = this.dataTracker.get(DANDORI_CURRENT_TYPE);
        if (value >= 0 && value < FOLLOWER_TYPES_VALUES.length)
            return FOLLOWER_TYPES_VALUES[value];
        return null;
    }

    private void setTrackedInt(TrackedData<Integer> key, int count)
    {
        this.dataTracker.set(key, count);
    }
}
