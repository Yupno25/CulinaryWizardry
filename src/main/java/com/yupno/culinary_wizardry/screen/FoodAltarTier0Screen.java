package com.yupno.culinary_wizardry.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.yupno.culinary_wizardry.CulinaryWizardry;
import com.yupno.culinary_wizardry.block.entity.custom.FoodAltarTier0BlockEntity;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
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

        this.blit(pPoseStack, x, y, 0, 0, imageWidth, imageHeight);

        pPoseStack.scale(1, 1, 1);

        if(menu.isCrafting()) {
            blit(pPoseStack, x + 70, y + 39, 176, 0, menu.getScaledProgress(), 8);
        }

        if(menu.getPureCulinaryEssence() > 0){
            blit(pPoseStack, x + 152, y + 10 + (50 - menu.getScaledPureCulinaryEssence()), 176, 8, 16, menu.getScaledPureCulinaryEssence());
        }


        if(isMouseAboveArea(pMouseX, pMouseY, x + 151, y + 9, 17, 51)){
            renderTooltip(pPoseStack, new TranslatableComponent("food_altar_tier0.pure_culinary_essence")
                    .append(": " + menu.getPureCulinaryEssence() + "/" + menu.getMaxPureCulinaryEssence()), pMouseX, pMouseY);
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
