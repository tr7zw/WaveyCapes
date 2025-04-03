package dev.tr7zw.waveycapes.support;

import dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper;
import dev.tr7zw.waveycapes.CapeRenderer;
import net.minecraft.client.player.AbstractClientPlayer;

public interface ModSupport {

    //#if MC >= 12102
    public boolean shouldBeUsed(PlayerWrapper capeRenderInfo);
    //#else
    //$$default boolean shouldBeUsed(PlayerWrapper capeRenderInfo) {
    //$$    return shouldBeUsed((AbstractClientPlayer) capeRenderInfo.getEntity());
    //$$}
    //#endif

    @Deprecated
    default boolean shouldBeUsed(AbstractClientPlayer player) {
        return false;
    }

    public CapeRenderer getRenderer();

    public boolean blockFeatureRenderer(Object feature);

}
