package dev.tr7zw.waveycapes;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;

//spotless:off
//#if MC >= 11903
import org.joml.Quaternionf;
//#else
//$$import com.mojang.math.Quaternion;
//#endif
//spotless:on

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

    // spotless:off
    //#if MC >= 11903
    public static void conjugate(Quaternionf quaternion2) {
    quaternion2.conjugate();
    //#else
    //$$public static void conjugate(Quaternion quaternion2) {
    //$$ quaternion2.conj();
    //#endif
    // spotless:on
    }

}
