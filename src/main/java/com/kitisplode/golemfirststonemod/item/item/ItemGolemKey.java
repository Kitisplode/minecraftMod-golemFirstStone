package com.kitisplode.golemfirststonemod.item.item;

import com.kitisplode.golemfirststonemod.entity.ModEntities;
import com.kitisplode.golemfirststonemod.entity.entity.golem.dungeons.EntityGolemKey;
import com.kitisplode.golemfirststonemod.item.client.ItemRendererGolemKey;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.extensions.IForgeItem;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class ItemGolemKey extends Item implements GeoItem, IForgeItem
{
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ItemGolemKey(Properties pProperties)
    {
        super(pProperties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public boolean canFitInsideContainerItems()
    {
        return false;
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack)
    {
        return true;
    }

    @Override
    public Entity createEntity(Level level, Entity location, ItemStack stack)
    {
        EntityGolemKey newGolem = ModEntities.ENTITY_GOLEM_KEY.get().create(level);
        if (newGolem == null) return null;
        newGolem.setPos(location.position());
        newGolem.setDeltaMovement(location.getDeltaMovement());
        if (stack.hasCustomHoverName()) newGolem.setCustomName(stack.getHoverName());
        if (stack.hasTag())
        {
            CompoundTag tag = stack.getTag();
            if (tag != null)
            {
                if (tag.contains("DeployPos")) newGolem.setDeployPosition(NbtUtils.readBlockPos(tag.getCompound("DeployPos")));
                if (tag.contains("Owner")) newGolem.setOwnerUUID(tag.getUUID("Owner"));
            }
        }
        return newGolem;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer)
    {
        consumer.accept(new IClientItemExtensions() {
            private ItemRendererGolemKey renderer = null;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null)
                    this.renderer = new ItemRendererGolemKey();

                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<GeoAnimatable>(this, "controller", 0, event ->
        {
            event.getController().setAnimationSpeed(2.00);
            return event.setAndContinue(RawAnimation.begin().then("animation.golem_key.carried", Animation.LoopType.LOOP));
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }
}
