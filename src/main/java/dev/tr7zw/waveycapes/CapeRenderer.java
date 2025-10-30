package dev.tr7zw.waveycapes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;

public interface CapeRenderer {

    public default void render(PlayerWrapper capeRenderInfo, int part, ModelPart model, PoseStack poseStack,
            VertexConsumer vertexConsumer, int light, int overlay) {
        model.render(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
    }

    public VertexConsumer getVertexConsumer(MultiBufferSource multiBufferSource, PlayerWrapper capeRenderInfo);

    public default boolean vanillaUvValues() {
        return true;
    }

}
