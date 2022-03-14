package dev.tr7zw.waveycapes.support;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.tr7zw.waveycapes.CapeRenderer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraftcapes.config.MinecraftCapesConfig;
import net.minecraftcapes.player.PlayerHandler;

public class MinecraftCapesSupport implements ModSupport {

    private MinecraftCapesRenderer render = new MinecraftCapesRenderer();

    @Override
    public boolean shouldBeUsed(AbstractClientPlayer player) {
        if(!MinecraftCapesConfig.isCapeVisible()) return false;

        return PlayerHandler.getFromPlayer(player).getCapeLocation() != null;
    }

    @Override
    public CapeRenderer getRenderer() {
        return render;
    }

    private class MinecraftCapesRenderer implements CapeRenderer {

        @Override
        public void render(AbstractClientPlayer player, int part, ModelPart model, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, int overlay) {
            PlayerHandler playerHandler = PlayerHandler.getFromPlayer(player);
            VertexConsumer vertexConsumer;
            if(MinecraftCapesConfig.isCapeVisible() && playerHandler.getCapeLocation() != null) {
                vertexConsumer = ItemRenderer.getArmorFoilBuffer(multiBufferSource, RenderType.armorCutoutNoCull(playerHandler.getCapeLocation()), false, playerHandler.getHasCapeGlint());
            } else {
                vertexConsumer = ItemRenderer.getArmorFoilBuffer(multiBufferSource, RenderType.armorCutoutNoCull(player.getCloakTextureLocation()), false, false);
            }
            model.render(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        }
        
        @Override
        public VertexConsumer getVertexConsumer(MultiBufferSource multiBufferSource, AbstractClientPlayer player) {
            PlayerHandler playerHandler = PlayerHandler.getFromPlayer(player);
            if(MinecraftCapesConfig.isCapeVisible() && playerHandler.getCapeLocation() != null) {
                return ItemRenderer.getArmorFoilBuffer(multiBufferSource, RenderType.armorCutoutNoCull(playerHandler.getCapeLocation()), false, playerHandler.getHasCapeGlint());
            } else {
                return ItemRenderer.getArmorFoilBuffer(multiBufferSource, RenderType.armorCutoutNoCull(player.getCloakTextureLocation()), false, false);
            }
        }
        
        @Override
        public boolean vanillaUvValues() {
            return true;
        }
        
    }

    @Override
    public boolean blockFeatureRenderer(Object feature) {
        return false;
    }

}
