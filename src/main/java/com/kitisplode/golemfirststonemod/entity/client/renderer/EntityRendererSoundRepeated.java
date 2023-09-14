package com.kitisplode.golemfirststonemod.entity.client.renderer;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelSoundRepeated;
import com.kitisplode.golemfirststonemod.entity.entity.effect.EntitySoundRepeated;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererSoundRepeated extends GeoEntityRenderer<EntitySoundRepeated>
{
	public EntityRendererSoundRepeated(EntityRendererFactory.Context renderManager)
	{
		super(renderManager, new EntityModelSoundRepeated());
	}

	@Override
	public Identifier getTextureLocation(EntitySoundRepeated animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/first/first_stone_2.png");
	}

	@Override
	public void render(EntitySoundRepeated entity, float entityYaw, float partialTick, MatrixStack poseStack,
					   VertexConsumerProvider bufferSource, int packedLight)
	{

	}
}
