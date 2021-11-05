package dev.tr7zw.waveycapes;

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
        if(doesClassExist("de.kxmischesdomi.morebannerfeatures.MoreBannerFeatures")) {
            SupportManager.mods.add(new MoreBannerFeaturesSupport());
            System.out.println("Wavey Capes loaded MoreBannerFeatures support!");
        }

        if(doesClassExist("net.minecraftcapes.MinecraftCapes")) {
            SupportManager.mods.add(new MinecraftCapesSupport());
            System.out.println("Wavey Capes loaded MinecraftCapes support!");
        }
    }
}
