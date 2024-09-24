package com.yupno.culinary_wizardry.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.yupno.culinary_wizardry.CulinaryWizardry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FoodAltarTier0Screen extends AbstractContainerScreen<FoodAltarTier0Menu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(CulinaryWizardry.MOD_ID, "textures/gui/food_altar_tier0_gui.png");

    public FoodAltarTier0Screen(FoodAltarTier0Menu pMenu, Inventory pPlayerInventory, Component pTitle) {
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

        /** CRAFTING ARROW */
        if (menu.isCrafting()) {
            blit(pPoseStack, x + 70, y + 39, 176, 0, menu.getScaledProgress(), 8);
        }

        /** CULINARY ESSENCE BAR */
        if (menu.getCulinaryEssence() > 0) {
            blit(pPoseStack, x + 152, y + 10 + (menu.getProgressSize() - menu.getScaledCulinaryEssence()),
                    176, 8, 16, menu.getScaledCulinaryEssence());
        }

        /** FOOD PROCESSING ANIMATION */
        int foodProgress = menu.getEatingProgress();
        int seventhMaxFoodProgress = menu.getMaxEatingProgress() / 7;

        if (foodProgress > 0) {
            blit(pPoseStack, x + 151, y + 63, 192, 8, 8, 8);

            if (foodProgress > seventhMaxFoodProgress) {
                blit(pPoseStack, x + 163, y + 68, 201, 8, 6, 10);
            }
            if (foodProgress > seventhMaxFoodProgress * 2) {
                blit(pPoseStack, x + 151, y + 73, 208, 8, 8, 8);
            }
            if (foodProgress > seventhMaxFoodProgress * 3) {
                blit(pPoseStack, x + 157, y + 73, 217, 8, 12, 8);
            }
            if (foodProgress > seventhMaxFoodProgress * 4) {
                blit(pPoseStack, x + 158, y + 63, 230, 8, 11, 8);
            }
            if (foodProgress > seventhMaxFoodProgress * 5) {
                blit(pPoseStack, x + 151, y + 63, 192, 19, 18, 18);
            }
            if (foodProgress > seventhMaxFoodProgress * 6) {
                blit(pPoseStack, x + 151, y + 63, 211, 19, 18, 18);
            }
        }

        /** TOOLTIP FOR CULINARY ESSENCE BAR */
        if (isMouseAboveArea(pMouseX, pMouseY, x + 151, y + 9, 17, 51)) {
            renderTooltip(pPoseStack, new TranslatableComponent("translatable.culinary_essence")
                    .append(": " + String.format("%,d", menu.getCulinaryEssence()) + "/" + String.format("%,d", menu.getMaxCulinaryEssence())), pMouseX, pMouseY);
        }
    }

    @Override
    public void render(PoseStack pPoseStack, int mouseX, int mouseY, float delta) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, mouseX, mouseY, delta);
        renderTooltip(pPoseStack, mouseX, mouseY);
    }

    private boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int width, int height) {
        return (pMouseX >= x && pMouseX <= x + width) && (pMouseY >= y && pMouseY <= y + height);
    }
}
