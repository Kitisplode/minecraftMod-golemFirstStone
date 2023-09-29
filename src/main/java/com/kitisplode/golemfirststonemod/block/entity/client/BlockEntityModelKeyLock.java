package com.kitisplode.golemfirststonemod.block.entity.client;

import com.kitisplode.golemfirststonemod.block.entity.BlockEntityKeyLock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.example.block.entity.FertilizerBlockEntity;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class BlockEntityModelKeyLock extends DefaultedBlockGeoModel<BlockEntityKeyLock>
{
    public BlockEntityModelKeyLock()
    {
        super(BlockEntityKeyLock.MODEL);
    }
    @Override
    public ResourceLocation getAnimationResource(BlockEntityKeyLock animatable) {
        return animatable.getAnimationsLocation();
    }
    @Override
    public ResourceLocation getModelResource(BlockEntityKeyLock animatable) {
        return animatable.getModelLocation();
    }
    @Override
    public ResourceLocation getTextureResource(BlockEntityKeyLock animatable) {
        return animatable.getTextureLocation();
    }
//
//    @Override
//    public RenderType getRenderType(BlockEntityKeyLock animatable, ResourceLocation texture) {
//        return RenderType.entityTranslucent(getTextureResource(animatable));
//    }
}
