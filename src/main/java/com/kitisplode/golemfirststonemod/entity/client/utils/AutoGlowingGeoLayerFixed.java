package com.kitisplode.golemfirststonemod.entity.client.utils;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class AutoGlowingGeoLayerFixed<T extends GeoAnimatable> extends AutoGlowingGeoLayer<T>
{
    private Identifier glowTexture;

    public AutoGlowingGeoLayerFixed(GeoRenderer renderer)
    {
        super(renderer);
    }

    public AutoGlowingGeoLayerFixed(GeoRenderer renderer, Identifier glowTexture)
    {
        this(renderer);
        this.glowTexture = glowTexture;
    }

    protected RenderLayer getRenderType(T animatable) {
        if (glowTexture == null) return RenderLayer.getEyes(getTextureResource(animatable));
        return RenderLayer.getEyes(glowTexture);
    }
}
