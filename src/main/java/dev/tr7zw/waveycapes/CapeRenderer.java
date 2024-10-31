package dev.tr7zw.waveycapes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;

public interface CapeRenderer {

    @Deprecated
    public default void render(AbstractClientPlayer player, int part, ModelPart model, PoseStack poseStack,
                       MultiBufferSource multiBufferSource, int light, int overlay) {}

    //spotless:off
    //#if MC >= 12102
    public void render(CapeRenderInfo capeRenderInfo, int part, ModelPart model, PoseStack poseStack,
            MultiBufferSource multiBufferSource, int light, int overlay);
    //#else
    //$$public default void render(CapeRenderInfo capeRenderInfo, int part, ModelPart model, PoseStack poseStack,
    //$$        MultiBufferSource multiBufferSource, int light, int overlay) {
    //$$    render(capeRenderInfo.getPlayer(), part, model, poseStack, multiBufferSource, light, overlay);
    //$$}
    //#endif
    //spotless:on

    public default VertexConsumer getVertexConsumer(MultiBufferSource multiBufferSource, CapeRenderInfo capeRenderInfo) {
        return null;
    }

    public boolean vanillaUvValues();

}
