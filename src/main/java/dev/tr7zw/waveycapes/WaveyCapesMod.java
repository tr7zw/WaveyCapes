package dev.tr7zw.waveycapes;

import dev.tr7zw.transition.loader.ModLoaderUtil;
import dev.tr7zw.waveycapes.support.*;
import dev.tr7zw.waveycapes.support.laby.LabyCloak;
//? if fabric {

import net.fabricmc.api.ClientModInitializer;

public class WaveyCapesMod extends WaveyCapesBase implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        init();
    }
    //? } else {
    /*
    public class WaveyCapesMod extends WaveyCapesBase {
    *///? }

    @Override
    public void initSupportHooks() {
        super.initSupportHooks();

        if (doesClassExist("net.minecraftcapes.MinecraftCapes")) {
            SupportManager.mods.add(new MinecraftCapesSupport());
            LOGGER.info("Wavey Capes loaded MinecraftCapes support!");
        }

        if (doesClassExist("net.labymod.api.user.shop.cloak.CloakCosmeticService")) {
            LabyCloak.init();
        }

        if (doesClassExist("com.unascribed.ears.common.EarsVersion")) {
            SupportManager.mods.add(new EarsSupport());
            LOGGER.info("Wavey Capes loaded Ears support!");
        }

        //? if (fabric || neoforge) && 1.21.11 {
        /*
        if (doesClassExist("com.wynntils.services.cosmetics.type.WynntilsCapeLayer")) {
            SupportManager.mods.add(new WynntilsSupport());
            LOGGER.info("Wavey Capes loaded Wynntils support!");
        }
        *///? }
    }

    @Override
    public void init() {
        super.init();
        ModLoaderUtil.disableDisplayTest();
        ModLoaderUtil.registerConfigScreen(WaveyCapesConfigScreen::createConfigScreen);
    }

}
