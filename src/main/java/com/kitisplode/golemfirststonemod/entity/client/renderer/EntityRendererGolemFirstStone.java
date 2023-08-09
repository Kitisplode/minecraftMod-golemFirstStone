package com.kitisplode.golemfirststonemod.entity.client.renderer;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelGolemFirstStone;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemFirstStone;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererGolemFirstStone extends GeoEntityRenderer<EntityGolemFirstStone>
{
	public EntityRendererGolemFirstStone(EntityRendererFactory.Context renderManager)
	{
		super(renderManager, new EntityModelGolemFirstStone());
		this.shadowRadius = 1.25f;
	}

	@Override
	public Identifier getTextureLocation(EntityGolemFirstStone animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/first_stone_2.png");
	}

	@Override
	public void render(EntityGolemFirstStone entity, float entityYaw, float partialTick, MatrixStack poseStack,
					   VertexConsumerProvider bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}
}
