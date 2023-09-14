package com.kitisplode.golemfirststonemod.mixin.entity;

import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDandoriCount;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriFollowHardGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriFollowSoftGoal;
import com.kitisplode.golemfirststonemod.entity.goal.action.DandoriMoveToDeployPositionGoal;
import com.kitisplode.golemfirststonemod.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Mixin(value = IronGolem.class)
public abstract class MixinIronGolem extends AbstractGolem implements NeutralMob, IEntityDandoriFollower
{
    private static final EntityDataAccessor<Integer> DANDORI_STATE = SynchedEntityData.defineId(MixinIronGolem.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(MixinIronGolem.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final double dandoriMoveRange = 3;
    private static final double dandoriSeeRange = 12;
    private BlockPos deployPosition;

    protected MixinIronGolem(EntityType<? extends AbstractGolem> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
    }

    @Inject(method = ("defineSynchedData"), at = @At("TAIL"))
    protected void inject_defineSynchedData(CallbackInfo ci)
    {
        if (!this.entityData.hasItem(DANDORI_STATE)) this.entityData.define(DANDORI_STATE, 0);
        if (!this.entityData.hasItem(DATA_OWNERUUID_ID)) this.entityData.define(DATA_OWNERUUID_ID, Optional.empty());
    }
    @ModifyVariable(method = ("addAdditionalSaveData"), at = @At("TAIL"), ordinal = 0)
    protected CompoundTag addAdditionalSaveData_owner(CompoundTag pCompound) {
        if (this.getOwnerUUID() != null) {
            pCompound.putUUID("Owner", this.getOwnerUUID());
        }
        return pCompound;
    }
    @ModifyVariable(method = ("readAdditionalSaveData"), at = @At("TAIL"), ordinal = 0)
    public CompoundTag readAdditionalSaveData_owner(CompoundTag pCompound) {
        UUID uuid = null;
        if (pCompound.hasUUID("Owner")) {
            uuid = pCompound.getUUID("Owner");
        } else if (pCompound.contains("Owner")){
            String s = pCompound.getString("Owner");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(Objects.requireNonNull(this.getServer()), s);
        }
        if (uuid != null) {
            try {
                this.setOwnerUUID(uuid);
            } catch (Throwable throwable) {}
        }
        return pCompound;
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.entityData.get(DATA_OWNERUUID_ID).orElse((UUID)null);
    }

    public void setOwnerUUID(@Nullable UUID pUuid) {
        this.entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable(pUuid));
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
        if (pDandoriState > 0)
        {
            this.setDeployPosition(null);
        }
        this.entityData.set(DANDORI_STATE, pDandoriState);
    }


    @Inject(method = ("registerGoals"), at = @At("HEAD"))
    protected void registerGoals(CallbackInfo ci)
    {
        this.goalSelector.addGoal(-1, new DandoriFollowHardGoal(this, 1.4, dandoriMoveRange, dandoriSeeRange));
        this.goalSelector.addGoal(0, new DandoriFollowSoftGoal(this, 1.4, dandoriMoveRange, dandoriSeeRange));
        this.goalSelector.addGoal(2, new DandoriMoveToDeployPositionGoal(this, 2.0f, 1.0f));
        this.goalSelector.addGoal(2, new DandoriFollowSoftGoal(this, 1.4, dandoriMoveRange, 0));
    }

    @ModifyVariable(method = ("handleEntityEvent"), at = @At("HEAD"), ordinal = 0)
    protected byte handleEntityEvent_dandori(byte status)
    {
        if (status == IEntityDandoriFollower.ENTITY_EVENT_DANDORI_START)
            addDandoriParticles();
        return status;
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

    @Override
    public int getRemainingPersistentAngerTime()
    {
        return 0;
    }

    @Override
    public void setRemainingPersistentAngerTime(int pRemainingPersistentAngerTime)
    {
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget()
    {
        return null;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID pPersistentAngerTarget)
    {

    }

    @Override
    public void startPersistentAngerTimer()
    {

    }
}
