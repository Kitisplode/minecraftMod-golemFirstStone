package com.kitisplode.golemfirststonemod.entity.client.utils;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class AutoGlowingGeoLayerFixed<T extends GeoAnimatable> extends AutoGlowingGeoLayer<T>
{
    private ResourceLocation glowTexture;

    public AutoGlowingGeoLayerFixed(GeoRenderer renderer)
    {
        super(renderer);
    }

    public AutoGlowingGeoLayerFixed(GeoRenderer renderer, ResourceLocation glowTexture)
    {
        this(renderer);
        this.glowTexture = glowTexture;
    }

    protected RenderType getRenderType(T animatable) {
        if (glowTexture == null) return RenderType.eyes(getTextureResource(animatable));
        return RenderType.eyes(glowTexture);
    }
}
