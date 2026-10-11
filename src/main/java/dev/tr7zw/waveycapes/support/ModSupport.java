package dev.tr7zw.waveycapes.support;

import dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper;
import dev.tr7zw.waveycapes.render.CapeRenderer;
import net.minecraft.client.player.AbstractClientPlayer;

public interface ModSupport {

    //? if >= 1.21.2 {

    public boolean shouldBeUsed(PlayerWrapper capeRenderInfo);
    //? } else {

    /*default boolean shouldBeUsed(PlayerWrapper capeRenderInfo) {
       return shouldBeUsed((AbstractClientPlayer) capeRenderInfo.getEntity());
    }
    *///? }

    @Deprecated
    default boolean shouldBeUsed(AbstractClientPlayer player) {
        return false;
    }

    public CapeRenderer getRenderer();

    // higher wins when several supports apply
    default int priority() {
        return 0;
    }

    public boolean blockFeatureRenderer(Object feature);

}
