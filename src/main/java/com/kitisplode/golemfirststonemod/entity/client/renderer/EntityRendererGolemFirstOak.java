package com.kitisplode.golemfirststonemod.entity.client.renderer;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelGolemFirstOak;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemFirstOak;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererGolemFirstOak extends GeoEntityRenderer<EntityGolemFirstOak>
{
	public EntityRendererGolemFirstOak(EntityRendererFactory.Context renderManager)
	{
		super(renderManager, new EntityModelGolemFirstOak());
		this.shadowRadius = 1.25f;
	}

	@Override
	public Identifier getTextureLocation(EntityGolemFirstOak animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/first_oak.png");
	}

	@Override
	public void render(EntityGolemFirstOak entity, float entityYaw, float partialTick, MatrixStack poseStack,
					   VertexConsumerProvider bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}
}
