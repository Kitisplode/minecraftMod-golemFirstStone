package com.kitisplode.golemfirststonemod.entity.client.renderer.first.diorite_pawn;

import com.kitisplode.golemfirststonemod.entity.client.model.first.diorite_pawn.EntityModelPawnDioriteKnowledge;
import com.kitisplode.golemfirststonemod.entity.client.utils.AutoGlowingGeoLayerFixed;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.diorite_pawn.EntityPawnDioriteKnowledge;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererPawnDioriteKnowledge extends GeoEntityRenderer<EntityPawnDioriteKnowledge>
{
	public EntityRendererPawnDioriteKnowledge(EntityRendererFactory.Context renderManager)
	{
		super(renderManager, new EntityModelPawnDioriteKnowledge());
		this.shadowRadius = 0.4f;

		this.addRenderLayer(new AutoGlowingGeoLayerFixed<>(this, EntityPawnDioriteKnowledge.GLOW_TEXTURE));
	}
}
