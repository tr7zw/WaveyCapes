package dev.tr7zw.waveycapes.support.laby;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper;
import dev.tr7zw.waveycapes.NMSUtil;
import dev.tr7zw.waveycapes.render.CapeInfos;
import dev.tr7zw.waveycapes.render.CapeRenderer;
import dev.tr7zw.waveycapes.support.ModSupport;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.entity.player.Player;
import net.labymod.api.user.shop.cloak.ExternalCloakRenderer;
import net.labymod.api.user.shop.cloak.RenderedCloak;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
//? if >= 1.21.11 {

import net.minecraft.client.renderer.rendertype.*;
//? } else {

/*import net.minecraft.client.renderer.*;
*///? }

public class LabySupport implements ModSupport, ExternalCloakRenderer {

    private final LabyModCloakRenderer renderer = new LabyModCloakRenderer();

    @Override
    public String id() {
        return "waveycapes:render";
    }

    @Override
    public Component displayName() {
        return Component.text("Wavey Capes");
    }

    @Override
    public boolean rendersCloakOf(Player player) {
        return true;
    }

    @Override
    public boolean shouldBeUsed(PlayerWrapper capeRenderInfo) {
        return this.cloak(capeRenderInfo) != null;
    }

    @Override
    public CapeRenderer getRenderer() {
        return this.renderer;
    }

    @Override
    public boolean blockFeatureRenderer(Object feature) {
        return false;
    }

    private RenderedCloak cloak(PlayerWrapper capeRenderInfo) {
        var player = LabyCloak.player(capeRenderInfo);
        return player == null ? null : LabyCloak.service().renderedCloak(player);
    }

    private class LabyModCloakRenderer implements CapeRenderer {

        private ModelPart[] parts;
        private int builtWidth;
        private int builtHeight;

        @Override
        public CapeInfos getCapeInfo(PlayerWrapper capeRenderInfo) {
            RenderedCloak cloak = LabySupport.this.cloak(capeRenderInfo);
            if (cloak == null) {
                return null;
            }

            this.buildParts(cloak);

            var texture = cloak.texture();
            return new CapeInfos(this, RenderTypes.entityTranslucent((Identifier) (Object) texture), false);
        }

        @Override
        public void render(PlayerWrapper capeRenderInfo, int part, ModelPart model, PoseStack poseStack,
                VertexConsumer vertexConsumer, int light, int overlay) {
            ModelPart[] cape = this.parts;
            if (cape == null) {
                return;
            }
            cape[part].render(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        }

        @Override
        public int uvSheetWidth() {
            return this.builtWidth;
        }

        @Override
        public int uvSheetHeight() {
            return this.builtHeight;
        }

        private void buildParts(RenderedCloak cloak) {
            if (this.parts != null && this.builtWidth == cloak.textureWidth()
                    && this.builtHeight == cloak.textureHeight()) {
                return;
            }

            this.builtWidth = cloak.textureWidth();
            this.builtHeight = cloak.textureHeight();
            // buildCape bakes the parts with a half scale on V, so the height is doubled to land
            // back on the sheet LabyMod normalizes its UVs against.
            this.parts = NMSUtil.buildCape(this.builtWidth, this.builtHeight * 2, x -> 0, y -> y);
        }

    }

}
