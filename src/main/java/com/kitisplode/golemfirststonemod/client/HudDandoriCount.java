package com.kitisplode.golemfirststonemod.client;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDandoriCount;
import com.kitisplode.golemfirststonemod.util.DataDandoriCount;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import javax.xml.crypto.Data;

public class HudDandoriCount implements HudRenderCallback
{
    private static final Identifier PIK_BLUE = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/pik_blue.png");
    private static final Identifier PIK_RED = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/pik_red.png");
    private static final Identifier PIK_YELLOW = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/pik_yellow.png");
    private static final Identifier GOLEM_IRON = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/golem_iron.png");
    private static final Identifier GOLEM_SNOW = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/golem_snow.png");
    private static final Identifier GOLEM_COBBLE = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/golem_cobble.png");
    private static final Identifier GOLEM_PLANK = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/golem_plank.png");
    private static final Identifier GOLEM_MOSSY = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/golem_mossy.png");
    private static final Identifier GOLEM_GRINDSTONE = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/golem_grindstone.png");
    private static final Identifier GOLEM_COPPER = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/golem_copper.png");
    private static final Identifier GOLEM_TUFF = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/golem_tuff.png");
    private static final Identifier GOLEM_AGENT = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/golem_agent.png");
    private static final Identifier FIRST_STONE = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/first_stone.png");
    private static final Identifier FIRST_OAK = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/first_oak.png");
    private static final Identifier FIRST_BRICK = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/first_brick.png");
    private static final Identifier FIRST_DIORITE = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/first_diorite.png");

    private static final Identifier CURSOR = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/cursor.png");

    @Override
    public void onHudRender(DrawContext drawContext, float tickDelta)
    {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;

        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();

        IEntityWithDandoriCount player = (IEntityWithDandoriCount) client.player;

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
        DataDandoriCount.FOLLOWER_TYPE currentType = player.getDandoriCurrentType();
        TextRenderer tr = client.textRenderer;
        final int perRow = 84;
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
            if (type == DataDandoriCount.FOLLOWER_TYPE.FIRST_STONE) drawPikCount(drawContext, draw_x, draw_y-8, FIRST_STONE, 16,16, golemCounts[i], isCurrentType,0,0, tr, 18,8, color);
            if (type == DataDandoriCount.FOLLOWER_TYPE.FIRST_OAK) drawPikCount(drawContext, draw_x, draw_y-8, FIRST_OAK, 16,16, golemCounts[i], isCurrentType,0,0, tr, 18,8, color);
            if (type == DataDandoriCount.FOLLOWER_TYPE.FIRST_BRICK) drawPikCount(drawContext, draw_x, draw_y-8, FIRST_BRICK, 16,16, golemCounts[i], isCurrentType,0,0, tr, 18,8, color);
            if (type == DataDandoriCount.FOLLOWER_TYPE.FIRST_DIORITE) drawPikCount(drawContext, draw_x, draw_y-8, FIRST_DIORITE, 16,16, golemCounts[i], isCurrentType,0,0, tr, 18,8, color);
            if (type == DataDandoriCount.FOLLOWER_TYPE.IRON) drawPikCount(drawContext, draw_x, draw_y, GOLEM_IRON, 8,8, golemCounts[i], isCurrentType,-4,-4, tr, 12,0, color);
            if (type == DataDandoriCount.FOLLOWER_TYPE.COBBLE) drawPikCount(drawContext, draw_x-4, draw_y, GOLEM_COBBLE, 16,8, golemCounts[i], isCurrentType,0,-4, tr, 16,0, color);
            if (type == DataDandoriCount.FOLLOWER_TYPE.GRINDSTONE) drawPikCount(drawContext, draw_x-4, draw_y, GOLEM_GRINDSTONE, 16,8, golemCounts[i], isCurrentType,0,-4, tr, 16,0, color);
            if (type == DataDandoriCount.FOLLOWER_TYPE.SNOW) drawPikCount(drawContext, draw_x, draw_y, GOLEM_SNOW, 8,8, golemCounts[i], isCurrentType,-4,-4, tr, 12,0, color);
            if (type == DataDandoriCount.FOLLOWER_TYPE.PLANK) drawPikCount(drawContext, draw_x, draw_y, GOLEM_PLANK, 8,8, golemCounts[i], isCurrentType,-4,-4, tr, 12,0, color);
            if (type == DataDandoriCount.FOLLOWER_TYPE.MOSSY) drawPikCount(drawContext, draw_x - 4, draw_y, GOLEM_MOSSY, 16,8, golemCounts[i], isCurrentType,0,-4, tr, 16,0, color);
            if (type == DataDandoriCount.FOLLOWER_TYPE.COPPER) drawPikCount(drawContext, draw_x-4, draw_y, GOLEM_COPPER, 16,16, golemCounts[i], isCurrentType,0,-4, tr, 16,0, color);
            if (type == DataDandoriCount.FOLLOWER_TYPE.TUFF) drawPikCount(drawContext, draw_x, draw_y, GOLEM_TUFF, 8,8, golemCounts[i], isCurrentType,-4,-4, tr, 12,0, color);
            if (type == DataDandoriCount.FOLLOWER_TYPE.AGENT) drawPikCount(drawContext, draw_x, draw_y, GOLEM_AGENT, 8,8, golemCounts[i], isCurrentType,-4,-4, tr, 12,0, color);
            if (type == DataDandoriCount.FOLLOWER_TYPE.PAWN_RED) drawPikCount(drawContext, draw_x, draw_y, PIK_RED, 8,8, golemCounts[i], isCurrentType,-4,-4, tr, 12,0, color);
            if (type == DataDandoriCount.FOLLOWER_TYPE.PAWN_YELLOW) drawPikCount(drawContext, draw_x-4, draw_y, PIK_YELLOW, 16,16, golemCounts[i], isCurrentType,0,-4, tr, 16,0, color);
            if (type == DataDandoriCount.FOLLOWER_TYPE.PAWN_BLUE) drawPikCount(drawContext, draw_x, draw_y, PIK_BLUE, 8,8, golemCounts[i], isCurrentType,-4,-4, tr, 12,0, color);

            if (i < 4) draw_x += 42;
            else draw_x += 32;
            if (draw_x >= perRow)
            {
                draw_x = start_x;
                draw_y -= 18;
            }
        }
    }

    private void drawPikCount(DrawContext drawContext, int x, int y, Identifier sprite, int width, int height, int count, boolean highlighted, int cursorX, int cursorY, TextRenderer tr, int textOffsetX, int textOffsetY, int textColor)
    {
        drawContext.drawTexture(sprite, x,y, 0,0, width,height, width,height);
        if (highlighted)
            drawContext.drawTexture(CURSOR, x + cursorX, y + cursorY, 0,0, 16, 16, 16, 16);
        drawContext.drawText(tr, "x " + count, x + textOffsetX, y + textOffsetY, textColor, true);
    }
}
