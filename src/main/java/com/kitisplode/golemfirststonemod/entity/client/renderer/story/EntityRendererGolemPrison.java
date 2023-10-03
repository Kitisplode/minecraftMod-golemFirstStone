package com.kitisplode.golemfirststonemod.entity.client.renderer.story;

import com.kitisplode.golemfirststonemod.entity.client.model.dungeons.EntityModelGolemKey;
import com.kitisplode.golemfirststonemod.entity.client.model.story.EntityModelGolemPrison;
import com.kitisplode.golemfirststonemod.entity.client.utils.AutoGlowingGeoLayerFixed;
import com.kitisplode.golemfirststonemod.entity.client.utils.EmissiveAlphaLayer;
import com.kitisplode.golemfirststonemod.entity.entity.golem.dungeons.EntityGolemKey;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.diorite_pawns.EntityPawnDioriteAction;
import com.kitisplode.golemfirststonemod.entity.entity.golem.story.EntityGolemPrison;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererGolemPrison extends GeoEntityRenderer<EntityGolemPrison>
{
	public EntityRendererGolemPrison(EntityRendererProvider.Context renderManager)
	{
		super(renderManager, new EntityModelGolemPrison());
		this.shadowRadius = 0.8f;
		this.addRenderLayer(new AutoGlowingGeoLayerFixed<>(this, EntityGolemPrison.TEXTURE_GLOWMASK));
		this.addRenderLayer(new EmissiveAlphaLayer<>(this, EntityGolemPrison.TEXTURE_LIGHT_GLOWMASK));
	}

	@Override
	public void render(EntityGolemPrison entity, float entityYaw, float partialTick, PoseStack poseStack,
					   MultiBufferSource bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
		((EntityModelGolemPrison)this.model).resetCustomAnimations();
	}
}
