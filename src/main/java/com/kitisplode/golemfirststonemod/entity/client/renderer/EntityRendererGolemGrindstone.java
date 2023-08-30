package com.kitisplode.golemfirststonemod.entity.client.renderer;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelGolemGrindstone;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemGrindstone;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererGolemGrindstone extends GeoEntityRenderer<EntityGolemGrindstone>
{
	public EntityRendererGolemGrindstone(EntityRendererFactory.Context renderManager)
	{
		super(renderManager, new EntityModelGolemGrindstone());
		this.shadowRadius = 0.8f;
	}

	@Override
	public Identifier getTextureLocation(EntityGolemGrindstone animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/golem_grindstone.png");
	}

	@Override
	public void render(EntityGolemGrindstone entity, float entityYaw, float partialTick, MatrixStack poseStack,
					   VertexConsumerProvider bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}
}
