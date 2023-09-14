package com.kitisplode.golemfirststonemod.entity.client.renderer.first;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.client.model.first.EntityModelGolemFirstBrick;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.EntityGolemFirstBrick;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererGolemFirstBrick extends GeoEntityRenderer<EntityGolemFirstBrick>
{
	public EntityRendererGolemFirstBrick(EntityRendererFactory.Context renderManager)
	{
		super(renderManager, new EntityModelGolemFirstBrick());
		this.shadowRadius = 1.25f;
	}

	@Override
	public Identifier getTextureLocation(EntityGolemFirstBrick animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/first/first_brick.png");
	}

	@Override
	public void render(EntityGolemFirstBrick entity, float entityYaw, float partialTick, MatrixStack poseStack,
					   VertexConsumerProvider bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}
}
