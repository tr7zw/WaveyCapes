//#if FABRIC
package dev.tr7zw.waveycapes.support;

import java.util.HashMap;
import java.util.stream.Collectors;

import com.mojang.blaze3d.vertex.PoseStack;

import de.kxmischesdomi.morebannerfeatures.core.accessor.Bannerable;
import de.kxmischesdomi.morebannerfeatures.renderer.BannerCapeFeatureRenderer;
import de.kxmischesdomi.morebannerfeatures.utils.RendererUtils;
import dev.tr7zw.waveycapes.CapeRenderer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;

public class MoreBannerFeaturesSupport implements ModSupport {

    private MoreBannerRenderer render = new MoreBannerRenderer();
    private ModelPart[] customCape = new ModelPart[16];

    public MoreBannerFeaturesSupport() {
        for (int i = 0; i < 16; i++) {
            CubeListBuilder modelPartBuilder = CubeListBuilder.create().texOffs(0, i).addBox(-5.0F, i, -1.0F, 10.0F,
                    1.0F, 1.0F, CubeDeformation.NONE, 1.0F, 0.5F);
            customCape[i] = new ModelPart(modelPartBuilder.getCubes().stream()
                    .map(modelCuboidData -> modelCuboidData.bake(34, 54)).collect(Collectors.toList()),
                    new HashMap<>());
        }
    }

    @Override
    public boolean shouldBeUsed(AbstractClientPlayer player) {
        return player instanceof Bannerable && !((Bannerable) player).getBannerItem().isEmpty()
                && ((Bannerable) player).getBannerItem().getItem() instanceof BannerItem;
    }

    @Override
    public CapeRenderer getRenderer() {
        return render;
    }

    private class MoreBannerRenderer implements CapeRenderer {

        @Override
        public void render(AbstractClientPlayer player, int part, ModelPart model, PoseStack poseStack,
                MultiBufferSource multiBufferSource, int light, int overlay) {
            ItemStack bannerItem = ((Bannerable) player).getBannerItem();

            RendererUtils.renderCanvasFromItem(bannerItem, poseStack, multiBufferSource, light, overlay,
                    customCape[part]);
        }

        @Override
        public boolean vanillaUvValues() {
            return false;
        }

    }

    @Override
    public boolean blockFeatureRenderer(Object feature) {
        return feature instanceof BannerCapeFeatureRenderer;
    }

}
//#endif
