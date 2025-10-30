package dev.tr7zw.waveycapes.support;

import java.util.function.Function;

import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper;
import dev.tr7zw.waveycapes.CapeRenderer;
import dev.tr7zw.waveycapes.versionless.ModBase;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraftcapes.config.MinecraftCapesConfig;
import net.minecraftcapes.player.PlayerHandler;

public class MinecraftCapesSupport implements ModSupport {

    private MinecraftCapesRenderer render = new MinecraftCapesRenderer();
    private Function<PlayerWrapper, PlayerHandler> getCape = null;

    private void init(PlayerWrapper test) {
        try {
            //#if MC >= 12109
            var entity = test.getAvatar();
            //#else
            //$$var entity = test.getEntity();
            //#endif
            PlayerHandler.get(entity.getUUID()).getCapeLocation();
            getCape = player -> PlayerHandler.get(entity.getUUID());
            ModBase.LOGGER.info("Using 'get(UUID)' method for MinecraftCapes.");
            return;
        } catch (Throwable ex) {
            // ignore
        }

        //#if MC < 12102
        //$$for (java.lang.reflect.Method m : PlayerHandler.class.getMethods()) {
        //$$    try {
        //$$        if (m.getReturnType() != PlayerHandler.class && m.getParameterCount() == 1
        //$$                && m.getParameterTypes()[0] != java.util.UUID.class) {
        //$$            continue;
        //$$        }
        //$$        m.invoke(null, test);
        //$$        getCape = player -> {
        //$$            try {
        //$$                return (PlayerHandler) m.invoke(null, player);
        //$$            } catch (IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
        //$$                return null;
        //$$            }
        //$$        };
        //$$        ModBase.LOGGER.info("Using '" + m.getName() + "' method for MinecraftCapes.");
        //$$        return;
        //$$    } catch (Throwable ex) {
        //$$        // ignore, MinecraftCapes wont work
        //$$    }
        //$$}
        //#endif
        getCape = player -> null;
        ModBase.LOGGER.info("Unable to find a method for MinecraftCapes.");
    }

    @Override
    public boolean shouldBeUsed(PlayerWrapper capeRenderInfo) {
        if (!MinecraftCapesConfig.isCapeVisible())
            return false;
        if (getCape == null)
            init(capeRenderInfo);
        PlayerHandler handler = getCape.apply(capeRenderInfo);
        return handler != null && handler.getCapeLocation() != null;
    }

    @Override
    public CapeRenderer getRenderer() {
        return render;
    }

    private class MinecraftCapesRenderer implements CapeRenderer {

        @Override
        public VertexConsumer getVertexConsumer(MultiBufferSource multiBufferSource, PlayerWrapper capeRenderInfo) {
            PlayerHandler playerHandler = getCape.apply(capeRenderInfo);
            if (MinecraftCapesConfig.isCapeVisible() && playerHandler.getCapeLocation() != null) {
                //#if MC >= 12109
                return ItemRenderer.getFoilBuffer(multiBufferSource,
                        RenderType.entityTranslucent(playerHandler.getCapeLocation()), false,
                        playerHandler.getHasCapeGlint());
                //#elseif MC >= 12100
                //$$return ItemRenderer.getArmorFoilBuffer(multiBufferSource,
                //$$        RenderType.entityTranslucent(playerHandler.getCapeLocation()), playerHandler.getHasCapeGlint());
                //#else
                //$$  return ItemRenderer.getArmorFoilBuffer(multiBufferSource,
                //$$          RenderType.entityTranslucent(playerHandler.getCapeLocation()), false,
                //$$          playerHandler.getHasCapeGlint());
                //#endif
            } else {
                //#if MC >= 12109
                return ItemRenderer.getFoilBuffer(multiBufferSource,
                        RenderType.entityTranslucent(capeRenderInfo.getCapeTexture()), false, false);
                //#elseif MC >= 12100
                //$$return ItemRenderer.getArmorFoilBuffer(multiBufferSource,
                //$$        RenderType.entityTranslucent(capeRenderInfo.getCapeTexture()), false);
                //#else
                //$$  return ItemRenderer.getArmorFoilBuffer(multiBufferSource,
                //$$          RenderType.entityTranslucent(capeRenderInfo.getCapeTexture()), false, false);
                //#endif
            }
        }

    }

    @Override
    public boolean blockFeatureRenderer(Object feature) {
        return false;
    }

}
