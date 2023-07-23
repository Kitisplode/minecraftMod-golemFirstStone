package com.kitisplode.fabricplayground.entity.client;

import com.kitisplode.fabricplayground.FabricPlaygroundMod;
import com.kitisplode.fabricplayground.entity.custom.EntityGolemFirstStone;
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
	}

	@Override
	public Identifier getTextureLocation(EntityGolemFirstStone animatable)
	{
		return new Identifier(FabricPlaygroundMod.MOD_ID, "textures/entity/first_stone_2.png");
	}

	@Override
	public void render(EntityGolemFirstStone entity, float entityYaw, float partialTick, MatrixStack poseStack,
					   VertexConsumerProvider bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}
}
