package com.yupno.culinary_wizardry.block.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.yupno.culinary_wizardry.block.entity.custom.FoodAltarTier3BlockEntity;
import com.yupno.culinary_wizardry.utils.CauldronQuad;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import java.awt.*;

public class FoodAltarTier3Renderer implements BlockEntityRenderer<FoodAltarTier3BlockEntity> {
    public static final ResourceLocation WATER_TEXTURE = new ResourceLocation("minecraft:textures/block/water_still.png");
    /**
     * Size of fluid texture
     */
    private final CauldronQuad quad = new CauldronQuad((float) 10 / 2);

    public FoodAltarTier3Renderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(FoodAltarTier3BlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {

        // Do Liquid Rendering
        long ticks = pBlockEntity.getLevel().getGameTime(); // This frame count should be common across all TEs
        double liquidHeight = 0.6875D;

        pPoseStack.pushPose();
        pPoseStack.translate(0.5D, liquidHeight, 0.5D);
        VertexConsumer vertexBuilder = pBufferSource.getBuffer((RenderType.entityTranslucentCull(WATER_TEXTURE)));
        quad.render(pPoseStack.last(), vertexBuilder,
                new Color(0, 255, 0), 200,
                0F, 1 / 32F * ((float) (ticks / 2) % 32),
                pPackedLight);
        pPoseStack.popPose();
    }
}
