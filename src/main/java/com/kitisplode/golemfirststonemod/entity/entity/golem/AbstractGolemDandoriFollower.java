package com.kitisplode.golemfirststonemod.entity.entity.golem;

import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDandoriCount;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

abstract public class AbstractGolemDandoriFollower extends IronGolem implements IEntityDandoriFollower
{

    private static final EntityDataAccessor<Integer> DANDORI_STATE = SynchedEntityData.defineId(AbstractGolemDandoriFollower.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(AbstractGolemDandoriFollower.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Boolean> THROWN = SynchedEntityData.defineId(AbstractGolemDandoriFollower.class, EntityDataSerializers.BOOLEAN);
    protected static final double dandoriMoveRange = 6;
    protected static final double dandoriSeeRange = 16;
    private boolean lastOnGround = false;
    private float throwAngle = 0.0f;
    private BlockPos deployPosition;

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
        if (!this.entityData.hasItem(DANDORI_STATE)) this.entityData.define(DANDORI_STATE, 0);
        if (!this.entityData.hasItem(OWNER_UUID)) this.entityData.define(OWNER_UUID, Optional.empty());
        if (!this.entityData.hasItem(THROWN)) this.entityData.define(THROWN, false);
    }
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        if (this.getOwnerUUID() != null) {
            pCompound.putUUID("Owner", this.getOwnerUUID());
        }
    }
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        UUID uuid = null;
        if (pCompound.hasUUID("Owner")) {
            uuid = pCompound.getUUID("Owner");
        } else if (pCompound.contains("Owner")) {
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

    public int getDandoriState()
    {
        return this.entityData.get(DANDORI_STATE);
    }
    public void setDandoriState(int pDandoriState)
    {
        if (this.getOwner() != null && this.getOwner() instanceof IEntityWithDandoriCount) ((IEntityWithDandoriCount) this.getOwner()).setRecountDandori();
        if (pDandoriState == 0)
        {
            this.setDeployPosition(this.getOnPos());
        }
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
        if (this.getOwner() != null && this.getOwner() instanceof IEntityWithDandoriCount) ((IEntityWithDandoriCount) this.getOwner()).setRecountDandori();
        super.remove(pReason);
    }

    @Override
    public boolean isImmobile()
    {
        return super.isImmobile();
    }

    public boolean getThrown()
    {
        return this.entityData.get(THROWN);
    }
    public void setThrown(boolean pThrown)
    {
        this.entityData.set(THROWN, pThrown);
    }
    public float getThrowAngle()
    {
        return throwAngle;
    }
    @Override
    public boolean hurt(@NotNull DamageSource source, float amount)
    {
        if (!isThrowable()) return super.hurt(source, amount);
        if (source.is(DamageTypeTags.IS_FALL)) return false;
        return super.hurt(source, amount);
    }
    @Override
    public void tick()
    {
        super.tick();
        if (this.isThrowable())
        {
            if (this.onGround() && !lastOnGround)
            {
                if (this.getThrown())
                {
                    this.setThrown(false);
                    this.setDeployPosition(this.getOnPos());
                }
            }
            lastOnGround = this.onGround();
            if (this.getThrown())
            {
                throwAngle -= 30.0f;
            } else
            {
                throwAngle = 0.0f;
            }
        }
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
        return this.getAttributeValue(Attributes.FOLLOW_RANGE);
    }
}
