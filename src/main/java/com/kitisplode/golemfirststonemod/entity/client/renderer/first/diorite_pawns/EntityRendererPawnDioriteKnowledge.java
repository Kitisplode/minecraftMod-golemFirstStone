package com.kitisplode.golemfirststonemod.entity.client.renderer.first.diorite_pawns;

import com.kitisplode.golemfirststonemod.entity.client.model.first.diorite_pawns.EntityModelPawnDioriteKnowledge;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.diorite_pawns.EntityPawnDioriteKnowledge;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class EntityRendererPawnDioriteKnowledge extends GeoEntityRenderer<EntityPawnDioriteKnowledge>
{
	public EntityRendererPawnDioriteKnowledge(EntityRendererProvider.Context renderManager)
	{
		super(renderManager, new EntityModelPawnDioriteKnowledge());
		this.shadowRadius = 0.4f;
//		this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
	}
}
