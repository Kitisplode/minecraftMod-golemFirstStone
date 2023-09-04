package com.kitisplode.golemfirststonemod.mixin;

import com.kitisplode.golemfirststonemod.entity.entity.golem.vote.EntityGolemCopper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;
import java.util.function.Supplier;

@Mixin(value = ServerWorld.class)
public abstract class MixinServerWorld
    extends World
    implements StructureWorldAccess
{
    protected MixinServerWorld(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates)
    {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    @Inject(method = ("getLightningRodPos"), at = @At("return"), cancellable = true)
    private void inject_redirectLightningToCopperGolem(BlockPos bp, CallbackInfoReturnable<Optional<BlockPos>> cir)
    {
        Vec3d pos = bp.toCenterPos();
        TargetPredicate tp = TargetPredicate.createNonAttackable().setBaseMaxDistance(128).setPredicate(entity -> entity instanceof EntityGolemCopper);
        LivingEntity golemCopper = this.getClosestEntity(LivingEntity.class, tp, null, pos.getX(), pos.getY(), pos.getZ(), Box.of(pos, 128,128,128));
        if (golemCopper != null) cir.setReturnValue(Optional.of(golemCopper.getBlockPos()));
    }

}
