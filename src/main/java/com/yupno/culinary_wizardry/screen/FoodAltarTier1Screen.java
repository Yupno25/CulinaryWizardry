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

public class FoodAltarTier1Screen extends AbstractContainerScreen<FoodAltarTier1Menu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(CulinaryWizardry.MOD_ID, "textures/gui/food_altar_tier1_gui.png");

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

        /** FOOD BAR FROM SUBALTAR */
        if (menu.isPresent()) {
            blit(pPoseStack, x + 78, y + 34 + (menu.getFillSize() - menu.getFillProgress()),
                    177, 30 + (menu.getFillSize() - menu.getFillProgress()), 20, menu.getFillProgress());
        }
    }

    @Override
    public void render(PoseStack pPoseStack, int mouseX, int mouseY, float delta) {
        renderBackground(pPoseStack);
        super.render(pPoseStack, mouseX, mouseY, delta);
        renderTooltip(pPoseStack, mouseX, mouseY);
    }
}
