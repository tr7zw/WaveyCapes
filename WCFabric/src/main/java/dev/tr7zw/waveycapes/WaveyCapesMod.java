package dev.tr7zw.waveycapes;

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
        try {
            Class.forName("de.kxmischesdomi.morebannerfeatures.MoreBannerFeatures");
            SupportManager.mods.add(new MoreBannerFeaturesSupport());
            LOGGER.info("Wavey Capes loaded MoreBannerFeatures support!");
        }catch(Exception ex) {
            // not loaded
        }
    }
}
