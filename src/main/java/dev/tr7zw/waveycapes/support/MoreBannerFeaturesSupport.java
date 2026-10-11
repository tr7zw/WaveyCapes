//? if >= 1.21.11 {
package dev.tr7zw.waveycapes.support;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.svrt.domiirl.mbf.api.BannerCapeApi;
import dev.svrt.domiirl.mbf.layer.BannerCapeFeatureRenderer;
import dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper;
import dev.tr7zw.waveycapes.render.CapeInfos;
import dev.tr7zw.waveycapes.render.CapePass;
import dev.tr7zw.waveycapes.render.CapeRenderer;
import net.minecraft.core.Direction;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class MoreBannerFeaturesSupport implements ModSupport {

    private final BannerCapeRenderer render = new BannerCapeRenderer();

    @Override
    public boolean shouldBeUsed(PlayerWrapper capeRenderInfo) {
        var avatar = capeRenderInfo.getAvatar();
        return avatar != null && BannerCapeApi.hasBannerCape(avatar);
    }

    @Override
    public CapeRenderer getRenderer() {
        return render;
    }

    @Override
    public int priority() {
        return 100;
    }

    @Override
    public boolean blockFeatureRenderer(Object feature) {
        return feature instanceof BannerCapeFeatureRenderer;
    }

    private class BannerCapeRenderer implements CapeRenderer {

        private final ModelPart[] customCape = buildBannerCape();

        @Override
        public List<CapePass> getCapePasses(PlayerWrapper capeRenderInfo) {
            return BannerCapeApi.capePasses(capeRenderInfo.getAvatar()).stream()
                    .map(pass -> new CapePass(pass.renderType(), pass::wrap)).toList();
        }

        @Override
        public void render(PlayerWrapper capeRenderInfo, int part, ModelPart model, PoseStack poseStack,
                VertexConsumer vertexConsumer, int light, int overlay) {
            customCape[part].render(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        }

        @Override
        public boolean singleSidedTexture() {
            return true;
        }

        @Override
        public int uvSheetWidth() {
            return BannerCapeApi.TEXTURE_WIDTH;
        }

        @Override
        public int uvSheetHeight() {
            return BannerCapeApi.TEXTURE_HEIGHT;
        }

        @Override
        public CapeInfos getCapeInfo(PlayerWrapper capeRenderInfo) {
            return null;
        }

    }

    private static ModelPart[] buildBannerCape() {
        Set<Direction> sides = EnumSet.of(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
        ModelPart[] parts = new ModelPart[16];

        for (int i = 0; i < parts.length; i++) {
            ModelPart.Cube cube = new ModelPart.Cube(0, i, -5.0F, i, -1.0F, 10.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, false,
                    BannerCapeApi.TEXTURE_WIDTH, BannerCapeApi.TEXTURE_HEIGHT, sides);
            parts[i] = new ModelPart(List.of(cube), Map.of());
        }

        return parts;
    }

}
//? }
