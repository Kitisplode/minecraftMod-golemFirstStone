package com.kitisplode.golemfirststonemod.entity.client.renderer;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.client.model.EntityModelGolemFirstDiorite;
import com.kitisplode.golemfirststonemod.entity.entity.golem.first.EntityGolemFirstDiorite;
import com.kitisplode.golemfirststonemod.entity.entity.golem.vote.EntityGolemTuff;
import com.kitisplode.golemfirststonemod.item.ModItems;
import com.kitisplode.golemfirststonemod.util.ExtraMath;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
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

public class EntityRendererGolemFirstDiorite extends GeoEntityRenderer<EntityGolemFirstDiorite>
{
	private final HeldItemRenderer heldItemRenderer;
	public EntityRendererGolemFirstDiorite(EntityRendererFactory.Context renderManager)
	{
		super(renderManager, new EntityModelGolemFirstDiorite());
		this.shadowRadius = 1.25f;
		this.heldItemRenderer = renderManager.getHeldItemRenderer();
	}

	@Override
	public Identifier getTextureLocation(EntityGolemFirstDiorite animatable)
	{
		return new Identifier(GolemFirstStoneMod.MOD_ID, "textures/entity/golem/first/first_diorite.png");
	}

	@Override
	public void render(EntityGolemFirstDiorite entity, float entityYaw, float partialTick, MatrixStack poseStack,
					   VertexConsumerProvider bufferSource, int packedLight)
	{
		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
		renderHeadFlame(entity, partialTick, poseStack, bufferSource, packedLight);
	}

	private void renderHeadFlame(EntityGolemFirstDiorite entity, float partialTick, MatrixStack matrixStack, VertexConsumerProvider bufferSource, int packedLight)
	{
		ItemStack itemStack = new ItemStack(ModItems.ITEM_FLAME_OF_CREATION_RED);
		if (entity.getSummonCooleddown()) itemStack = new ItemStack(ModItems.ITEM_FLAME_OF_CREATION_BLUE);
		matrixStack.push();
		CoreGeoBone flame = this.getGeoModel().getAnimationProcessor().getBone("flame");
		CoreGeoBone head = this.getGeoModel().getAnimationProcessor().getBone("head");
		CoreGeoBone body = this.getGeoModel().getAnimationProcessor().getBone("body");

		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-MathHelper.lerp(partialTick, entity.prevBodyYaw, entity.getBodyYaw()) + 180));
		RenderUtils.prepMatrixForBone(matrixStack, body);
		RenderUtils.prepMatrixForBone(matrixStack, head);
		RenderUtils.prepMatrixForBone(matrixStack, flame);
		matrixStack.translate(head.getPivotX()/16, head.getPivotY()/16.0 - 0.1, head.getPivotZ()/16);
		matrixStack.multiply(RotationAxis.POSITIVE_X.rotation(-body.getRotX()));
		matrixStack.multiply(RotationAxis.POSITIVE_X.rotation(-head.getRotX()));
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotation(head.getRotZ()));
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(partialTick, entity.prevBodyYaw, entity.getBodyYaw()) + 180));
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotation(-(float)ExtraMath.getYawBetweenPoints(entity.getEyePos(), MinecraftClient.getInstance().player.getPos()) + (float)(Math.PI)));
		matrixStack.scale(1.5f, 1.5f, 1.5f);
		this.heldItemRenderer.renderItem(entity, itemStack, ModelTransformationMode.GROUND, false, matrixStack, bufferSource, packedLight);
		matrixStack.pop();
	}
}
