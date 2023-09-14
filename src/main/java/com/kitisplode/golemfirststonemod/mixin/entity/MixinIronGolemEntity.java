package com.kitisplode.golemfirststonemod.mixin.entity;

import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDandoriCount;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriFollowHardGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriFollowSoftGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriMoveToDeployPositionGoal;
import com.kitisplode.golemfirststonemod.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.UUID;

@Mixin(value = IronGolemEntity.class)
public abstract class MixinIronGolemEntity extends GolemEntity implements Angerable, IEntityDandoriFollower
{
    private static final TrackedData<Integer> DANDORI_STATE = DataTracker.registerData(MixinIronGolemEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(MixinIronGolemEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private static final double dandoriMoveRange = 4;
    private static final double dandoriSeeRange = 12;
    private BlockPos deployPosition;

    protected MixinIronGolemEntity(EntityType<? extends GolemEntity> entityType, World world)
    {
        super(entityType, world);
    }

    @Inject(method = ("initDataTracker"), at = @At("HEAD"))
    protected void initDataTracker(CallbackInfo ci)
    {
        if (!this.dataTracker.containsKey(DANDORI_STATE))
            this.dataTracker.startTracking(DANDORI_STATE, 0);
        if (!this.dataTracker.containsKey(OWNER_UUID))
            this.dataTracker.startTracking(OWNER_UUID, Optional.empty());
    }

    @ModifyVariable(method = ("writeCustomDataToNbt"), at = @At("TAIL"), ordinal = 0)
    protected NbtCompound writeNBT_owner(NbtCompound nbt)
    {
        if (this.getOwnerUuid() != null) {
            nbt.putUuid("Owner", this.getOwnerUuid());
        }
        return nbt;
    }

    @ModifyVariable(method = ("readCustomDataFromNbt"), at = @At("TAIL"), ordinal = 0)
    protected NbtCompound readNBT_owner(NbtCompound nbt)
    {
        UUID uUID = null;
        if (nbt.containsUuid("Owner")) {
            uUID = nbt.getUuid("Owner");
        } else if (nbt.contains("Owner")) {
            String string = nbt.getString("Owner");
            uUID = ServerConfigHandler.getPlayerUuidByName(this.getServer(), string);
        }
        if (uUID != null) {
            try {
                this.setOwnerUuid(uUID);
            } catch (Throwable throwable) {
            }
        }
        return nbt;
    }

    @Override
    public LivingEntity getOwner()
    {
        UUID uUID = this.getOwnerUuid();
        if (uUID == null)
            return null;
        return this.getWorld().getPlayerByUuid(uUID);
    }

    @Override
    public void setOwner(LivingEntity newOwner)
    {
        if (newOwner != null)
        {
            setOwnerUuid(newOwner.getUuid());
        }
    }

    @Override
    public boolean isImmobile()
    {
        return super.isImmobile();
    }

    @Nullable
    private UUID getOwnerUuid() {
        return this.dataTracker.get(OWNER_UUID).orElse(null);
    }

    private void setOwnerUuid(@Nullable UUID uuid) {
        this.dataTracker.set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    public int getDandoriState()
    {
        return this.dataTracker.get(DANDORI_STATE);
    }

    public void setDandoriState(int pDandoriState)
    {
        if (this.getOwner() != null && this.getOwner() instanceof IEntityWithDandoriCount) ((IEntityWithDandoriCount) this.getOwner()).setRecountDandori();
        if (pDandoriState > 0)
        {
            this.setDeployPosition(null);
        }
        this.dataTracker.set(DANDORI_STATE, pDandoriState);
    }

    @Inject(method = ("initGoals"), at = @At("HEAD"))
    protected void initGoals_head(CallbackInfo ci)
    {
        this.goalSelector.add(-1, new DandoriFollowHardGoal(this, 1.4, dandoriMoveRange, dandoriSeeRange));
        this.goalSelector.add(0, new DandoriFollowSoftGoal(this, 1.4, dandoriMoveRange, dandoriSeeRange));
        this.goalSelector.add(2, new DandoriMoveToDeployPositionGoal(this, 2.0f, 1.0f));
        this.goalSelector.add(2, new DandoriFollowSoftGoal(this, 1.4, dandoriMoveRange, 0));
    }

    @ModifyVariable(method = ("handleStatus"), at = @At("HEAD"), ordinal = 0)
    protected byte handleStatus_dandori(byte status)
    {
        if (status == IEntityDandoriFollower.ENTITY_EVENT_DANDORI_START)
            addDandoriParticles();
        return status;
    }

    private void addDandoriParticles()
    {
        getWorld().addParticle(ParticleTypes.NOTE,
                getX(), getY() + getHeight() * 1.5, getZ(),
                0,1,0);
    }

    @Override
    public void remove(RemovalReason reason)
    {
        if (this.getOwner() != null && this.getOwner() instanceof IEntityWithDandoriCount dandoriOwner) (dandoriOwner).setRecountDandori();
        super.remove(reason);
    }

    @Override
    public void setDeployPosition(BlockPos bp)
    {
        this.deployPosition = bp;
    }
    @Override
    public BlockPos getDeployPosition()
    {
        return this.deployPosition;
    }
    @Override
    public double getTargetRange()
    {
        if (this.isDandoriOn()) return 6.0d;
        return this.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
    }
}
