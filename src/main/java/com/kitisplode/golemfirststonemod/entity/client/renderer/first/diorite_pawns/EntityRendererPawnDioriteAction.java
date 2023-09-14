package com.kitisplode.golemfirststonemod.entity.client.renderer.first.diorite_pawns;

import com.kitisplode.golemfirststonemod.entity.client.model.first.diorite_pawns.EntityModelPawnDioriteAction;
import com.kitisplode.golemfirststonemod.entity.client.utils.AutoGlowingGeoLayerFixed;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.diorite_pawns.EntityPawnDioriteAction;
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

}