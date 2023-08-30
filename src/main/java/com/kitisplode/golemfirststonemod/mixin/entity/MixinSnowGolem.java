package com.kitisplode.golemfirststonemod.mixin.entity;

import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDandoriCount;
import com.kitisplode.golemfirststonemod.entity.goal.goal.DandoriFollowGoal;
import com.kitisplode.golemfirststonemod.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.RangedAttackMob;
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

@Mixin(value = SnowGolem.class)
public abstract class MixinSnowGolem extends AbstractGolem implements Shearable, RangedAttackMob, net.minecraftforge.common.IForgeShearable, IEntityDandoriFollower
{
    private static final EntityDataAccessor<Boolean> DANDORI_STATE = SynchedEntityData.defineId(MixinSnowGolem.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(MixinSnowGolem.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final double dandoriMoveRange = 3;
    private static final double dandoriSeeRange = 36;

    protected MixinSnowGolem(EntityType<? extends AbstractGolem> pEntityType, Level pLevel)
    {
        super(pEntityType, pLevel);
    }

    @Inject(method = ("defineSynchedData"), at = @At("TAIL"))
    protected void inject_defineSynchedData(CallbackInfo ci)
    {
        if (!this.entityData.hasItem(DANDORI_STATE)) this.entityData.define(DANDORI_STATE, false);
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
        UUID uuid;
        if (pCompound.hasUUID("Owner")) {
            uuid = pCompound.getUUID("Owner");
        } else {
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
    public boolean isOwner(LivingEntity pEntity) {
        return pEntity == this.getOwner();
    }

    public boolean getDandoriState()
    {
        return this.entityData.get(DANDORI_STATE);
    }
    public void setDandoriState(boolean pDandoriState)
    {
        if (this.getOwner() != null && this.getDandoriState()) ((IEntityWithDandoriCount) this.getOwner()).setRecountDandori();
        this.entityData.set(DANDORI_STATE, pDandoriState);
    }


    @Inject(method = ("registerGoals"), at = @At("HEAD"))
    protected void registerGoals(CallbackInfo ci)
    {
        this.goalSelector.addGoal(0, new DandoriFollowGoal(this, 1.4, Ingredient.of(ModItems.ITEM_DANDORI_CALL.get(), ModItems.ITEM_DANDORI_ATTACK.get()), dandoriMoveRange, dandoriSeeRange));
    }

    @Override
    public void handleEntityEvent(byte status)
    {
        if (status == IEntityDandoriFollower.ENTITY_EVENT_DANDORI_START)
            addDandoriParticles();
        else super.handleEntityEvent(status);
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
