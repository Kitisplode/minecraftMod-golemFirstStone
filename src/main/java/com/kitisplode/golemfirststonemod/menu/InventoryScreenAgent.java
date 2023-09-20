package com.kitisplode.golemfirststonemod.menu;

import com.kitisplode.golemfirststonemod.entity.entity.golem.other.EntityGolemAgent;
import com.kitisplode.golemfirststonemod.menu.InventoryMenuAgent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class InventoryScreenAgent extends AbstractContainerScreen<InventoryMenuAgent>
{
    private static final ResourceLocation BG = new ResourceLocation("textures/gui/container/horse.png");
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
        this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }
}
