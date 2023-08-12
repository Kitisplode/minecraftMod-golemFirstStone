package com.kitisplode.golemfirststonemod.mixin;

import com.kitisplode.golemfirststonemod.entity.entity.IEntityDandoriFollower;
import com.kitisplode.golemfirststonemod.entity.goal.goal.DandoriFollowGoal;
import com.kitisplode.golemfirststonemod.item.ModItems;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Shearable;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = SnowGolemEntity.class)
public abstract class MixinSnowGolemEntity
        extends GolemEntity
        implements Shearable,
        RangedAttackMob,
        IEntityDandoriFollower
{
    private static final TrackedData<Boolean> DANDORI_STATE = DataTracker.registerData(MixinSnowGolemEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final double dandoriMoveRange = 3;
    private static final double dandoriSeeRange = 20;

    protected MixinSnowGolemEntity(EntityType<? extends SnowGolemEntity> entityType, World world)
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
        this.goalSelector.add(0, new DandoriFollowGoal(this, 1.25, Ingredient.ofItems(ModItems.ITEM_DANDORI_CALL), dandoriMoveRange, dandoriSeeRange));
    }

    @Override
    public void handleStatus(byte status)
    {
        if (status == EntityStatuses.ADD_POSITIVE_PLAYER_REACTION_PARTICLES)
            addDandoriParticles();
        else super.handleStatus(status);
    }

    private void addDandoriParticles()
    {
        this.getWorld().addParticle(ParticleTypes.NOTE,
                this.getX(), this.getEyeY() + 1, this.getZ(),
                0,1,0);
    }
}
