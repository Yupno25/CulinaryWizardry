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

public class SubAltarScreen extends AbstractContainerScreen<SubAltarMenu> {
    private final ResourceLocation TEXTURE =
            new ResourceLocation(CulinaryWizardry.MOD_ID, "textures/gui/sub_altar_" + menu.getFoodType().getName() + "_gui.png");

    public SubAltarScreen(SubAltarMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
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

        /** CULINARY ESSENCE BAR */
        if (menu.getCulinaryEssence() > 0) {
            blit(pPoseStack, x + 80, y + 10 + (menu.getProgressSize() - menu.getScaledPureCulinaryEssence()),
                    176, 0, 16, menu.getScaledPureCulinaryEssence());
        }

        /** FOOD PROCESSING ANIMATION */
        int foodProgress = menu.getFoodProgress();
        int seventhMaxFoodProgress = (int)(menu.getMaxFoodProgress() / 7);

        if (foodProgress > 0) {
            blit(pPoseStack, x + 79, y + 63, 192, 0, 8, 8);

            if (foodProgress > seventhMaxFoodProgress) {
                blit(pPoseStack, x + 91, y + 68, 201, 0, 6, 10);
            }
            if (foodProgress > seventhMaxFoodProgress * 2) {
                blit(pPoseStack, x + 79, y + 73, 208, 0, 8, 8);
            }
            if (foodProgress > seventhMaxFoodProgress * 3) {
                blit(pPoseStack, x + 85, y + 73, 217, 0, 13, 8);
            }
            if (foodProgress > seventhMaxFoodProgress * 4) {
                blit(pPoseStack, x + 86, y + 63, 231, 0, 11, 8);
            }
            if (foodProgress > seventhMaxFoodProgress * 5) {
                blit(pPoseStack, x + 79, y + 63, 192, 11, 18, 18);
            }
            if (foodProgress > seventhMaxFoodProgress * 6) {
                blit(pPoseStack, x + 79, y + 63, 211, 11, 18, 18);
            }
        }

        /** TOOLTIP FOR CULINARY ESSENCE BAR */
        if (isMouseAboveArea(pMouseX, pMouseY, x + 79, y + 9, 17, 51)) {
            renderTooltip(pPoseStack, new TranslatableComponent("translatable.pure_" + menu.getFoodType().getName() + "_essence")
                    .append(": " + menu.getCulinaryEssence() + "/" + menu.getMaxCulinaryEssence()), pMouseX, pMouseY);
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
