package com.kitisplode.golemfirststonemod.entity.client.renderer.other;

import com.kitisplode.golemfirststonemod.entity.client.model.other.EntityModelGolemAgent;
import com.kitisplode.golemfirststonemod.entity.client.utils.AutoGlowingGeoLayerFixed;
import com.kitisplode.golemfirststonemod.entity.entity.golem.other.EntityGolemAgent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtils;

public class EntityRendererGolemAgent extends GeoEntityRenderer<EntityGolemAgent>
{
    private final AutoGlowingGeoLayerFixed<EntityGolemAgent> glowLayer = new AutoGlowingGeoLayerFixed<>(this, EntityGolemAgent.GLOWMASK);

    private final ItemInHandRenderer itemInHandRenderer;

    public EntityRendererGolemAgent(EntityRendererProvider.Context renderManager)
    {
        super(renderManager, new EntityModelGolemAgent());
        this.shadowRadius = 0.4f;
        this.itemInHandRenderer = renderManager.getItemInHandRenderer();
    }

    @Override
    public void render(EntityGolemAgent entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight)
    {
        this.renderLayers.getRenderLayers().clear();
        if (entity.getActive())
        {
            this.addRenderLayer(glowLayer);
        }
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
        renderHeldItem(entity, partialTick, poseStack, bufferSource, packedLight);
        ((EntityModelGolemAgent)this.model).resetCustomAnimations();
    }

    private void renderHeldItem(EntityGolemAgent entity, float partialTick, PoseStack matrixStack, MultiBufferSource bufferSource, int packedLight)
    {
        ItemStack itemStack = entity.getMainHandItem();
        if (itemStack.isEmpty()) return;

        matrixStack.pushPose();
        CoreGeoBone hand = this.getGeoModel().getAnimationProcessor().getBone("hand_right");
        CoreGeoBone arm = this.getGeoModel().getAnimationProcessor().getBone("arm_right");
        CoreGeoBone body = this.getGeoModel().getAnimationProcessor().getBone("body");
        CoreGeoBone whole = this.getGeoModel().getAnimationProcessor().getBone("whole");

        matrixStack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTick, entity.yBodyRotO, entity.yBodyRot) + 180));
        RenderUtils.prepMatrixForBone(matrixStack, whole);
        RenderUtils.prepMatrixForBone(matrixStack, body);
        RenderUtils.prepMatrixForBone(matrixStack, arm);
        RenderUtils.prepMatrixForBone(matrixStack, hand);
        matrixStack.translate(hand.getPivotX()/16, hand.getPivotY()/16.0, (hand.getPivotZ() - 1)/16);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-90));
        matrixStack.scale(0.75f, 0.75f, 0.75f);
        this.itemInHandRenderer.renderItem(entity, itemStack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false, matrixStack, bufferSource, packedLight);
        matrixStack.popPose();
    }

}
