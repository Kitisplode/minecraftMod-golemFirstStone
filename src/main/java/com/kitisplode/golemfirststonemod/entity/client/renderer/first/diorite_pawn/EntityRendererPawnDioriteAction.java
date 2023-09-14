package com.kitisplode.golemfirststonemod.entity.client.renderer.first.diorite_pawn;

import com.kitisplode.golemfirststonemod.entity.client.model.first.diorite_pawn.EntityModelPawnDioriteAction;
import com.kitisplode.golemfirststonemod.entity.client.utils.AutoGlowingGeoLayerFixed;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.diorite_pawn.EntityPawnDioriteAction;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererPawnDioriteAction extends GeoEntityRenderer<EntityPawnDioriteAction>
{
	public EntityRendererPawnDioriteAction(EntityRendererFactory.Context renderManager)
	{
		super(renderManager, new EntityModelPawnDioriteAction());
		this.shadowRadius = 0.4f;

		this.addRenderLayer(new AutoGlowingGeoLayerFixed<>(this, EntityPawnDioriteAction.GLOW_TEXTURE));
	}
}
