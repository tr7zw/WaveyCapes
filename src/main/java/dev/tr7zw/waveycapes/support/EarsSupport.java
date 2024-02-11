package dev.tr7zw.waveycapes.support;

import java.util.HashMap;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.unascribed.ears.api.EarsFeatureType;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.api.iface.EarsInhibitor;
import com.unascribed.ears.api.registry.EarsInhibitorRegistry;
import com.unascribed.ears.common.render.EarsRenderDelegate.TexSource;

import dev.tr7zw.waveycapes.CapeRenderer;
import dev.tr7zw.waveycapes.NMSUtil;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
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
    private ModelPart[] customCape = new ModelPart[16];

    public EarsSupport() {
        EarsInhibitorRegistry.register("Waveycapes", this);
        for (int i = 0; i < 16; i++) {
            CubeListBuilder modelPartBuilder = CubeListBuilder.create().texOffs(-1, i - 1).addBox(-5.0F, i, -1.0F,
                    10.0F, 1.0F, 1.0F, CubeDeformation.NONE, 1.0F, 1.0F);
            customCape[i] = new ModelPart(modelPartBuilder.getCubes().stream()
                    .map(modelCuboidData -> modelCuboidData.bake(20, 16)).collect(Collectors.toList()),
                    new HashMap<>());
        }
    }

    @Override
    public boolean shouldBeUsed(AbstractClientPlayer player) {
        EarsFeatures playerFeatures = EarsFeatures.getById(player.getUUID());
        return playerFeatures != null && playerFeatures.capeEnabled;
    }

    @Override
    public CapeRenderer getRenderer() {
        return render;
    }

    private ResourceLocation getPlayerCape(AbstractClientPlayer player, EarsFeatures playerFeatures) {
        ResourceLocation skin = NMSUtil.getPlayerCape(player);
        return new ResourceLocation(skin.getNamespace(), TexSource.CAPE.addSuffix(skin.getPath()));
    }

    private class EarsRenderer implements CapeRenderer {

        @Override
        public void render(AbstractClientPlayer player, int part, ModelPart model, PoseStack poseStack,
                MultiBufferSource multiBufferSource, int light, int overlay) {
            EarsFeatures playerFeatures = EarsFeatures.getById(player.getUUID());

            VertexConsumer vertexConsumer = null;
            if (playerFeatures != null && playerFeatures.capeEnabled) {
                ResourceLocation cape = getPlayerCape(player, playerFeatures);
                if (cape != null) {
                    vertexConsumer = ItemRenderer.getArmorFoilBuffer(multiBufferSource,
                            RenderType.armorCutoutNoCull(cape), false, false);
                }
            }
            if (vertexConsumer == null) {
                vertexConsumer = ItemRenderer.getArmorFoilBuffer(multiBufferSource,
                        RenderType.armorCutoutNoCull(NMSUtil.getPlayerCape(player)), false, false);
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
