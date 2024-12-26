package dev.tr7zw.waveycapes.support;

import dev.tr7zw.waveycapes.CapeRenderInfo;
import dev.tr7zw.waveycapes.CapeRenderer;
import net.minecraft.client.player.AbstractClientPlayer;

public interface ModSupport {

    //#if MC >= 12102
    public boolean shouldBeUsed(CapeRenderInfo capeRenderInfo);
    //#else
    //$$default boolean shouldBeUsed(CapeRenderInfo capeRenderInfo) {
    //$$    return shouldBeUsed(capeRenderInfo.getPlayer());
    //$$}
    //#endif

    @Deprecated
    default boolean shouldBeUsed(AbstractClientPlayer player) {
        return false;
    }

    public CapeRenderer getRenderer();

    public boolean blockFeatureRenderer(Object feature);

}
