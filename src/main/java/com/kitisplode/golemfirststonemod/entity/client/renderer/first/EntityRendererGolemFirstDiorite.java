package com.kitisplode.golemfirststonemod.entity.client.renderer.first;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.client.model.first.EntityModelGolemFirstDiorite;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.EntityGolemFirstDiorite;
import com.kitisplode.golemfirststonemod.item.ModItems;
import com.kitisplode.golemfirststonemod.util.ExtraMath;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtils;

public class EntityRendererGolemFirstDiorite extends GeoEntityRenderer<EntityGolemFirstDiorite>
{
	private final ItemInHandRenderer itemInHandRenderer;
	public EntityRendererGolemFirstDiorite(EntityRendererProvider.Context renderManager)
	{
		super(renderManager, new EntityModelGolemFirstDiorite());
		this.shadowRadius = 1.25f;
		this.itemInHandRenderer = renderManager.getItemInHandRenderer();
	}

	@Override
	public ResourceLocation getTextureLocation(EntityGolemFirstDiorite animatable)
	{
		return new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/first/first_diorite.png");
	}

	@Override
	public void render(EntityGolemFirstDiorite entity, float entityYaw, float partialTick, PoseStack poseStack,
					   MultiBufferSource bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
		renderHeadFlame(entity, partialTick, poseStack, bufferSource, packedLight);
	}

	private void renderHeadFlame(EntityGolemFirstDiorite entity, float partialTick, PoseStack matrixStack, MultiBufferSource bufferSource, int packedLight)
	{
		ItemStack itemStack = new ItemStack(ModItems.ITEM_FLAME_OF_CREATION_RED.get());
		if (entity.getSummonCooleddown() && entity.getSummonState() < 3) itemStack = new ItemStack(ModItems.ITEM_FLAME_OF_CREATION_BLUE.get());
		matrixStack.pushPose();
		CoreGeoBone flame = this.getGeoModel().getAnimationProcessor().getBone("flame");
		CoreGeoBone head = this.getGeoModel().getAnimationProcessor().getBone("head");
		CoreGeoBone body = this.getGeoModel().getAnimationProcessor().getBone("body");

		matrixStack.mulPose(Axis.YP.rotationDegrees(-Mth.lerp(partialTick, entity.yBodyRotO, entity.yBodyRot) + 180));
		RenderUtils.prepMatrixForBone(matrixStack, body);
		RenderUtils.prepMatrixForBone(matrixStack, head);
		RenderUtils.prepMatrixForBone(matrixStack, flame);
		matrixStack.translate(head.getPivotX()/16, head.getPivotY()/16.0, head.getPivotZ()/16);
		matrixStack.mulPose(Axis.XP.rotation(-body.getRotX()));
		matrixStack.mulPose(Axis.XP.rotation(-head.getRotX()));
		matrixStack.mulPose(Axis.YP.rotation(head.getRotZ()));
		matrixStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yBodyRotO, entity.yBodyRot) + 180));
		matrixStack.mulPose(Axis.YP.rotation(-(float) ExtraMath.getYawBetweenPoints(entity.getEyePosition(), Minecraft.getInstance().player.position()) + (float)(Math.PI)));
		matrixStack.translate(0,-0.25,0);
		matrixStack.scale(1.5f, 1.5f, 1.5f);

		this.itemInHandRenderer.renderItem(entity, itemStack, ItemDisplayContext.GROUND, false, matrixStack, bufferSource, 255);
		matrixStack.popPose();
	}
}
