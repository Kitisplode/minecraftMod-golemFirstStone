package com.kitisplode.golemfirststonemod.mixin;

import com.kitisplode.golemfirststonemod.entity.entity.golem.vote.EntityGolemCopper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Supplier;

@Mixin(value = ServerLevel.class)
public abstract class MixinServerLevel extends Level implements WorldGenLevel
{
    protected MixinServerLevel(WritableLevelData pLevelData, ResourceKey<Level> pDimension, RegistryAccess pRegistryAccess, Holder<DimensionType> pDimensionTypeRegistration, Supplier<ProfilerFiller> pProfiler, boolean pIsClientSide, boolean pIsDebug, long pBiomeZoomSeed, int pMaxChainedNeighborUpdates)
    {
        super(pLevelData, pDimension, pRegistryAccess, pDimensionTypeRegistration, pProfiler, pIsClientSide, pIsDebug, pBiomeZoomSeed, pMaxChainedNeighborUpdates);
    }

    @Inject(method = ("findLightningRod"), at = @At("return"), cancellable = true)
    private void inject_redirectLightningToCopperGolem(BlockPos bp, CallbackInfoReturnable<Optional<BlockPos>> cir)
    {
        Vec3 pos = bp.getCenter();
        TargetingConditions tp = TargetingConditions.forNonCombat().range(128).selector(entity -> entity instanceof EntityGolemCopper);
        LivingEntity golemCopper = this.getNearestEntity(LivingEntity.class, tp, null, pos.x(), pos.y(), pos.z(), AABB.ofSize(pos, 128,128,128));
        if (golemCopper != null) cir.setReturnValue(Optional.of(golemCopper.getOnPos()));
    }
}
