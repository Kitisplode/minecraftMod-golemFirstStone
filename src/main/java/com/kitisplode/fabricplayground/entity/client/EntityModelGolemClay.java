package com.kitisplode.fabricplayground.entity.client;

import com.kitisplode.fabricplayground.FabricPlaygroundMod;
import com.kitisplode.fabricplayground.entity.custom.EntityGolemClay;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class EntityModelGolemClay extends GeoModel<EntityGolemClay>
{
    @Override
    public Identifier getModelResource(EntityGolemClay animatable)
    {
        return new Identifier(FabricPlaygroundMod.MOD_ID, "geo/example.geo.json");
    }

    @Override
    public Identifier getTextureResource(EntityGolemClay animatable)
    {
        return new Identifier(FabricPlaygroundMod.MOD_ID, "textures/entity/example.png");
    }

    @Override
    public Identifier getAnimationResource(EntityGolemClay animatable)
    {
        return new Identifier(FabricPlaygroundMod.MOD_ID, "animations/example.animation.json");
    }

    @Override
    public void setCustomAnimations(EntityGolemClay animatable, long instanceId, AnimationState<EntityGolemClay> animationState)
    {
        CoreGeoBone head = getAnimationProcessor().getBone("head");
        if (head != null)
        {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
