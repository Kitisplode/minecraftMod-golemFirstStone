package com.kitisplode.golemfirststonemod.entity.client.renderer.legends;

import com.kitisplode.golemfirststonemod.entity.client.model.legends.EntityModelGolemGrindstone;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemGrindstone;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererGolemGrindstone extends GeoEntityRenderer<EntityGolemGrindstone>
{
	public EntityRendererGolemGrindstone(EntityRendererProvider.Context renderManager)
	{
		super(renderManager, new EntityModelGolemGrindstone());
		this.shadowRadius = 0.8f;

	}

	@Override
	public void render(EntityGolemGrindstone entity, float entityYaw, float partialTick, PoseStack poseStack,
					   MultiBufferSource bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
		((EntityModelGolemGrindstone)this.model).resetCustomAnimations();
	}
}
