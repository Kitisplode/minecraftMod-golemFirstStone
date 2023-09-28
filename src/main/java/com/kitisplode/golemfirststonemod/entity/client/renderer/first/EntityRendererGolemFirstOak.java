package com.kitisplode.golemfirststonemod.entity.client.renderer.first;

import com.kitisplode.golemfirststonemod.entity.client.model.first.EntityModelGolemFirstOak;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.EntityGolemFirstOak;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererGolemFirstOak extends GeoEntityRenderer<EntityGolemFirstOak>
{
	public EntityRendererGolemFirstOak(EntityRendererProvider.Context renderManager)
	{
		super(renderManager, new EntityModelGolemFirstOak());
		this.shadowRadius = 1.25f;
	}

	@Override
	public void render(EntityGolemFirstOak entity, float entityYaw, float partialTick, PoseStack poseStack,
					   MultiBufferSource bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
		((EntityModelGolemFirstOak)this.model).resetCustomAnimations();
	}
}
