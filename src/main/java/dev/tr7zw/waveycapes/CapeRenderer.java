package dev.tr7zw.waveycapes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;

public interface CapeRenderer {

    @Deprecated
    public default void render(AbstractClientPlayer player, int part, ModelPart model, PoseStack poseStack,
            MultiBufferSource multiBufferSource, int light, int overlay) {
    }

    //#if MC >= 12102
    public void render(PlayerWrapper capeRenderInfo, int part, ModelPart model, PoseStack poseStack,
            MultiBufferSource multiBufferSource, int light, int overlay);
    //#else
    //$$public default void render(PlayerWrapper capeRenderInfo, int part, ModelPart model, PoseStack poseStack,
    //$$        MultiBufferSource multiBufferSource, int light, int overlay) {
    //$$    render((AbstractClientPlayer) capeRenderInfo.getEntity(), part, model, poseStack, multiBufferSource, light, overlay);
    //$$}
    //#endif

    public default VertexConsumer getVertexConsumer(MultiBufferSource multiBufferSource, PlayerWrapper capeRenderInfo) {
        return null;
    }

    public boolean vanillaUvValues();

}
