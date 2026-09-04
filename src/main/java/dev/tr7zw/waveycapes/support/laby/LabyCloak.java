package dev.tr7zw.waveycapes.support.laby;

import dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper;
import dev.tr7zw.waveycapes.support.SupportManager;
import dev.tr7zw.waveycapes.versionless.ModBase;
import net.labymod.api.Laby;
import net.labymod.api.client.entity.player.Player;
import net.labymod.api.user.shop.cloak.CloakCosmeticService;

public final class LabyCloak {

    private static CloakCosmeticService cloakService;

    private LabyCloak() {
    }

    public static void init() {
        LabySupport render = new LabySupport();
        SupportManager.mods.add(render);
        service().register(render);
        ModBase.LOGGER.info("Wavey Capes loaded LabyMod support!");
    }

    public static CloakCosmeticService service() {
        if (cloakService == null) {
            cloakService = Laby.references().cloakCosmeticService();
        }
        return cloakService;
    }

    public static Player player(PlayerWrapper wrapper) {
        //? if >= 1.21.9 {

        Object entity = wrapper.getAvatar();
        //? } else {

        /*Object entity = wrapper.getEntity();
        *///? }
        return entity instanceof Player player ? player : null;
    }

}
