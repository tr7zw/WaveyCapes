package dev.tr7zw.waveycapes;

import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class VanillaCapeRenderer implements CapeRenderer {

    @Override
    public VertexConsumer getVertexConsumer(MultiBufferSource multiBufferSource, PlayerWrapper capeRenderInfo) {
        ResourceLocation cape = capeRenderInfo.getCapeTexture();
        if (cape != null) {
            return multiBufferSource.getBuffer(RenderType.entityTranslucent(cape));
        }
        return null;
    }

    @Override
    public boolean vanillaUvValues() {
        return true;
    }

}
