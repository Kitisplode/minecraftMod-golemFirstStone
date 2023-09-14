package com.kitisplode.golemfirststonemod.entity.client.renderer.first.diorite_pawn;

import com.kitisplode.golemfirststonemod.entity.client.model.first.diorite_pawn.EntityModelPawnDioriteForesight;
import com.kitisplode.golemfirststonemod.entity.client.utils.AutoGlowingGeoLayerFixed;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.diorite_pawn.EntityPawnDioriteForesight;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererPawnDioriteForesight extends GeoEntityRenderer<EntityPawnDioriteForesight>
{
	public EntityRendererPawnDioriteForesight(EntityRendererFactory.Context renderManager)
	{
		super(renderManager, new EntityModelPawnDioriteForesight());
		this.shadowRadius = 0.4f;

		this.addRenderLayer(new AutoGlowingGeoLayerFixed<>(this, EntityPawnDioriteForesight.GLOW_TEXTURE));
	}
}
