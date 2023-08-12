package com.kitisplode.golemfirststonemod.mixin;

import com.kitisplode.golemfirststonemod.entity.entity.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.goal.goal.DandoriFollowGoal;
import com.kitisplode.golemfirststonemod.item.ModItems;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = IronGolemEntity.class)
public abstract class MixinIronGolemEntity extends GolemEntity implements Angerable, IEntityDandoriFollower
{
    private static final TrackedData<Boolean> DANDORI_STATE = DataTracker.registerData(MixinIronGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final double dandoriMoveRange = 3;
    private static final double dandoriSeeRange = 20;

    protected MixinIronGolemEntity(EntityType<? extends GolemEntity> entityType, World world)
    {
        super(entityType, world);
    }

    @Inject(method = ("initDataTracker"), at = @At("HEAD"))
    protected void initDataTracker(CallbackInfo ci)
    {
        if (!this.dataTracker.containsKey(DANDORI_STATE))
            this.dataTracker.startTracking(DANDORI_STATE, false);
    }

    public boolean getDandoriState()
    {
        return this.dataTracker.get(DANDORI_STATE);
    }

    public void setDandoriState(boolean pDandoriState)
    {
        this.dataTracker.set(DANDORI_STATE, pDandoriState);
    }

    @Inject(method = ("initGoals"), at = @At("HEAD"))
    protected void initGoals(CallbackInfo ci)
    {
        this.goalSelector.add(0, new DandoriFollowGoal(this, 1.0, Ingredient.ofItems(ModItems.ITEM_DANDORI_CALL), dandoriMoveRange, dandoriSeeRange));
    }

    @ModifyVariable(method = ("handleStatus"), at = @At("HEAD"), ordinal = 0)
    protected byte handleStatus_dandori(byte status)
    {
        if (status == EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES)
            addDandoriParticles();
        return status;
    }

    private void addDandoriParticles()
    {
        this.getWorld().addParticle(ParticleTypes.NOTE,
                this.getX(), this.getEyeY() + 1, this.getZ(),
                0,1,0);
    }
}
