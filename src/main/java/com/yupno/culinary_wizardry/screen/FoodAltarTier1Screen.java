package com.yupno.culinary_wizardry.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.yupno.culinary_wizardry.CulinaryWizardry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FoodAltarTier1Screen extends AbstractContainerScreen<FoodAltarTier1Menu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(CulinaryWizardry.MOD_ID, "textures/gui/food_altar_tier1_gui.png");
    private final int imageHeight = 172;
    private final int tallerInv = 4;

    public FoodAltarTier1Screen(FoodAltarTier1Menu pMenu, Inventory pPlayerInventory, Component pTitle) {
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

        /** CRAFTING ANIMATION */
        int foodProgress = menu.getCraftingProgress();
        int seventhMaxFoodProgress = menu.getMaxCraftingProgress() / 7;

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
        int[] usedItemSlots = menu.getUsedItemSlots();

        for (int i = 0; i < xyItemSlot.length; i++) {
            if (usedItemSlots[i] == i)
                blit(pPoseStack, pX + xyItemSlot[i][0], pY + tallerInv + xyItemSlot[i][1], pUOffset, pVOffset, pUWidth, pVHeight);
        }
    }

    @Override
    public void render(PoseStack pPoseStack, int mouseX, int mouseY, float delta) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, mouseX, mouseY, delta);
        renderTooltip(pPoseStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {
        this.font.draw(pPoseStack, this.title, (float)this.titleLabelX, (float)this.titleLabelY - tallerInv, 4210752);
        this.font.draw(pPoseStack, this.playerInventoryTitle, (float)this.inventoryLabelX, (float)this.inventoryLabelY  + tallerInv, 4210752);
    }
}
