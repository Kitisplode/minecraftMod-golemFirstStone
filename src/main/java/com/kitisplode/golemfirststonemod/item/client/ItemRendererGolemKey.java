package com.kitisplode.golemfirststonemod.item.client;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.item.item.ItemGolemKey;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class ItemRendererGolemKey extends GeoItemRenderer<ItemGolemKey>
{
    public ItemRendererGolemKey() {
        super(new DefaultedItemGeoModel<>(new ResourceLocation(GolemFirstStoneMod.MOD_ID, "golem_key")));
    }
}
