package com.kitisplode.golemfirststonemod.entity.client.renderer;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelGolemTuff;
import com.kitisplode.golemfirststonemod.entity.entity.golem.vote.EntityGolemTuff;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtils;

public class EntityRendererGolemTuff extends GeoEntityRenderer<EntityGolemTuff>
{
	private final HeldItemRenderer heldItemRenderer;

	public EntityRendererGolemTuff(EntityRendererFactory.Context renderManager)
	{
		super(renderManager, new EntityModelGolemTuff());
		this.shadowRadius = 0.8f;
		this.heldItemRenderer = renderManager.getHeldItemRenderer();
	}

	@Override
	public Identifier getTextureLocation(EntityGolemTuff animatable)
	{
		return animatable.getTexture();
	}

	@Override
	public void render(EntityGolemTuff entity, float entityYaw, float partialTick, MatrixStack poseStack,
					   VertexConsumerProvider bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
		renderHeldItem(entity, partialTick, poseStack, bufferSource, packedLight);
	}

	private void renderHeldItem(EntityGolemTuff entity, float partialTick, MatrixStack matrixStack, VertexConsumerProvider bufferSource, int packedLight)
	{
		ItemStack itemStack = entity.getEquippedStack(EquipmentSlot.MAINHAND);
		matrixStack.push();
		CoreGeoBone torso = this.getGeoModel().getAnimationProcessor().getBone("torso");
		CoreGeoBone whole = this.getGeoModel().getAnimationProcessor().getBone("whole");

		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-MathHelper.lerp(partialTick, entity.prevBodyYaw, entity.getBodyYaw()) + 180));
		RenderUtils.prepMatrixForBone(matrixStack, whole);
		RenderUtils.prepMatrixForBone(matrixStack, torso);
		if (Block.getBlockFromItem(itemStack.getItem()) != Blocks.AIR) matrixStack.translate(0.0f, 0.47f, -0.5f);
		else matrixStack.translate(0.0f, 0.62f, -0.5f);
		matrixStack.scale(0.75f, 0.75f, 0.75f);
		this.heldItemRenderer.renderItem(entity, itemStack, ModelTransformationMode.GROUND, false, matrixStack, bufferSource, packedLight);
		matrixStack.pop();
	}
}
