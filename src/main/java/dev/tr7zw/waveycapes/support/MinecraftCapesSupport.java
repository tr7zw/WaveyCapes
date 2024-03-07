package dev.tr7zw.waveycapes.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.function.Function;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.tr7zw.waveycapes.CapeRenderer;
import dev.tr7zw.waveycapes.NMSUtil;
import dev.tr7zw.waveycapes.versionless.ModBase;
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
    private Function<AbstractClientPlayer, PlayerHandler> getCape = null;

    private void init(AbstractClientPlayer test) {
        try {
            PlayerHandler.get(test).getCapeLocation();
            getCape = PlayerHandler::get;
            ModBase.LOGGER.info("Using 'get(Player)' method for MinecraftCapes.");
            return;
        } catch (Throwable ex) {
            // ignore
        }
        try {
            PlayerHandler.get(test.getUUID()).getCapeLocation();
            getCape = player -> PlayerHandler.get(player.getUUID());
            ModBase.LOGGER.info("Using 'get(UUID)' method for MinecraftCapes.");
            return;
        } catch (Throwable ex) {
            // ignore
        }

        for (Method m : PlayerHandler.class.getMethods()) {
            try {
                if (m.getReturnType() != PlayerHandler.class && m.getParameterCount() == 1
                        && m.getParameterTypes()[0] != UUID.class) {
                    continue;
                }
                m.invoke(null, test);
                getCape = player -> {
                    try {
                        return (PlayerHandler) m.invoke(null, player);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        return null;
                    }
                };
                ModBase.LOGGER.info("Using '" + m.getName() + "' method for MinecraftCapes.");
                return;
            } catch (Throwable ex) {
                // ignore, MinecraftCapes wont work
            }
        }
        getCape = player -> null;
        ModBase.LOGGER.info("Unable to find a method for MinecraftCapes.");
    }

    @Override
    public boolean shouldBeUsed(AbstractClientPlayer player) {
        if (!MinecraftCapesConfig.isCapeVisible())
            return false;
        if (getCape == null)
            init(player);
        PlayerHandler handler = getCape.apply(player);
        return handler != null && handler.getCapeLocation() != null;
    }

    @Override
    public CapeRenderer getRenderer() {
        return render;
    }

    private class MinecraftCapesRenderer implements CapeRenderer {

        @Override
        public void render(AbstractClientPlayer player, int part, ModelPart model, PoseStack poseStack,
                MultiBufferSource multiBufferSource, int light, int overlay) {
            PlayerHandler playerHandler = getCape.apply(player);
            VertexConsumer vertexConsumer;
            if (MinecraftCapesConfig.isCapeVisible() && playerHandler.getCapeLocation() != null) {
                vertexConsumer = ItemRenderer.getArmorFoilBuffer(multiBufferSource,
                        RenderType.armorCutoutNoCull(playerHandler.getCapeLocation()), false,
                        playerHandler.getHasCapeGlint());
            } else {
                vertexConsumer = ItemRenderer.getArmorFoilBuffer(multiBufferSource,
                        RenderType.armorCutoutNoCull(NMSUtil.getPlayerCape(player)), false, false);
            }
            model.render(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        }

        @Override
        public VertexConsumer getVertexConsumer(MultiBufferSource multiBufferSource, AbstractClientPlayer player) {
            PlayerHandler playerHandler = getCape.apply(player);
            if (MinecraftCapesConfig.isCapeVisible() && playerHandler.getCapeLocation() != null) {
                return ItemRenderer.getArmorFoilBuffer(multiBufferSource,
                        RenderType.armorCutoutNoCull(playerHandler.getCapeLocation()), false,
                        playerHandler.getHasCapeGlint());
            } else {
                return ItemRenderer.getArmorFoilBuffer(multiBufferSource,
                        RenderType.armorCutoutNoCull(NMSUtil.getPlayerCape(player)), false, false);
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
