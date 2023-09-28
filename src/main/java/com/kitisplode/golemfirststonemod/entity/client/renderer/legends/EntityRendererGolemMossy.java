package com.kitisplode.golemfirststonemod.entity.client.renderer.legends;

import com.kitisplode.golemfirststonemod.entity.client.model.legends.EntityModelGolemMossy;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemMossy;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererGolemMossy extends GeoEntityRenderer<EntityGolemMossy>
{
	public EntityRendererGolemMossy(EntityRendererProvider.Context renderManager)
	{
		super(renderManager, new EntityModelGolemMossy());
		this.shadowRadius = 0.8f;

	}

	@Override
	public void render(EntityGolemMossy entity, float entityYaw, float partialTick, PoseStack poseStack,
					   MultiBufferSource bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
		((EntityModelGolemMossy)this.model).resetCustomAnimations();
	}
}
