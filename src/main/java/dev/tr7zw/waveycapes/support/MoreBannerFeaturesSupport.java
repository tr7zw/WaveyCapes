//#if FABRIC
package dev.tr7zw.waveycapes.support;

import com.mojang.blaze3d.vertex.PoseStack;

import de.kxmischesdomi.morebannerfeatures.core.accessor.Bannerable;
import de.kxmischesdomi.morebannerfeatures.renderer.BannerCapeFeatureRenderer;
import de.kxmischesdomi.morebannerfeatures.utils.RendererUtils;
import dev.tr7zw.waveycapes.CapeRenderer;
import dev.tr7zw.waveycapes.NMSUtil;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.ItemStack;

public class MoreBannerFeaturesSupport implements ModSupport {

    private MoreBannerRenderer render = new MoreBannerRenderer();
    private ModelPart[] customCape = NMSUtil.buildCape(34, 54, x -> 0, y -> y);

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
