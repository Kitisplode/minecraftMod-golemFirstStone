package com.kitisplode.golemfirststonemod.entity.client.renderer;

import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelPawn;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityPawn;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererPawn extends GeoEntityRenderer<EntityPawn>
{
	public EntityRendererPawn(EntityRendererFactory.Context renderManager)
	{
		super(renderManager, new EntityModelPawn());
		this.shadowRadius = 0.4f;
	}

	@Override
	public Identifier getTextureLocation(EntityPawn animatable)
	{
		return animatable.getTextureLocation();
	}

	@Override
	public void render(EntityPawn entity, float entityYaw, float partialTick, MatrixStack poseStack,
                       VertexConsumerProvider bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}
}
