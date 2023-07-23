package com.kitisplode.fabricplayground.entity.client;

import com.kitisplode.fabricplayground.FabricPlaygroundMod;
import com.kitisplode.fabricplayground.entity.custom.EntityGolemClay;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererGolemClay extends GeoEntityRenderer<EntityGolemClay>
{
    public EntityRendererGolemClay(EntityRendererFactory.Context renderManager)
    {
        super(renderManager, new EntityModelGolemClay());
    }

    @Override
    public Identifier getTextureLocation(EntityGolemClay animatable)
    {
        return new Identifier(FabricPlaygroundMod.MOD_ID, "textures/entity/example.png");
    }

    @Override
    public void render(EntityGolemClay entity, float entityYaw, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight)
    {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
