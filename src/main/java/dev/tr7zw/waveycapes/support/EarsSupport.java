package dev.tr7zw.waveycapes.support;

import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.unascribed.ears.api.EarsFeatureType;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.api.iface.EarsInhibitor;
import com.unascribed.ears.api.registry.EarsInhibitorRegistry;
import com.unascribed.ears.common.render.EarsRenderDelegate.TexSource;

import dev.tr7zw.waveycapes.CapeRenderInfo;
import dev.tr7zw.util.NMSHelper;
import dev.tr7zw.waveycapes.CapeRenderer;
import dev.tr7zw.waveycapes.NMSUtil;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class EarsSupport implements ModSupport, EarsInhibitor {

    private EarsRenderer render = new EarsRenderer();
    // dumb hack to get ears to load the texture once
    private WeakHashMap<Object, AtomicInteger> cache = new WeakHashMap<Object, AtomicInteger>();
    private ModelPart[] customCape = NMSUtil.buildCape(20, 16, x -> -1, y -> y - 1);

    public EarsSupport() {
        EarsInhibitorRegistry.register("Waveycapes", this);
    }

    @Override
    public boolean shouldBeUsed(CapeRenderInfo capeRenderInfo) {
        EarsFeatures playerFeatures = EarsFeatures.getById(capeRenderInfo.getCapeHolder().getUUID());
        return playerFeatures != null && playerFeatures.capeEnabled;
    }

    @Override
    public CapeRenderer getRenderer() {
        return render;
    }

    private ResourceLocation getPlayerCape(CapeRenderInfo capeRenderInfo, EarsFeatures playerFeatures) {
        ResourceLocation skin = capeRenderInfo.getCapeTexture();
        return NMSHelper.getResourceLocation(skin.getNamespace(), TexSource.CAPE.addSuffix(skin.getPath()));
    }

    private class EarsRenderer implements CapeRenderer {

        @Override
        public void render(CapeRenderInfo capeRenderInfo, int part, ModelPart model, PoseStack poseStack,
                MultiBufferSource multiBufferSource, int light, int overlay) {
            EarsFeatures playerFeatures = EarsFeatures.getById(capeRenderInfo.getCapeHolder().getUUID());

            VertexConsumer vertexConsumer = null;
            if (playerFeatures != null && playerFeatures.capeEnabled) {
                ResourceLocation cape = getPlayerCape(capeRenderInfo, playerFeatures);
                if (cape != null) {
                    // spotless:off
                  //#if MC >= 12100
                    vertexConsumer = ItemRenderer.getArmorFoilBuffer(multiBufferSource,
                            RenderType.armorCutoutNoCull(cape), false);
                    //#else
                  //$$  vertexConsumer = ItemRenderer.getArmorFoilBuffer(multiBufferSource,
                  //$$          RenderType.armorCutoutNoCull(cape), false, false);
                  //#endif
                  //spotless:on
                }
            }
            if (vertexConsumer == null) {
                // spotless:off
                //#if MC >= 12100
                vertexConsumer = ItemRenderer.getArmorFoilBuffer(multiBufferSource,
                        RenderType.armorCutoutNoCull(capeRenderInfo.getCapeTexture()), false);
                //#else
              //$$  vertexConsumer = ItemRenderer.getArmorFoilBuffer(multiBufferSource,
              //$$  RenderType.armorCutoutNoCull(capeRenderInfo.getCapeTexture()), false, false);
              //#endif
              //spotless:on
            }
            customCape[part].render(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        }

        @Override
        public boolean vanillaUvValues() {
            return false;
        }

    }

    @Override
    public boolean blockFeatureRenderer(Object feature) {
        return false;
    }

    @Override
    public boolean shouldInhibit(EarsFeatureType arg0, Object arg1) {
        if (arg0 == EarsFeatureType.CAPE) {
            if (cache.containsKey(arg1)) {
                return true;
            } else {
                cache.put(arg1, null);
            }
        }
        return false;
    }
}
