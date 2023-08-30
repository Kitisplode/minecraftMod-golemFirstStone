package com.kitisplode.golemfirststonemod.entity.client.renderer;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelGolemMossy;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemMossy;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererGolemMossy extends GeoEntityRenderer<EntityGolemMossy>
{
	public EntityRendererGolemMossy(EntityRendererFactory.Context renderManager)
	{
		super(renderManager, new EntityModelGolemMossy());
		this.shadowRadius = 0.8f;
	}

	@Override
	public Identifier getTextureLocation(EntityGolemMossy animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/golem_mossy.png");
	}

	@Override
	public void render(EntityGolemMossy entity, float entityYaw, float partialTick, MatrixStack poseStack,
					   VertexConsumerProvider bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}
}
