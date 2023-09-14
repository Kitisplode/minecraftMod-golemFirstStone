package com.kitisplode.golemfirststonemod.entity.client.renderer.vote;

import com.kitisplode.golemfirststonemod.entity.client.model.vote.EntityModelGolemCopper;
import com.kitisplode.golemfirststonemod.entity.entity.golem.vote.EntityGolemCopper;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererGolemCopper extends GeoEntityRenderer<EntityGolemCopper>
{
	public EntityRendererGolemCopper(EntityRendererFactory.Context renderManager)
	{
		super(renderManager, new EntityModelGolemCopper());
		this.shadowRadius = 0.4f;
	}

	@Override
	public Identifier getTextureLocation(EntityGolemCopper animatable)
	{
		return animatable.getTexture();
	}

	@Override
	public void render(EntityGolemCopper entity, float entityYaw, float partialTick, MatrixStack poseStack,
					   VertexConsumerProvider bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}
}
