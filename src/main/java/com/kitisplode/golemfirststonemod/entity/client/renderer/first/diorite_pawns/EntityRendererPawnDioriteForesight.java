package com.kitisplode.golemfirststonemod.entity.client.renderer.first.diorite_pawns;

import com.kitisplode.golemfirststonemod.entity.client.model.first.diorite_pawns.EntityModelPawnDioriteForesight;
import com.kitisplode.golemfirststonemod.entity.client.utils.AutoGlowingGeoLayerFixed;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.diorite_pawns.EntityPawnDioriteForesight;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererPawnDioriteForesight extends GeoEntityRenderer<EntityPawnDioriteForesight>
{
	public EntityRendererPawnDioriteForesight(EntityRendererProvider.Context renderManager)
	{
		super(renderManager, new EntityModelPawnDioriteForesight());
		this.shadowRadius = 0.4f;
		this.addRenderLayer(new AutoGlowingGeoLayerFixed<>(this, EntityPawnDioriteForesight.GLOW_TEXTURE));
	}

	@Override
	public void render(EntityPawnDioriteForesight entity, float entityYaw, float partialTick, PoseStack poseStack,
					   MultiBufferSource bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
		((EntityModelPawnDioriteForesight)this.model).resetCustomAnimations();
	}
}
