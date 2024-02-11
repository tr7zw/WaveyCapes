package dev.tr7zw.waveycapes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class VanillaCapeRenderer implements CapeRenderer {

    public VertexConsumer vertexConsumer = null;

    @Override
    public void render(AbstractClientPlayer player, int part, ModelPart model, PoseStack poseStack,
            MultiBufferSource multiBufferSource, int light, int overlay) {
        model.render(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
    }

    @Override
    public VertexConsumer getVertexConsumer(MultiBufferSource multiBufferSource, AbstractClientPlayer player) {
        return multiBufferSource.getBuffer(RenderType.entityCutout(NMSUtil.getPlayerCape(player)));
    }

    @Override
    public boolean vanillaUvValues() {
        return true;
    }

}
