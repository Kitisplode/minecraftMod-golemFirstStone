package com.kitisplode.golemfirststonemod.mixin.entity;

import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDandoriCount;
import com.kitisplode.golemfirststonemod.entity.goal.goal.DandoriFollowGoal;
import com.kitisplode.golemfirststonemod.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.ServerConfigHandler;
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
    private static final TrackedData<Boolean> DANDORI_STATE = DataTracker.registerData(MixinIronGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(MixinIronGolemEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private static final double dandoriMoveRange = 3;
    private static final double dandoriSeeRange = 36;

    protected MixinIronGolemEntity(EntityType<? extends GolemEntity> entityType, World world)
    {
        super(entityType, world);
    }

    @Inject(method = ("initDataTracker"), at = @At("HEAD"))
    protected void initDataTracker(CallbackInfo ci)
    {
        if (!this.dataTracker.containsKey(DANDORI_STATE))
            this.dataTracker.startTracking(DANDORI_STATE, false);
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
    public boolean isOwner(LivingEntity entity)
    {
        return entity.getUuid() == this.getOwnerUuid();
    }

    @Nullable
    private UUID getOwnerUuid() {
        return this.dataTracker.get(OWNER_UUID).orElse(null);
    }

    private void setOwnerUuid(@Nullable UUID uuid) {
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

    @Inject(method = ("initGoals"), at = @At("HEAD"))
    protected void initGoals(CallbackInfo ci)
    {
        this.goalSelector.add(0, new DandoriFollowGoal(this, 1.0, Ingredient.ofItems(ModItems.ITEM_DANDORI_CALL, ModItems.ITEM_DANDORI_ATTACK), dandoriMoveRange, dandoriSeeRange));
    }

    @ModifyVariable(method = ("handleStatus"), at = @At("HEAD"), ordinal = 0)
    protected byte handleStatus_dandori(byte status)
    {
        if (status == IEntityDandoriFollower.ENTITY_EVENT_DANDORI_START)
            addDandoriParticles();
        return status;
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
