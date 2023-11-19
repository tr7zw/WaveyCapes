package dev.tr7zw.waveycapes;

import dev.tr7zw.waveycapes.support.EarsSupport;
import dev.tr7zw.waveycapes.support.MinecraftCapesSupport;
import dev.tr7zw.waveycapes.support.MoreBannerFeaturesSupport;
import dev.tr7zw.waveycapes.support.SupportManager;
import net.fabricmc.api.ModInitializer;

public class WaveyCapesMod extends WaveyCapesBase implements ModInitializer {
    @Override
    public void onInitialize() {
        init();
    }

    @Override
    public void initSupportHooks() {
        if (doesClassExist("de.kxmischesdomi.morebannerfeatures.MoreBannerFeatures")) {
            SupportManager.mods.add(new MoreBannerFeaturesSupport());
            LOGGER.info("Wavey Capes loaded MoreBannerFeatures support!");
        }

        if (doesClassExist("net.minecraftcapes.MinecraftCapes")) {
            SupportManager.mods.add(new MinecraftCapesSupport());
            LOGGER.info("Wavey Capes loaded MinecraftCapes support!");
        }

        if (doesClassExist("com.unascribed.ears.common.EarsVersion")) {
            SupportManager.mods.add(new EarsSupport());
            LOGGER.info("Wavey Capes loaded Ears support!");
        }
    }
}
