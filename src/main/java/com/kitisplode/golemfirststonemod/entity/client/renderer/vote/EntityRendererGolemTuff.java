package com.kitisplode.golemfirststonemod.entity.client.renderer.vote;

import com.kitisplode.golemfirststonemod.entity.client.model.vote.EntityModelGolemTuff;
import com.kitisplode.golemfirststonemod.entity.entity.golem.vote.EntityGolemTuff;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtils;

public class EntityRendererGolemTuff extends GeoEntityRenderer<EntityGolemTuff>
{
	private final ItemInHandRenderer itemInHandRenderer;
	public EntityRendererGolemTuff(EntityRendererProvider.Context renderManager)
	{
		super(renderManager, new EntityModelGolemTuff());
		this.shadowRadius = 0.8f;
		this.itemInHandRenderer = renderManager.getItemInHandRenderer();
	}

	@Override
	public ResourceLocation getTextureLocation(EntityGolemTuff animatable)
	{
		return animatable.getTexture();
	}

	@Override
	public void render(EntityGolemTuff entity, float entityYaw, float partialTick, PoseStack poseStack,
					   MultiBufferSource bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
		renderHeldItem(entity, partialTick, poseStack, bufferSource, packedLight);
	}

	private void renderHeldItem(EntityGolemTuff entity, float partialTick, PoseStack matrixStack, MultiBufferSource bufferSource, int packedLight)
	{
		ItemStack itemStack = entity.getItemInHand(InteractionHand.MAIN_HAND);
		matrixStack.pushPose();
		CoreGeoBone torso = this.getGeoModel().getAnimationProcessor().getBone("torso");
		CoreGeoBone whole = this.getGeoModel().getAnimationProcessor().getBone("whole");

		matrixStack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTick, entity.yBodyRotO, entity.yBodyRot) + 180));
		RenderUtils.prepMatrixForBone(matrixStack, whole);
		RenderUtils.prepMatrixForBone(matrixStack, torso);
		if (Block.byItem(itemStack.getItem()) != Blocks.AIR) matrixStack.translate(0.0f, 0.47f, -0.5f);
		else matrixStack.translate(0.0f, 0.62f, -0.5f);
		matrixStack.scale(0.75f, 0.75f, 0.75f);
		this.itemInHandRenderer.renderItem(entity, itemStack, ItemDisplayContext.GROUND, false, matrixStack, bufferSource, packedLight);
		matrixStack.popPose();
	}
}
