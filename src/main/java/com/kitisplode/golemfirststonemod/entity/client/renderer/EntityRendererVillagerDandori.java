package com.kitisplode.golemfirststonemod.entity.client.renderer;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelGolemFirstStone;
import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelVillagerDandori;
import com.kitisplode.golemfirststonemod.entity.entity.EntityVillagerDandori;
import com.kitisplode.golemfirststonemod.entity.entity.golem.EntityGolemFirstStone;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererVillagerDandori extends GeoEntityRenderer<EntityVillagerDandori>
{
	public EntityRendererVillagerDandori(EntityRendererFactory.Context renderManager)
	{
		super(renderManager, new EntityModelVillagerDandori());
		this.shadowRadius = 0.8f;
	}

	@Override
	public Identifier getTextureLocation(EntityVillagerDandori animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/villager_dandori.png");
	}

	@Override
	public void render(EntityVillagerDandori entity, float entityYaw, float partialTick, MatrixStack poseStack,
					   VertexConsumerProvider bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}
}
