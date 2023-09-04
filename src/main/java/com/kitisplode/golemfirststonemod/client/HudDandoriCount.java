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
    private static final Identifier GOLEM_TUFF = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/golem_tuff.png");
    private static final Identifier GOLEM_COPPER = new Identifier(GolemFirstStoneMod.MOD_ID, "textures/hud/dandori/golem_copper.png");
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
        int tuff = player.getDandoriCountTuff();
        int copper = player.getDandoriCountCopper();
        int firstStone = player.getDandoriCountFirstStone();
        int firstOak = player.getDandoriCountFirstOak();
        int firstBrick = player.getDandoriCountFirstBrick();
        int firstDiorite = player.getDandoriCountFirstDiorite();
        int golemTotalMelee = iron + cobble + grindstone;
        int golemTotalRanged = snow + plank;
        int golemTotalMisc = mossy + tuff + copper;
        int firstTotal = firstStone + firstOak + firstBrick + firstDiorite;

        DataDandoriCount.FOLLOWER_TYPE currentType = player.getDandoriCurrentType();

        TextRenderer tr = client.textRenderer;

        final int start_x = 12;//width / 2 - 90;
        int draw_x = start_x;
        int draw_y = (int)((float)height * 0.95f);
        int color = 0xffffffff;

        if (!client.player.isCreative()) draw_y -= 32;

        // Firsts
        if (firstStone > 0)
        {
            drawContext.drawTexture(FIRST_STONE, draw_x, draw_y-8, 0, 0, 16, 16, 16, 16);
            if (currentType == DataDandoriCount.FOLLOWER_TYPE.FIRST_STONE)
                drawContext.drawTexture(CURSOR, draw_x, draw_y-8, 0, 0, 16, 16, 16, 16);
            draw_x += 18;
            drawContext.drawText(tr, "x " + firstStone, draw_x, draw_y, color, true);
            draw_x += 24;
        }
        if (firstOak > 0)
        {
            drawContext.drawTexture(FIRST_OAK, draw_x, draw_y-8, 0, 0, 16, 16, 16, 16);
            if (currentType == DataDandoriCount.FOLLOWER_TYPE.FIRST_OAK)
                drawContext.drawTexture(CURSOR, draw_x, draw_y-8, 0, 0, 16, 16, 16, 16);
            draw_x += 18;
            drawContext.drawText(tr, "x " + firstOak, draw_x, draw_y, color, true);
            draw_x += 24;
        }
        if (firstBrick > 0)
        {
            drawContext.drawTexture(FIRST_BRICK, draw_x, draw_y-8, 0, 0, 16, 16, 16, 16);
            if (currentType == DataDandoriCount.FOLLOWER_TYPE.FIRST_BRICK)
                drawContext.drawTexture(CURSOR, draw_x, draw_y-8, 0, 0, 16, 16, 16, 16);
            draw_x += 18;
            drawContext.drawText(tr, "x " + firstBrick, draw_x, draw_y, color, true);
            draw_x += 24;
        }
        if (firstDiorite > 0)
        {
            drawContext.drawTexture(FIRST_DIORITE, draw_x, draw_y-8, 0, 0, 16, 16, 16, 16);
            if (currentType == DataDandoriCount.FOLLOWER_TYPE.FIRST_DIORITE)
                drawContext.drawTexture(CURSOR, draw_x, draw_y-8, 0, 0, 16, 16, 16, 16);
            draw_x += 18;
            drawContext.drawText(tr, "x " + firstDiorite, draw_x, draw_y, color, true);
            draw_x += 24;
        }

        if (firstTotal > 0) draw_y -= 18;
        draw_x = start_x;
        // Melee golems


        if (iron > 0)
        {
            drawContext.drawTexture(GOLEM_IRON, draw_x, draw_y, 0, 0, 8, 8, 8, 8);
            if (currentType == DataDandoriCount.FOLLOWER_TYPE.IRON)
                drawContext.drawTexture(CURSOR, draw_x-4, draw_y-4, 0, 0, 16, 16, 16, 16);
            draw_x += 12;
            drawContext.drawText(tr, "x " + iron, draw_x, draw_y, color, true);
            draw_x += 20;
        }
        if (cobble > 0)
        {
            drawContext.drawTexture(GOLEM_COBBLE, draw_x-4, draw_y, 0, 0, 16, 8, 16, 8);
            if (currentType == DataDandoriCount.FOLLOWER_TYPE.COBBLE)
                drawContext.drawTexture(CURSOR, draw_x-4, draw_y-4, 0, 0, 16, 16, 16, 16);
            draw_x += 12;
            drawContext.drawText(tr, "x " + cobble, draw_x, draw_y, color, true);
            draw_x += 20;
        }
        if (grindstone > 0)
        {
            drawContext.drawTexture(GOLEM_GRINDSTONE, draw_x-4, draw_y, 0, 0, 16, 8, 16, 8);
            if (currentType == DataDandoriCount.FOLLOWER_TYPE.GRINDSTONE)
                drawContext.drawTexture(CURSOR, draw_x-4, draw_y-4, 0, 0, 16, 16, 16, 16);
            draw_x += 12;
            drawContext.drawText(tr, "x " + grindstone, draw_x, draw_y, color, true);
            draw_x += 20;
        }

        if (golemTotalMelee > 0) draw_y -= 12;
        draw_x = start_x;

        // Ranged golems
        if (snow > 0)
        {
            drawContext.drawTexture(GOLEM_SNOW, draw_x, draw_y, 0, 0, 8, 8, 8, 8);
            if (currentType == DataDandoriCount.FOLLOWER_TYPE.SNOW)
                drawContext.drawTexture(CURSOR, draw_x-4, draw_y-4, 0, 0, 16, 16, 16, 16);
            draw_x += 12;
            drawContext.drawText(tr, "x " + snow, draw_x, draw_y, color, true);
            draw_x += 20;
        }
        if (plank > 0)
        {
            drawContext.drawTexture(GOLEM_PLANK, draw_x, draw_y, 0, 0, 8, 8, 8, 8);
            if (currentType == DataDandoriCount.FOLLOWER_TYPE.PLANK)
                drawContext.drawTexture(CURSOR, draw_x-4, draw_y-4, 0, 0, 16, 16, 16, 16);
            draw_x += 12;
            drawContext.drawText(tr, "x " + plank, draw_x, draw_y, color, true);
            draw_x += 20;
        }

        if (golemTotalRanged > 0) draw_y -= 12;
        draw_x = start_x;

        // Misc golems
        if (mossy > 0)
        {
            drawContext.drawTexture(GOLEM_MOSSY, draw_x-4, draw_y, 0, 0, 16, 8, 16, 8);
            if (currentType == DataDandoriCount.FOLLOWER_TYPE.MOSSY)
                drawContext.drawTexture(CURSOR, draw_x-4, draw_y-4, 0, 0, 16, 16, 16, 16);
            draw_x += 12;
            drawContext.drawText(tr, "x " + mossy, draw_x, draw_y, color, true);
            draw_x += 20;
        }
        if (copper > 0)
        {
            drawContext.drawTexture(GOLEM_COPPER, draw_x-4, draw_y, 0, 0, 16, 16, 16, 16);
            if (currentType == DataDandoriCount.FOLLOWER_TYPE.COPPER)
                drawContext.drawTexture(CURSOR, draw_x-4, draw_y-4, 0, 0, 16, 16, 16, 16);
            draw_x += 12;
            drawContext.drawText(tr, "x " + copper, draw_x, draw_y, color, true);
            draw_x += 20;
        }
        if (tuff > 0)
        {
            drawContext.drawTexture(GOLEM_TUFF, draw_x, draw_y, 0, 0, 8, 8, 8, 8);
            if (currentType == DataDandoriCount.FOLLOWER_TYPE.TUFF)
                drawContext.drawTexture(CURSOR, draw_x-4, draw_y-4, 0, 0, 16, 16, 16, 16);
            draw_x += 12;
            drawContext.drawText(tr, "x " + tuff, draw_x, draw_y, color, true);
            draw_x += 20;
        }

        if (golemTotalMisc > 0) draw_y -= 12;
        draw_x = start_x;
        // Piks
        if (red > 0)
        {
            drawContext.drawTexture(PIK_RED, draw_x, draw_y, 0, 0, 8, 8, 8, 8);
            if (currentType == DataDandoriCount.FOLLOWER_TYPE.PAWN_RED)
                drawContext.drawTexture(CURSOR, draw_x-4, draw_y-4, 0, 0, 16, 16, 16, 16);
            draw_x += 12;
            drawContext.drawText(tr, "x " + red, draw_x, draw_y, color, true);
            draw_x += 20;
        }
        if (yellow > 0)
        {
            drawContext.drawTexture(PIK_YELLOW, draw_x - 4, draw_y, 0, 0, 16, 16, 16, 16);
            if (currentType == DataDandoriCount.FOLLOWER_TYPE.PAWN_YELLOW)
                drawContext.drawTexture(CURSOR, draw_x-4, draw_y-4, 0, 0, 16, 16, 16, 16);
            draw_x += 12;
            drawContext.drawText(tr, "x " + yellow, draw_x, draw_y, color, true);
            draw_x += 20;
        }
        if (blue > 0)
        {
            drawContext.drawTexture(PIK_BLUE, draw_x, draw_y, 0, 0, 8, 8, 8, 8);
            if (currentType == DataDandoriCount.FOLLOWER_TYPE.PAWN_BLUE)
                drawContext.drawTexture(CURSOR, draw_x-4, draw_y-4, 0, 0, 16, 16, 16, 16);
            draw_x += 12;
            drawContext.drawText(tr, "x " + blue, draw_x, draw_y, color, true);
            draw_x += 20;
        }
    }
}
