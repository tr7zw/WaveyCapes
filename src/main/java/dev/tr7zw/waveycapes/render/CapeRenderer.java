package dev.tr7zw.waveycapes.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.texture.OverlayTexture;

public interface CapeRenderer {

    default void render(PlayerWrapper capeRenderInfo, int part, ModelPart model, PoseStack poseStack,
            VertexConsumer vertexConsumer, int light, int overlay) {
        model.render(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
    }

    CapeInfos getCapeInfo(PlayerWrapper capeRenderInfo);

    default boolean vanillaUvValues() {
        return true;
    }

    default int uvSheetWidth() {
        return 64;
    }

    default int uvSheetHeight() {
        return 32;
    }

}
