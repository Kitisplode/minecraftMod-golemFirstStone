package com.kitisplode.golemfirststonemod.entity.client.renderer.other;

import com.kitisplode.golemfirststonemod.entity.client.model.other.EntityModelGolemAgent;
import com.kitisplode.golemfirststonemod.entity.client.utils.AutoGlowingGeoLayerFixed;
import com.kitisplode.golemfirststonemod.entity.entity.golem.other.EntityGolemAgent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererGolemAgent extends GeoEntityRenderer<EntityGolemAgent>
{
    private final AutoGlowingGeoLayerFixed<EntityGolemAgent> glowLayer = new AutoGlowingGeoLayerFixed<>(this, EntityGolemAgent.GLOWMASK);

    public EntityRendererGolemAgent(EntityRendererProvider.Context renderManager)
    {
        super(renderManager, new EntityModelGolemAgent());
        this.shadowRadius = 0.4f;
    }

    @Override
    public void render(EntityGolemAgent entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight)
    {
        this.renderLayers.getRenderLayers().clear();
        if (entity.getActive())
        {
            this.addRenderLayer(glowLayer);
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

}
