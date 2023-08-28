package com.kitisplode.golemfirststonemod.entity.entity.golem;

import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDandoriCount;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

abstract public class AbstractGolemDandoriFollower extends IronGolem implements IEntityDandoriFollower
{

    private static final EntityDataAccessor<Boolean> DANDORI_STATE = SynchedEntityData.defineId(AbstractGolemDandoriFollower.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(AbstractGolemDandoriFollower.class, EntityDataSerializers.OPTIONAL_UUID);
    protected static final double dandoriMoveRange = 6;
    protected static final double dandoriSeeRange = 36;

    public AbstractGolemDandoriFollower(EntityType<? extends IronGolem> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes()
    {
        return IronGolem.createAttributes();
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        if (!this.entityData.hasItem(DANDORI_STATE)) this.entityData.define(DANDORI_STATE, false);
        if (!this.entityData.hasItem(OWNER_UUID)) this.entityData.define(OWNER_UUID, Optional.empty());
    }
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        if (this.getOwnerUUID() != null) {
            pCompound.putUUID("Owner", this.getOwnerUUID());
        }
    }
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        UUID uuid;
        if (pCompound.hasUUID("Owner")) {
            uuid = pCompound.getUUID("Owner");
        } else {
            String s = pCompound.getString("Owner");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }
        if (uuid != null) {
            try {
                this.setOwnerUUID(uuid);
            } catch (Throwable throwable) {}
        }
    }
    @Nullable
    public UUID getOwnerUUID() {
        return this.entityData.get(OWNER_UUID).orElse((UUID)null);
    }

    public void setOwnerUUID(@Nullable UUID pUuid) {
        this.entityData.set(OWNER_UUID, Optional.ofNullable(pUuid));
    }
    public LivingEntity getOwner()
    {
        UUID uUID = this.getOwnerUUID();
        if (uUID == null) return null;
        return this.level().getPlayerByUUID(uUID);
    }
    public void setOwner(LivingEntity pOwner)
    {
        if (pOwner != null) setOwnerUUID(pOwner.getUUID());
    }
    public boolean isOwner(LivingEntity pEntity) {
        return pEntity == this.getOwner();
    }

    public boolean getDandoriState()
    {
        return this.entityData.get(DANDORI_STATE);
    }
    public void setDandoriState(boolean pDandoriState)
    {
        if (this.getOwner() != null) ((IEntityWithDandoriCount) this.getOwner()).setRecountDandori();
        this.entityData.set(DANDORI_STATE, pDandoriState);
    }

    public void handleEntityEvent(byte pId)
    {
        if (pId == IEntityDandoriFollower.ENTITY_EVENT_DANDORI_START) addDandoriParticles();
        else super.handleEntityEvent(pId);
    }
    protected void addDandoriParticles()
    {
        level().addParticle(ParticleTypes.NOTE,
                getX(), getY() + getBbHeight() * 1.5, getZ(),
                0,1,0);
    }

    @Override
    public void remove(Entity.RemovalReason pReason)
    {
        if (this.getDandoriState() && this.getOwner() != null)
        {
            ((IEntityWithDandoriCount) this.getOwner()).setRecountDandori();
        }
        super.remove(pReason);
    }
}
