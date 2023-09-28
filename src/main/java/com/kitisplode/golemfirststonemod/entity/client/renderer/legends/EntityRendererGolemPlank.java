package com.kitisplode.golemfirststonemod.entity.client.renderer.legends;

import com.kitisplode.golemfirststonemod.entity.client.model.legends.EntityModelGolemPlank;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemPlank;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererGolemPlank extends GeoEntityRenderer<EntityGolemPlank>
{
	public EntityRendererGolemPlank(EntityRendererProvider.Context renderManager)
	{
		super(renderManager, new EntityModelGolemPlank());
		this.shadowRadius = 0.8f;
	}

	@Override
	public void render(EntityGolemPlank entity, float entityYaw, float partialTick, PoseStack poseStack,
					   MultiBufferSource bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
		((EntityModelGolemPlank)this.model).resetCustomAnimations();
	}
}
