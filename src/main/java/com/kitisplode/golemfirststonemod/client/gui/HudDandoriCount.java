package com.kitisplode.golemfirststonemod.client.gui;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDandoriCount;
import com.kitisplode.golemfirststonemod.util.DataDandoriCount;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class HudDandoriCount implements IGuiOverlay
{
    private static final ResourceLocation PIK_BLUE = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/pik_blue.png");
    private static final ResourceLocation PIK_RED = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/pik_red.png");
    private static final ResourceLocation PIK_YELLOW = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/pik_yellow.png");
    private static final ResourceLocation GOLEM_IRON = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/golem_iron.png");
    private static final ResourceLocation GOLEM_SNOW = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/golem_snow.png");
    private static final ResourceLocation GOLEM_COBBLE = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/golem_cobble.png");
    private static final ResourceLocation GOLEM_PLANK = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/golem_plank.png");
    private static final ResourceLocation GOLEM_MOSSY = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/golem_mossy.png");
    private static final ResourceLocation GOLEM_GRINDSTONE = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/golem_grindstone.png");
    private static final ResourceLocation GOLEM_TUFF = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/golem_tuff.png");
    private static final ResourceLocation GOLEM_COPPER = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/golem_copper.png");
    private static final ResourceLocation GOLEM_AGENT = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/golem_agent.png");
    private static final ResourceLocation FIRST_STONE = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/first_stone.png");
    private static final ResourceLocation FIRST_OAK = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/first_oak.png");
    private static final ResourceLocation FIRST_BRICK = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/first_brick.png");
    private static final ResourceLocation FIRST_DIORITE = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/first_diorite.png");

    private static final ResourceLocation CURSOR = new ResourceLocation(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/cursor.png");

    public static final IGuiOverlay HUD_DANDORI = new HudDandoriCount();

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int width, int height)
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.gameMode.getPlayerMode() == GameType.SPECTATOR) return;

        IEntityWithDandoriCount player = (IEntityWithDandoriCount) mc.player;
        if (player == null) return;

        int total = player.getTotalDandoriCount();
        if (total <= 0) return;

        int blue = player.getDandoriCountBlue();
        int red = player.getDandoriCountRed();
        int yellow = player.getDandoriCountYellow();
        int iron = player.getDandoriCountIron();
        int snow = player.getDandoriCountSnow();
        int cobble = player.getDandoriCountCobble();
        int plank = player.getDandoriCountPlank();
        int mossy = player.getDandoriCountMossy();
        int grindstone = player.getDandoriCountGrindstone();
        int copper = player.getDandoriCountCopper();
        int tuff = player.getDandoriCountTuff();
        int agent = player.getDandoriCountAgent();
        int firstStone = player.getDandoriCountFirstStone();
        int firstOak = player.getDandoriCountFirstOak();
        int firstBrick = player.getDandoriCountFirstBrick();
        int firstDiorite = player.getDandoriCountFirstDiorite();

        int[] golemCounts = {firstStone, firstOak, firstBrick, firstDiorite, iron, cobble, grindstone, snow, plank, mossy, copper, tuff, agent, red, yellow, blue};

        int golemTotal = 0;

        DataDandoriCount.FOLLOWER_TYPE currentType = player.getDandoriCurrentType();

        final int perRow = 3;
        final int start_x = 12;//width / 2 - 90;
        int draw_x = start_x;
        int draw_y = (int)((float)height * 0.95f);
        int color = 0xff_ffffff;

        DataDandoriCount.FOLLOWER_TYPE[] types = DataDandoriCount.FOLLOWER_TYPE.values();
        for (int i = 0; i < types.length; i++)
        {
            if (golemCounts[i] <= 0) continue;
            DataDandoriCount.FOLLOWER_TYPE type = types[i];
            boolean isCurrentType = currentType == type;
            if (type == DataDandoriCount.FOLLOWER_TYPE.FIRST_STONE) drawPikCount(guiGraphics, draw_x, draw_y-8, FIRST_STONE, 16,16, golemCounts[i], isCurrentType,0,0, mc.font, 18,8, color);
            if (type == DataDandoriCount.FOLLOWER_TYPE.FIRST_OAK) drawPikCount(guiGraphics, draw_x, draw_y-8, FIRST_OAK, 16,16, golemCounts[i], isCurrentType,0,0, mc.font, 18,8, color);
            if (type == DataDandoriCount.FOLLOWER_TYPE.FIRST_BRICK) drawPikCount(guiGraphics, draw_x, draw_y-8, FIRST_BRICK, 16,16, golemCounts[i], isCurrentType,0,0, mc.font, 18,8, color);
            if (type == DataDandoriCount.FOLLOWER_TYPE.FIRST_DIORITE) drawPikCount(guiGraphics, draw_x, draw_y-8, FIRST_DIORITE, 16,16, golemCounts[i], isCurrentType,0,0, mc.font, 18,8, color);
            if (type == DataDandoriCount.FOLLOWER_TYPE.IRON) drawPikCount(guiGraphics, draw_x, draw_y, GOLEM_IRON, 8,8, golemCounts[i], isCurrentType,-4,-4, mc.font, 12,0, color);
            if (type == DataDandoriCount.FOLLOWER_TYPE.COBBLE) drawPikCount(guiGraphics, draw_x-4, draw_y, GOLEM_COBBLE, 16,8, golemCounts[i], isCurrentType,0,-4, mc.font, 16,0, color);
            if (type == DataDandoriCount.FOLLOWER_TYPE.GRINDSTONE) drawPikCount(guiGraphics, draw_x-4, draw_y, GOLEM_GRINDSTONE, 16,8, golemCounts[i], isCurrentType,0,-4, mc.font, 16,0, color);
            if (type == DataDandoriCount.FOLLOWER_TYPE.SNOW) drawPikCount(guiGraphics, draw_x, draw_y, GOLEM_SNOW, 8,8, golemCounts[i], isCurrentType,-4,-4, mc.font, 12,0, color);
            if (type == DataDandoriCount.FOLLOWER_TYPE.PLANK) drawPikCount(guiGraphics, draw_x, draw_y, GOLEM_PLANK, 8,8, golemCounts[i], isCurrentType,-4,-4, mc.font, 12,0, color);
            if (type == DataDandoriCount.FOLLOWER_TYPE.MOSSY) drawPikCount(guiGraphics, draw_x - 4, draw_y, GOLEM_MOSSY, 16,8, golemCounts[i], isCurrentType,0,-4, mc.font, 16,0, color);
            if (type == DataDandoriCount.FOLLOWER_TYPE.COPPER) drawPikCount(guiGraphics, draw_x-4, draw_y, GOLEM_COPPER, 16,16, golemCounts[i], isCurrentType,0,-4, mc.font, 16,0, color);
            if (type == DataDandoriCount.FOLLOWER_TYPE.TUFF) drawPikCount(guiGraphics, draw_x, draw_y, GOLEM_TUFF, 8,8, golemCounts[i], isCurrentType,-4,-4, mc.font, 12,0, color);
            if (type == DataDandoriCount.FOLLOWER_TYPE.AGENT) drawPikCount(guiGraphics, draw_x, draw_y, GOLEM_AGENT, 8,8, golemCounts[i], isCurrentType,-4,-4, mc.font, 12,0, color);
            if (type == DataDandoriCount.FOLLOWER_TYPE.PAWN_RED) drawPikCount(guiGraphics, draw_x, draw_y, PIK_RED, 8,8, golemCounts[i], isCurrentType,-4,-4, mc.font, 12,0, color);
            if (type == DataDandoriCount.FOLLOWER_TYPE.PAWN_YELLOW) drawPikCount(guiGraphics, draw_x-4, draw_y, PIK_YELLOW, 16,16, golemCounts[i], isCurrentType,0,-4, mc.font, 16,0, color);
            if (type == DataDandoriCount.FOLLOWER_TYPE.PAWN_BLUE) drawPikCount(guiGraphics, draw_x, draw_y, PIK_BLUE, 8,8, golemCounts[i], isCurrentType,-4,-4, mc.font, 12,0, color);

            if (++golemTotal >= perRow)
            {
                draw_x = start_x;
                draw_y -= 18;
                golemTotal = 0;
            }
            else
            {
                if (i < 4) draw_x += 42;
                else draw_x += 32;
            }
        }
    }

    private void drawPikCount(GuiGraphics guiGraphics, int x, int y, ResourceLocation sprite, int width, int height, int count, boolean highlighted, int cursorX, int cursorY, Font font, int textOffsetX, int textOffsetY, int textColor)
    {
        guiGraphics.blit(sprite, x,y, 0,0, width,height, width,height);
        if (highlighted)
            guiGraphics.blit(CURSOR, x + cursorX, y + cursorY, 0, 0, 16, 16, 16, 16);

        guiGraphics.drawString(font, "x " + count, x + textOffsetX,y + textOffsetY, textColor, true);
    }
}
