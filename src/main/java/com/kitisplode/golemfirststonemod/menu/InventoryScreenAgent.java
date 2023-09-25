package com.kitisplode.golemfirststonemod.menu;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.golem.other.EntityGolemAgent;
import com.kitisplode.golemfirststonemod.item.item.ItemInstruction;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

public class InventoryScreenAgent extends HandledScreen<InventoryMenuAgent>
{
    private static final Identifier BG = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/hud/screen_agent.png");
    private EntityGolemAgent agent;
    /** The mouse x-position recorded during the last rendered frame. */
    private float xMouse;
    /** The mouse y-position recorded during the last rendered frame. */
    private float yMouse;

    public InventoryScreenAgent(InventoryMenuAgent pMenu, PlayerInventory pPlayerInventory, EntityGolemAgent pAgent)
    {
        super(pMenu, pPlayerInventory, pAgent.getDisplayName());
        this.agent = pAgent;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY)
    {
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(BG, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
        context.drawTexture(BG, i + 79, j + 17, 0, this.backgroundHeight, 5 * 18, 54);
        context.drawTexture(BG, i + 7, j + 35 - 18, 18, this.backgroundHeight + 54, 18, 18);
        InventoryScreen.drawEntity(context, i + 51, j + 60, 17, (float)(i + 51) - this.xMouse, (float)(j + 75 - 50) - this.yMouse, this.agent);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        this.xMouse = mouseX;
        this.yMouse = mouseY;
        super.render(context, mouseX, mouseY, delta);
        this.inner_render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    public void inner_render(DrawContext pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick)
    {
        int i = this.x;
        int j = this.y;
        RenderSystem.disableDepthTest();
        pGuiGraphics.getMatrices().push();
        pGuiGraphics.getMatrices().translate((float)i, (float)j, 0.0F);

        for(int k = 0; k < this.handler.slots.size(); ++k) {
            Slot slot = this.handler.slots.get(k);

            if (this.isPointOverSlot(slot, pMouseX, pMouseY) && slot.isEnabled()) {
                ItemStack item = slot.getStack();
                if (item.getItem() instanceof ItemInstruction itemInstruction)
                {
                    int highlightedSlotCount = itemInstruction.getInstructionCount();
                    for (int s = 1; s < highlightedSlotCount + 1; s++)
                    {
                        if (s >= this.handler.slots.size()) break;
                        Slot extraSlot = this.handler.slots.get(k + s);
                        drawSlotHighlight(pGuiGraphics, extraSlot.x, extraSlot.y, 0);
                    }
                }
            }
        }

        pGuiGraphics.getMatrices().pop();
        RenderSystem.enableDepthTest();
    }

    private boolean isPointOverSlot(Slot pSlot, double pMouseX, double pMouseY) {
        return this.isPointWithinBounds(pSlot.x, pSlot.y, 16, 16, pMouseX, pMouseY);
    }
}
