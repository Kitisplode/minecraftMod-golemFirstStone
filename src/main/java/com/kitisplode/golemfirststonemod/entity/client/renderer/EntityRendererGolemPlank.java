package com.kitisplode.golemfirststonemod.entity.client.renderer;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelGolemCobble;
import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelGolemPlank;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemCobble;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemPlank;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererGolemPlank extends GeoEntityRenderer<EntityGolemPlank>
{
	public EntityRendererGolemPlank(EntityRendererFactory.Context renderManager)
	{
		super(renderManager, new EntityModelGolemPlank());
		this.shadowRadius = 0.8f;
	}

	@Override
	public Identifier getTextureLocation(EntityGolemPlank animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/golem_plank.png");
	}

	@Override
	public void render(EntityGolemPlank entity, float entityYaw, float partialTick, MatrixStack poseStack,
					   VertexConsumerProvider bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}
}
