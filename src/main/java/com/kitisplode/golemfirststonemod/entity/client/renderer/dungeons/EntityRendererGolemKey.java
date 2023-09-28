package com.kitisplode.golemfirststonemod.entity.client.renderer.dungeons;

import com.kitisplode.golemfirststonemod.entity.client.model.dungeons.EntityModelGolemKey;
import com.kitisplode.golemfirststonemod.entity.client.model.other.EntityModelGolemAgent;
import com.kitisplode.golemfirststonemod.entity.entity.golem.dungeons.EntityGolemKey;
import com.kitisplode.golemfirststonemod.entity.entity.golem.other.EntityGolemAgent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererGolemKey extends GeoEntityRenderer<EntityGolemKey>
{
	public EntityRendererGolemKey(EntityRendererProvider.Context renderManager)
	{
		super(renderManager, new EntityModelGolemKey());
		this.shadowRadius = 0.4f;
	}

	@Override
	public void render(EntityGolemKey entity, float entityYaw, float partialTick, PoseStack poseStack,
					   MultiBufferSource bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
		((EntityModelGolemKey)this.model).resetCustomAnimations();
	}
}
