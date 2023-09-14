package com.kitisplode.golemfirststonemod.entity.client.renderer.first.diorite_pawn;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.client.model.legends.EntityModelGolemCobble;
import com.kitisplode.golemfirststonemod.entity.entity.golem.legends.EntityGolemCobble;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class EntityRendererPawnDioriteAction extends GeoEntityRenderer<EntityGolemCobble>
{
	public EntityRendererPawnDioriteAction(EntityRendererFactory.Context renderManager)
	{
		super(renderManager, new EntityModelGolemCobble());
		this.shadowRadius = 0.4f;

		this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
	}
}
