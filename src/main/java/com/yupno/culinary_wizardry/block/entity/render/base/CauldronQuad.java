package com.yupno.culinary_wizardry.block.entity.render.base;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.phys.Vec2;

import java.awt.*;

public class CauldronQuad {
    /**
     * Taken from Favouriteless's Enchanted Github
     * Link: https://github.com/Favouriteless/Enchanted/tree/1.18.2
     */
    private static final Vec2[] uvs = new Vec2[]{new Vec2(1F, 0F), new Vec2(0F, 0F), new Vec2(0F, 1 / 32F), new Vec2(1F, 1 / 32F)};
    private final Vector3f[] positions;

    public CauldronQuad(float fluidTextureSize) {
        positions = new Vector3f[]{new Vector3f(fluidTextureSize, 0.0F, -fluidTextureSize), new Vector3f(-fluidTextureSize, 0.0F, -fluidTextureSize), new Vector3f(-fluidTextureSize, 0.0F, fluidTextureSize), new Vector3f(fluidTextureSize, 0.0F, fluidTextureSize)};
    }

    public void render(PoseStack.Pose pose, VertexConsumer vertexBuilder, Color color, int alpha, float uOffset, float vOffset, int combinedLight) {
        Matrix4f poseMatrix = pose.pose();
        for (int i = 0; i < 4; i++) {
            Vector3f localPos = positions[i];
            Vec2 quadUvs = uvs[i];

            Vector4f posVector = new Vector4f(localPos.x() / 16.0F, localPos.y() / 16.0F, localPos.z() / 16.0F, 1.0F);
            posVector.transform(poseMatrix);

            vertexBuilder.vertex(posVector.x(), posVector.y(), posVector.z(),
                    color.getRed() / 255F, color.getGreen() / 255F, color.getGreen() / 255F, alpha / 255F,
                    quadUvs.x + uOffset, quadUvs.y + vOffset,
                    OverlayTexture.NO_OVERLAY, combinedLight,
                    0F, 1F, 0F);
        }
    }
}
