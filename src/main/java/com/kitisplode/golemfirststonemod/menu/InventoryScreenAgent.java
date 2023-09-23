package com.kitisplode.golemfirststonemod.menu;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.other.EntityGolemAgent;
import com.kitisplode.golemfirststonemod.item.item.ItemInstruction;
import com.kitisplode.golemfirststonemod.menu.InventoryMenuAgent;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class InventoryScreenAgent extends AbstractContainerScreen<InventoryMenuAgent>
{
    private static final ResourceLocation BG = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/hud/screen_agent.png");
    private EntityGolemAgent agent;

    /** The mouse x-position recorded during the last rendered frame. */
    private float xMouse;
    /** The mouse y-position recorded during the last rendered frame. */
    private float yMouse;

    public InventoryScreenAgent(InventoryMenuAgent pMenu, Inventory pPlayerInventory, EntityGolemAgent pAgent)
    {
        super(pMenu, pPlayerInventory, pAgent.getDisplayName());
        this.agent = pAgent;
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY)
    {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        // Draw the main background
        pGuiGraphics.blit(BG, i, j, 0, 0, this.imageWidth, this.imageHeight);
        // Draw the main slots
        pGuiGraphics.blit(BG, i + 79, j + 17, 0, this.imageHeight, 5 * 18, 54);

        // Draw the hand slot
        pGuiGraphics.blit(BG, i + 7, j + 35 - 18, 18, this.imageHeight + 54, 18, 18);

        InventoryScreen.renderEntityInInventoryFollowsMouse(pGuiGraphics, i + 51, j + 60, 25, (float)(i + 51) - this.xMouse, (float)(j + 75 - 50) - this.yMouse, this.agent);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pGuiGraphics);
        this.xMouse = (float)pMouseX;
        this.yMouse = (float)pMouseY;
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.inner_render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    public void inner_render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick)
    {
        int i = this.leftPos;
        int j = this.topPos;
        RenderSystem.disableDepthTest();
        pGuiGraphics.pose().pushPose();
        pGuiGraphics.pose().translate((float)i, (float)j, 0.0F);

        for(int k = 0; k < this.menu.slots.size(); ++k) {
            Slot slot = this.menu.slots.get(k);

            if (this.isHovering(slot, pMouseX, pMouseY) && slot.isActive()) {
                ItemStack item = slot.getItem();
                if (item.getItem() instanceof ItemInstruction itemInstruction)
                {
                    int highlightedSlotCount = itemInstruction.getInstructionCount();
                    for (int s = 1; s < highlightedSlotCount + 1; s++)
                    {
                        if (s >= this.menu.slots.size()) break;
                        Slot extraSlot = this.menu.slots.get(k + s);
                        renderSlotHighlight(pGuiGraphics, extraSlot.x, extraSlot.y, 0, getSlotColor(k + s));
                    }
                }
            }
        }

        pGuiGraphics.pose().popPose();
        RenderSystem.enableDepthTest();
    }

    private boolean isHovering(Slot pSlot, double pMouseX, double pMouseY) {
        return this.isHovering(pSlot.x, pSlot.y, 16, 16, pMouseX, pMouseY);
    }
}
