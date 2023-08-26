package com.kitisplode.golemfirststonemod.entity.entity.golem;

import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDandoriCount;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

abstract public class AbstractGolemDandoriFollower extends IronGolemEntity implements IEntityDandoriFollower
{
    protected static final TrackedData<Integer> ATTACK_STATE = DataTracker.registerData(EntityGolemCobble.class, TrackedDataHandlerRegistry.INTEGER);
    protected static final TrackedData<Boolean> DANDORI_STATE = DataTracker.registerData(EntityGolemCobble.class, TrackedDataHandlerRegistry.BOOLEAN);
    protected static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(EntityGolemCobble.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    protected static final double dandoriMoveRange = 6;
    protected static final double dandoriSeeRange = 36;

    public AbstractGolemDandoriFollower(EntityType<? extends IronGolemEntity> entityType, World world)
    {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker()
    {
        super.initDataTracker();
        if (!this.dataTracker.containsKey(ATTACK_STATE))
            this.dataTracker.startTracking(ATTACK_STATE, 0);
        if (!this.dataTracker.containsKey(DANDORI_STATE))
            this.dataTracker.startTracking(DANDORI_STATE, false);
        if (!this.dataTracker.containsKey(OWNER_UUID))
            this.dataTracker.startTracking(OWNER_UUID, Optional.empty());
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt)
    {
        super.writeCustomDataToNbt(nbt);
        if (this.getOwnerUuid() != null) {
            nbt.putUuid("Owner", this.getOwnerUuid());
        }
    }
    @Override
    public void readCustomDataFromNbt(NbtCompound nbt)
    {
        super.readCustomDataFromNbt(nbt);
        UUID uUID;
        if (nbt.containsUuid("Owner")) {
            uUID = nbt.getUuid("Owner");
        } else {
            String string = nbt.getString("Owner");
            uUID = ServerConfigHandler.getPlayerUuidByName(this.getServer(), string);
        }
        if (uUID != null) {
            try {
                this.setOwnerUuid(uUID);
            } catch (Throwable throwable) {
            }
        }
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
    public boolean isOwner(LivingEntity entity)
    {
        return entity.getUuid() == this.getOwnerUuid();
    }
    @Nullable
    protected UUID getOwnerUuid() {
        return this.dataTracker.get(OWNER_UUID).orElse(null);
    }
    protected void setOwnerUuid(@Nullable UUID uuid) {
        this.dataTracker.set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    public boolean getDandoriState()
    {
        return this.dataTracker.get(DANDORI_STATE);
    }
    public void setDandoriState(boolean pDandoriState)
    {
        if (!pDandoriState)
        {
            if (this.getOwner() != null && this.getDandoriState()) ((IEntityWithDandoriCount) this.getOwner()).setRecountDandori();
        }
        this.dataTracker.set(DANDORI_STATE, pDandoriState);
    }

    @Override
    public void handleStatus(byte status)
    {
        switch(status)
        {
            case IEntityDandoriFollower.ENTITY_EVENT_DANDORI_START:
                addDandoriParticles();
                break;
            default:
                super.handleStatus(status);
                break;
        }
    }

    protected void addDandoriParticles()
    {
        getWorld().addParticle(ParticleTypes.NOTE,
                getX(), getY() + getHeight() / 2.0f, getZ(),
                0,1,0);
    }

    @Override
    public void remove(RemovalReason reason)
    {
        if (this.getDandoriState() && this.getOwner() != null)
        {
            ((IEntityWithDandoriCount) this.getOwner()).setRecountDandori();
        }
        super.remove(reason);
    }
}
