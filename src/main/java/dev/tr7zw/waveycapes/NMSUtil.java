package dev.tr7zw.waveycapes;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;

public class NMSUtil {

    public static ResourceLocation getPlayerCape(AbstractClientPlayer player) {
        // spotless:off
        //#if MC >= 12002
        return player.getSkin().capeTexture();
        //#else
        //$$ return player.getCloakTextureLocation();
        //#endif
        //spotless:on
    }

}
