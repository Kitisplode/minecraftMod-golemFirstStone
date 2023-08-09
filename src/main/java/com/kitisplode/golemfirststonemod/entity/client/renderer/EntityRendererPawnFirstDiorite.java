package com.kitisplode.golemfirststonemod.entity.client.renderer;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelPawnFirstDiorite;
import com.kitisplode.golemfirststonemod.entity.entity.golem.pawn.EntityPawnFirstDiorite;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererPawnFirstDiorite extends GeoEntityRenderer<EntityPawnFirstDiorite>
{
	public EntityRendererPawnFirstDiorite(EntityRendererFactory.Context renderManager)
	{
		super(renderManager, new EntityModelPawnFirstDiorite());
		this.shadowRadius = 0.4f;
	}

	@Override
	public Identifier getTextureLocation(EntityPawnFirstDiorite animatable)
	{
		int pawnType = animatable.getPawnType();
		return switch (pawnType)
		{
			case 0 -> new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/diorite_action.png");
			case 1 -> new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/diorite_foresight.png");
			default -> new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/diorite_knowledge.png");
		};
	}

	@Override
	public void render(EntityPawnFirstDiorite entity, float entityYaw, float partialTick, MatrixStack poseStack,
					   VertexConsumerProvider bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}
}
