package com.kitisplode.golemfirststonemod.entity.client;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemFirstDiorite;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemFirstStone;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererGolemFirstDiorite extends GeoEntityRenderer<EntityGolemFirstDiorite>
{
	public EntityRendererGolemFirstDiorite(EntityRendererFactory.Context renderManager)
	{
		super(renderManager, new EntityModelGolemFirstDiorite());
	}

	@Override
	public Identifier getTextureLocation(EntityGolemFirstDiorite animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/first_diorite.png");
	}

	@Override
	public void render(EntityGolemFirstDiorite entity, float entityYaw, float partialTick, MatrixStack poseStack,
					   VertexConsumerProvider bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}
}
