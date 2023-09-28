package com.kitisplode.golemfirststonemod.entity.client.renderer.first.diorite_pawns;

import com.kitisplode.golemfirststonemod.entity.client.model.dungeons.EntityModelGolemKey;
import com.kitisplode.golemfirststonemod.entity.client.model.first.diorite_pawns.EntityModelPawnDioriteAction;
import com.kitisplode.golemfirststonemod.entity.client.utils.AutoGlowingGeoLayerFixed;
import com.kitisplode.golemfirststonemod.entity.entity.golem.dungeons.EntityGolemKey;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.diorite_pawns.EntityPawnDioriteAction;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererPawnDioriteAction extends GeoEntityRenderer<EntityPawnDioriteAction>
{
	public EntityRendererPawnDioriteAction(EntityRendererProvider.Context renderManager)
	{
		super(renderManager, new EntityModelPawnDioriteAction());
		this.shadowRadius = 0.4f;
		this.addRenderLayer(new AutoGlowingGeoLayerFixed<>(this, EntityPawnDioriteAction.GLOW_TEXTURE));
	}

	@Override
	public void render(EntityPawnDioriteAction entity, float entityYaw, float partialTick, PoseStack poseStack,
					   MultiBufferSource bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
		((EntityModelPawnDioriteAction)this.model).resetCustomAnimations();
	}

}
