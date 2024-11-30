package com.yupno.culinary_wizardry.block.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yupno.culinary_wizardry.block.entity.custom.FoodAltarTier4BE;
import com.yupno.culinary_wizardry.block.entity.render.base.BaseFoodAltarRenderer;
import com.yupno.culinary_wizardry.block.entity.render.base.CauldronQuad;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class FoodAltarTier4Renderer implements BlockEntityRenderer<FoodAltarTier4BE> {
    /**
     * Size of fluid texture
     */
    private final CauldronQuad quad = new CauldronQuad((float) 10 / 2);

    public FoodAltarTier4Renderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(@NotNull FoodAltarTier4BE pBlockEntity, float pPartialTick, @NotNull PoseStack pPoseStack,
                       @NotNull MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        BaseFoodAltarRenderer.altarLiquidRendering(pBlockEntity, pPoseStack, pBufferSource, pPackedLight, quad, new Color(0, 255, 0));
    }
}
