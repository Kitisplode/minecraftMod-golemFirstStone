package com.kitisplode.golemfirststonemod.entity.client.renderer.legends;

import com.kitisplode.golemfirststonemod.entity.client.model.legends.EntityModelGolemCobble;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemCobble;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererGolemCobble extends GeoEntityRenderer<EntityGolemCobble>
{
	public EntityRendererGolemCobble(EntityRendererProvider.Context renderManager)
	{
		super(renderManager, new EntityModelGolemCobble());
		this.shadowRadius = 0.8f;

	}

	@Override
	public void render(EntityGolemCobble entity, float entityYaw, float partialTick, PoseStack poseStack,
					   MultiBufferSource bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
		((EntityModelGolemCobble)this.model).resetCustomAnimations();
	}
}
