package com.yupno.culinary_wizardry.block.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.yupno.culinary_wizardry.block.entity.custom.SubAltarBlockEntity;
import com.yupno.culinary_wizardry.block.entity.render.base.CauldronQuad;
import com.yupno.culinary_wizardry.utils.CulinaryUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SubAltarRenderer implements BlockEntityRenderer<SubAltarBlockEntity> {
    private static final Vector3f ITEM_POS = new Vector3f(0.5F, 0.65F, 0.5F);
    public static final ResourceLocation WATER_TEXTURE = new ResourceLocation("minecraft:textures/block/water_still.png");
    /**
     * Size of fluid texture
     */
    private final CauldronQuad quad = new CauldronQuad((float) 8 / 2);
    private int rotationDegrees = 0;

    public SubAltarRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(SubAltarBlockEntity pBlockEntity, float pPartialTick, @NotNull PoseStack pPoseStack,
                       @NotNull MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        ItemStack item = pBlockEntity.getItemFromSlot();

        // Do Liquid Rendering
        int essenceAmount = pBlockEntity.getEssence();
        if (essenceAmount > 0) {
            long ticks = pBlockEntity.getLevel().getGameTime(); // This frame count should be common across all TEs
            double liquidHeight = (pBlockEntity.getEssence() * 0.15D / pBlockEntity.getMaxEssence()) + 0.25D;

            pPoseStack.pushPose();
            pPoseStack.translate(0.5D, liquidHeight, 0.5D);
            VertexConsumer vertexBuilder = pBufferSource.getBuffer((RenderType.entityTranslucentCull(WATER_TEXTURE)));
            quad.render(pPoseStack.last(), vertexBuilder,
                    CulinaryUtils.getLiquidEssenceColor(pBlockEntity.getFoodType()), 200,
                    0F, 1 / 32F * ((float) (ticks / 2) % 32),
                    pPackedLight);

            pPoseStack.popPose();
        }

        // Rendering Item
        if (!item.isEmpty()) {
            ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();

            pPoseStack.pushPose();
            pPoseStack.translate(ITEM_POS.x(), ITEM_POS.y(), ITEM_POS.z());
            rotationDegrees++;
            pPoseStack.mulPose(Vector3f.XP.rotationDegrees(30 + rotationDegrees));
            pPoseStack.mulPose(Vector3f.ZP.rotationDegrees(60 + rotationDegrees));
            pPoseStack.mulPose(Vector3f.YP.rotationDegrees(90 + rotationDegrees));
            pPoseStack.scale(0.3F, 0.3F, 0.3F);
            renderer.renderStatic(item, ItemTransforms.TransformType.FIXED, pPackedLight, OverlayTexture.NO_OVERLAY, pPoseStack, pBufferSource, 0);
            pPoseStack.popPose();
        }
    }
}
