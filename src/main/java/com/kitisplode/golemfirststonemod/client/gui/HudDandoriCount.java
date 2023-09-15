package com.kitisplode.golemfirststonemod.client.gui;

import com.kitisplode.golemfirststonemod.GolemFirstStoneMod;
import com.kitisplode.golemfirststonemod.entity.entity.interfaces.IEntityWithDandoriCount;
import com.kitisplode.golemfirststonemod.util.DataDandoriCount;
import net.minecraft.client.Minecraft;
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

        final int start_x = 12;//width / 2 - 90;
        int draw_x = start_x;
        int draw_y = (int)((float)height * 0.95f);
        int color = 0xff_ffffff;

        if (!mc.player.isCreative()) draw_y -= 32;

        // Firsts
        if (firstStone > 0)
        {
            guiGraphics.blit(FIRST_STONE, draw_x,draw_y-8, 0,0, 16,16, 16,16);
            if (currentType == DataDandoriCount.FOLLOWER_TYPE.FIRST_STONE)
                guiGraphics.blit(CURSOR, draw_x,draw_y-8, 0, 0, 16, 16, 16, 16);
            draw_x += 18;
            guiGraphics.drawString(mc.font, "x " + firstStone, draw_x,draw_y, color, true);
            draw_x += 24;
        }
        if (firstOak > 0)
        {
            guiGraphics.blit(FIRST_OAK, draw_x,draw_y-8, 0,0, 16,16, 16,16);
            if (currentType == DataDandoriCount.FOLLOWER_TYPE.FIRST_OAK)
                guiGraphics.blit(CURSOR, draw_x,draw_y-8, 0, 0, 16, 16, 16, 16);
            draw_x += 18;
            guiGraphics.drawString(mc.font, "x " + firstOak, draw_x,draw_y, color, true);
            draw_x += 24;
        }
        if (firstBrick > 0)
        {
            guiGraphics.blit(FIRST_BRICK, draw_x,draw_y-8, 0,0, 16,16, 16,16);
            if (currentType == DataDandoriCount.FOLLOWER_TYPE.FIRST_BRICK)
                guiGraphics.blit(CURSOR, draw_x,draw_y-8, 0, 0, 16, 16, 16, 16);
            draw_x += 18;
            guiGraphics.drawString(mc.font, "x " + firstBrick, draw_x,draw_y, color, true);
            draw_x += 24;
        }
        if (firstDiorite > 0)
        {
            guiGraphics.blit(FIRST_DIORITE, draw_x,draw_y-8, 0,0, 16,16, 16,16);
            if (currentType == DataDandoriCount.FOLLOWER_TYPE.FIRST_DIORITE)
                guiGraphics.blit(CURSOR, draw_x,draw_y-8, 0, 0, 16, 16, 16, 16);
            draw_x += 18;
            guiGraphics.drawString(mc.font, "x " + firstDiorite, draw_x,draw_y, color, true);
            draw_x += 24;
        }

        if (firstTotal > 0) draw_y -= 18;
        draw_x = start_x;

        // Melee Golems
        if (iron > 0)
        {
            guiGraphics.blit(GOLEM_IRON, draw_x,draw_y, 0,0, 8,8, 8,8);
            if (currentType == DataDandoriCount.FOLLOWER_TYPE.IRON)
                guiGraphics.blit(CURSOR, draw_x-4,draw_y-4, 0, 0, 16, 16, 16, 16);
            draw_x += 12;
            guiGraphics.drawString(mc.font, "x " + iron, draw_x,draw_y, color, true);
            draw_x += 20;
        }
        if (cobble > 0)
        {
            guiGraphics.blit(GOLEM_COBBLE, draw_x-4,draw_y, 0,0, 16,8, 16,8);
            if (currentType == DataDandoriCount.FOLLOWER_TYPE.COBBLE)
                guiGraphics.blit(CURSOR, draw_x-4,draw_y-4, 0, 0, 16, 16, 16, 16);
            draw_x += 12;
            guiGraphics.drawString(mc.font, "x " + cobble, draw_x,draw_y, color, true);
            draw_x += 20;
        }
        if (grindstone > 0)
        {
            guiGraphics.blit(GOLEM_GRINDSTONE, draw_x-4,draw_y, 0,0, 16,8, 16,8);
            if (currentType == DataDandoriCount.FOLLOWER_TYPE.GRINDSTONE)
                guiGraphics.blit(CURSOR, draw_x-4,draw_y-4, 0, 0, 16, 16, 16, 16);
            draw_x += 12;
            guiGraphics.drawString(mc.font, "x " + grindstone, draw_x,draw_y, color, true);
            draw_x += 20;
        }

        if (golemTotalMelee > 0) draw_y -= 18;
        draw_x = start_x;

        // Ranged Golems
        if (snow > 0)
        {
            guiGraphics.blit(GOLEM_SNOW, draw_x,draw_y, 0,0, 8,8, 8,8);
            if (currentType == DataDandoriCount.FOLLOWER_TYPE.SNOW)
                guiGraphics.blit(CURSOR, draw_x-4,draw_y-4, 0, 0, 16, 16, 16, 16);
            draw_x += 12;
            guiGraphics.drawString(mc.font, "x " + snow, draw_x,draw_y, color, true);
            draw_x += 20;
        }
        if (plank > 0)
        {
            guiGraphics.blit(GOLEM_PLANK, draw_x,draw_y, 0,0, 8,8, 8,8);
            if (currentType == DataDandoriCount.FOLLOWER_TYPE.PLANK)
                guiGraphics.blit(CURSOR, draw_x-4,draw_y-4, 0, 0, 16, 16, 16, 16);
            draw_x += 12;
            guiGraphics.drawString(mc.font, "x " + plank, draw_x,draw_y, color, true);
            draw_x += 20;
        }

        if (golemTotalRanged > 0) draw_y -= 18;
        draw_x = start_x;

        // Misc Golems
        if (mossy > 0)
        {
            guiGraphics.blit(GOLEM_MOSSY, draw_x-4,draw_y, 0,0, 16,8, 16,8);
            if (currentType == DataDandoriCount.FOLLOWER_TYPE.MOSSY)
                guiGraphics.blit(CURSOR, draw_x-4,draw_y-4, 0, 0, 16, 16, 16, 16);
            draw_x += 12;
            guiGraphics.drawString(mc.font, "x " + mossy, draw_x,draw_y, color, true);
            draw_x += 20;
        }
        if (tuff > 0)
        {
            guiGraphics.blit(GOLEM_TUFF, draw_x,draw_y, 0,0, 8,8, 8,8);
            if (currentType == DataDandoriCount.FOLLOWER_TYPE.TUFF)
                guiGraphics.blit(CURSOR, draw_x-4,draw_y-4, 0, 0, 16, 16, 16, 16);
            draw_x += 12;
            guiGraphics.drawString(mc.font, "x " + tuff, draw_x,draw_y, color, true);
            draw_x += 20;
        }
        if (copper > 0)
        {
            guiGraphics.blit(GOLEM_COPPER, draw_x-4,draw_y, 0,0, 16,16, 16,16);
            if (currentType == DataDandoriCount.FOLLOWER_TYPE.COPPER)
                guiGraphics.blit(CURSOR, draw_x-4,draw_y-4, 0, 0, 16, 16, 16, 16);
            draw_x += 12;
            guiGraphics.drawString(mc.font, "x " + copper, draw_x,draw_y, color, true);
            draw_x += 20;
        }

        if (golemTotalMisc > 0) draw_y -= 18;
        draw_x = start_x;

        // Pawns
        if (red > 0)
        {
            guiGraphics.blit(PIK_RED, draw_x,draw_y, 0,0, 8,8, 8,8);
            if (currentType == DataDandoriCount.FOLLOWER_TYPE.PAWN_RED)
                guiGraphics.blit(CURSOR, draw_x-4,draw_y-4, 0, 0, 16, 16, 16, 16);
            draw_x += 12;
            guiGraphics.drawString(mc.font, "x " + red, draw_x,draw_y, color, true);
            draw_x += 20;
        }
        if (yellow > 0)
        {
            guiGraphics.blit(PIK_YELLOW, draw_x-4,draw_y, 0,0, 16,16, 16,16);
            if (currentType == DataDandoriCount.FOLLOWER_TYPE.PAWN_YELLOW)
                guiGraphics.blit(CURSOR, draw_x-4,draw_y-4, 0, 0, 16, 16, 16, 16);
            draw_x += 12;
            guiGraphics.drawString(mc.font, "x " + yellow, draw_x,draw_y, color, true);
            draw_x += 20;
        }
        if (blue > 0)
        {
            guiGraphics.blit(PIK_BLUE, draw_x,draw_y, 0,0, 8,8, 8,8);
            if (currentType == DataDandoriCount.FOLLOWER_TYPE.PAWN_BLUE)
                guiGraphics.blit(CURSOR, draw_x-4,draw_y-4, 0, 0, 16, 16, 16, 16);
            draw_x += 12;
            guiGraphics.drawString(mc.font, "x " + blue, draw_x,draw_y, color, true);
            draw_x += 20;
        }
    }
}
