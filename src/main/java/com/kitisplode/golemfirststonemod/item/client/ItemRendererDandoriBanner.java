package com.kitisplode.golemfirststonemod.item.client;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.item.item.ItemDandoriBanner;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class ItemRendererDandoriBanner extends GeoItemRenderer<ItemDandoriBanner>
{
    public ItemRendererDandoriBanner() {
        super(new DefaultedItemGeoModel<>(new ResourceLocation(GolemFirstStoneMod.MOD_ID, "banner_courage")));
    }
}
