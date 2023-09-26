package com.kitisplode.golemfirststonemod.entity.client.renderer.dungeons;

import com.kitisplode.golemfirststonemod.entity.client.model.dungeons.EntityModelGolemKey;
import com.kitisplode.golemfirststonemod.entity.entity.golem.dungeons.EntityGolemKey;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererGolemKey extends GeoEntityRenderer<EntityGolemKey>
{
	public EntityRendererGolemKey(EntityRendererProvider.Context renderManager)
	{
		super(renderManager, new EntityModelGolemKey());
		this.shadowRadius = 0.4f;
	}
}
