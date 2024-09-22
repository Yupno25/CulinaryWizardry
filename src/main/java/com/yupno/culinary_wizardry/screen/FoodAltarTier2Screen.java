package com.yupno.culinary_wizardry.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.yupno.culinary_wizardry.CulinaryWizardry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FoodAltarTier2Screen extends AbstractContainerScreen<FoodAltarTier2Menu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(CulinaryWizardry.MOD_ID, "textures/gui/food_altar_tier2_gui.png");

    public FoodAltarTier2Screen(FoodAltarTier2Menu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        /** THE INVENTORY */
        this.blit(pPoseStack, x, y, 0, 0, imageWidth, imageHeight);

        /** ITEM SLOT CULINARY ESSENCE */
        if (menu.isPresent()) {
            blit(pPoseStack, x + 78, y + 34 + (menu.getFillSize() - menu.getFillProgress()),
                    194, 30 + (menu.getFillSize() - menu.getFillProgress()), menu.getFillSize(), menu.getFillProgress());
        }

        /** SMALL FRUITS ESSENCE BAR*/
        if (menu.isSmallPresent(1)) {
            blit(pPoseStack, x + 57, y + 12 + (menu.getSmallFillSize() - menu.getSmallFillProgress(1)),
                    177, 30 + (menu.getSmallFillSize() - menu.getSmallFillProgress(1)), 1, menu.getSmallFillProgress(1));
        }

        /** SMALL GRAINS ESSENCE BAR*/
        if (menu.isSmallPresent(2)) {
            blit(pPoseStack, x + 49, y + 42 + (menu.getSmallFillSize() - menu.getSmallFillProgress(2)),
                    179, 30 + (menu.getSmallFillSize() - menu.getSmallFillProgress(2)), 1, menu.getSmallFillProgress(2));
        }

        /** SMALL PROTEINS ESSENCE BAR*/
        if (menu.isSmallPresent(3)) {
            blit(pPoseStack, x + 80, y + 80,
                    177, 47, menu.getSmallFillProgress(3), 1);
        }

        /** SMALL SUGARS ESSENCE BAR*/
        if (menu.isSmallPresent(4)) {
            blit(pPoseStack, x + 126, y + 42 + (menu.getSmallFillSize() - menu.getSmallFillProgress(4)),
                    183, 30 + (menu.getSmallFillSize() - menu.getSmallFillProgress(4)), 1, menu.getSmallFillProgress(4));
        }

        /** SMALL VEGETABLES ESSENCE BAR*/
        if (menu.isSmallPresent(5)) {
            blit(pPoseStack, x + 118, y + 12 + (menu.getSmallFillSize() - menu.getSmallFillProgress(5)),
                    185, 30 + (menu.getSmallFillSize() - menu.getSmallFillProgress(5)), 1, menu.getSmallFillProgress(5));
        }

        /** CRAFTING ANIMATION */
        int foodProgress = menu.getEatingProgress();
        int seventhMaxFoodProgress = menu.getMaxEatingProgress() / 7;

        if (foodProgress > 0) {
            superBlit(pPoseStack, x, y, 176, 0, 8, 8);

            if (foodProgress > seventhMaxFoodProgress) {
                superBlit(pPoseStack, x + 12, y + 5, 185, 0, 6, 10);
            }
            if (foodProgress > seventhMaxFoodProgress * 2) {
                superBlit(pPoseStack, x, y + 10, 192, 0, 8, 8);
            }
            if (foodProgress > seventhMaxFoodProgress * 3) {
                superBlit(pPoseStack, x + 6, y + 10, 201, 0, 12, 8);
            }
            if (foodProgress > seventhMaxFoodProgress * 4) {
                superBlit(pPoseStack, x + 7, y, 214, 0, 11, 8);
            }
            if (foodProgress > seventhMaxFoodProgress * 5) {
                superBlit(pPoseStack, x, y, 176, 11, 18, 18);
            }
            if (foodProgress > seventhMaxFoodProgress * 6) {
                superBlit(pPoseStack, x, y, 195, 11, 18, 18);
            }
        }
    }

    private final int[][] xyItemSlot = new int[][]{{60, 11}, {52, 41}, {79, 60}, {106, 41}, {98, 11}};

    private void superBlit(PoseStack pPoseStack, int pX, int pY, int pUOffset, int pVOffset, int pUWidth, int pVHeight) {
        int[] usedItemslots = menu.getUsedItemSlots();

        for (int i = 0; i < xyItemSlot.length; i++) {
            if (usedItemslots[i] == i)
                blit(pPoseStack, pX + xyItemSlot[i][0], pY + xyItemSlot[i][1], pUOffset, pVOffset, pUWidth, pVHeight);
        }
    }

    @Override
    public void render(PoseStack pPoseStack, int mouseX, int mouseY, float delta) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, mouseX, mouseY, delta);
        renderTooltip(pPoseStack, mouseX, mouseY);
    }
}
