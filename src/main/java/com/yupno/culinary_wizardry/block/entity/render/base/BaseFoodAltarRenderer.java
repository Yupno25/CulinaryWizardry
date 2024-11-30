package com.yupno.culinary_wizardry.block.entity.render.base;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.awt.*;

public class BaseFoodAltarRenderer {
    public static final ResourceLocation WATER_TEXTURE = new ResourceLocation("minecraft:textures/block/water_still.png");

    public static void altarLiquidRendering(BlockEntity pBlockEntity, PoseStack pPoseStack, MultiBufferSource pBufferSource,
                                            int pPackedLight, CauldronQuad quad, Color color) {
        long ticks = pBlockEntity.getLevel().getGameTime(); // This frame count should be common across all TEs
        double liquidHeight = 0.6875D;

        pPoseStack.pushPose();
        pPoseStack.translate(0.5D, liquidHeight, 0.5D);
        VertexConsumer vertexBuilder = pBufferSource.getBuffer((RenderType.entityTranslucentCull(WATER_TEXTURE)));
        quad.render(pPoseStack.last(), vertexBuilder,
                color, 200,
                0F, 1 / 32F * ((float) (ticks / 2) % 32),
                pPackedLight);
        pPoseStack.popPose();
    }
}
