package com.kitisplode.golemfirststonemod.entity.client.renderer.other;

import com.kitisplode.golemfirststonemod.entity.client.model.other.EntityModelGolemAgent;
import com.kitisplode.golemfirststonemod.entity.entity.golem.other.EntityGolemAgent;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityRendererGolemAgent extends GeoEntityRenderer<EntityGolemAgent>
{
    public EntityRendererGolemAgent(EntityRendererProvider.Context renderManager)
    {
        super(renderManager, new EntityModelGolemAgent());
        this.shadowRadius = 0.4f;
    }

}
