package com.kitisplode.golemfirststonemod.entity.client.renderer.other;

import com.kitisplode.golemfirststonemod.entity.client.model.other.EntityModelGolemAgent;
import com.kitisplode.golemfirststonemod.entity.client.utils.AutoGlowingGeoLayerFixed;
import com.kitisplode.golemfirststonemod.entity.entity.golem.other.EntityGolemAgent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtils;

public class EntityRendererGolemAgent extends GeoEntityRenderer<EntityGolemAgent>
{
	private final AutoGlowingGeoLayerFixed<EntityGolemAgent> glowLayer = new AutoGlowingGeoLayerFixed<>(this, EntityGolemAgent.GLOWMASK);
	private final HeldItemRenderer heldItemRenderer;
	public EntityRendererGolemAgent(EntityRendererFactory.Context renderManager)
	{
		super(renderManager, new EntityModelGolemAgent());
		this.shadowRadius = 0.4f;
		this.heldItemRenderer = renderManager.getHeldItemRenderer();
	}

	@Override
	public void render(EntityGolemAgent entity, float entityYaw, float partialTick, MatrixStack poseStack,
					   VertexConsumerProvider bufferSource, int packedLight)
	{
		this.renderLayers.getRenderLayers().clear();
		if (entity.getActive())
		{
			this.addRenderLayer(glowLayer);
		}
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
		renderHeldItem(entity, partialTick, poseStack, bufferSource, packedLight);
	}


	private void renderHeldItem(EntityGolemAgent entity, float partialTick, MatrixStack matrixStack, VertexConsumerProvider bufferSource, int packedLight)
	{
		ItemStack itemStack = entity.getStackInHand(Hand.MAIN_HAND);
		if (itemStack.isEmpty()) return;

		matrixStack.push();
		CoreGeoBone hand = this.getGeoModel().getAnimationProcessor().getBone("hand_right");
		CoreGeoBone arm = this.getGeoModel().getAnimationProcessor().getBone("arm_right");
		CoreGeoBone body = this.getGeoModel().getAnimationProcessor().getBone("body");
		CoreGeoBone whole = this.getGeoModel().getAnimationProcessor().getBone("whole");

		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-MathHelper.lerp(partialTick, entity.prevBodyYaw, entity.bodyYaw) + 180));
		RenderUtils.prepMatrixForBone(matrixStack, whole);
		RenderUtils.prepMatrixForBone(matrixStack, body);
		RenderUtils.prepMatrixForBone(matrixStack, arm);
		RenderUtils.prepMatrixForBone(matrixStack, hand);
		matrixStack.translate(hand.getPivotX()/16, hand.getPivotY()/16.0, (hand.getPivotZ() - 1)/16);
		matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
		matrixStack.scale(0.75f, 0.75f, 0.75f);
		this.heldItemRenderer.renderItem(entity, itemStack, ModelTransformationMode.THIRD_PERSON_RIGHT_HAND, false, matrixStack, bufferSource, packedLight);
		matrixStack.pop();
	}
}
