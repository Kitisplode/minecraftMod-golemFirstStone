package com.kitisplode.golemfirststonemod.item.item;

import com.kitisplode.golemfirststonemod.item.client.ItemRendererDandoriBanner;
import com.kitisplode.golemfirststonemod.sound.ModSounds;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.UseAction;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.RenderProvider;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ItemDandoriBanner extends ItemDandoriCall implements GeoItem
{
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    public ItemDandoriBanner(Settings settings)
    {
        super(settings);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    protected void playWhistleSound(PlayerEntity pPlayer)
    {
        pPlayer.playSound(ModSounds.ITEM_DANDORI_BANNER_01, 0.8f, 1.0f);
    }
    protected void playWhistleSoundForced(LivingEntity pLivingEntity)
    {
        pLivingEntity.playSound(ModSounds.ITEM_DANDORI_BANNER_02, 0.8f, 1.0f);
    }

    @Override
    public void createRenderer(Consumer<Object> consumer)
    {
        consumer.accept(new RenderProvider() {
            private ItemRendererDandoriBanner renderer = null;

            @Override
            public BuiltinModelItemRenderer getCustomRenderer() {
                if (this.renderer == null)
                    this.renderer = new ItemRendererDandoriBanner();

                return this.renderer;
            }
        });
    }

    @Override
    public Supplier<Object> getRenderProvider()
    {
        return this.renderProvider;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar)
    {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, event ->
        {
            return event.setAndContinue(RawAnimation.begin().then("animation.banner_courage.idle", Animation.LoopType.LOOP));
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache()
    {
        return cache;
    }
}
