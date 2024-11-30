package com.yupno.culinary_wizardry.screen.base;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;

public class ScreenHelper extends GuiComponent {
    public static boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int width, int height) {
        return (pMouseX >= x && pMouseX <= x + width) && (pMouseY >= y && pMouseY <= y + height);
    }

    /**
     * SPECIFIC FOOD ALTAR LOGIC
     */

    public static final int tallerInv = 4;
    private static final int[][] xyItemSlot = new int[][]{{60, 11}, {52, 41}, {79, 60}, {106, 41}, {98, 11}};

    private void superBlit(PoseStack pPoseStack, int pX, int pY, int pUOffset, int pVOffset, int pUWidth, int pVHeight, BaseFoodAltarMenu menu) {
        int[] usedItemSlots = menu.getUsedItemSlots();

        for (int i = 0; i < xyItemSlot.length; i++) {
            if (usedItemSlots[i] == i)
                blit(pPoseStack, pX + xyItemSlot[i][0], pY + tallerInv + xyItemSlot[i][1], pUOffset, pVOffset, pUWidth, pVHeight);
        }
    }

    public void foodAltarCraftingAnimation(PoseStack pPoseStack, int x, int y, BaseFoodAltarMenu menu) {
        int foodProgress = menu.getCraftingProgress();
        int seventhMaxFoodProgress = menu.getMaxCraftingProgress() / 7;

        if (foodProgress > 0) {
            superBlit(pPoseStack, x, y, 176, 0, 8, 8, menu);

            if (foodProgress > seventhMaxFoodProgress) {
                superBlit(pPoseStack, x + 12, y + 5, 185, 0, 6, 10, menu);
            }
            if (foodProgress > seventhMaxFoodProgress * 2) {
                superBlit(pPoseStack, x, y + 10, 192, 0, 8, 8, menu);
            }
            if (foodProgress > seventhMaxFoodProgress * 3) {
                superBlit(pPoseStack, x + 6, y + 10, 201, 0, 12, 8, menu);
            }
            if (foodProgress > seventhMaxFoodProgress * 4) {
                superBlit(pPoseStack, x + 7, y, 214, 0, 11, 8, menu);
            }
            if (foodProgress > seventhMaxFoodProgress * 5) {
                superBlit(pPoseStack, x, y, 176, 11, 18, 18, menu);
            }
            if (foodProgress > seventhMaxFoodProgress * 6) {
                superBlit(pPoseStack, x, y, 195, 11, 18, 18, menu);
            }
        }
    }
}
