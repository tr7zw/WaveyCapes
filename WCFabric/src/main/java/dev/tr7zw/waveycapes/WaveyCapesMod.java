package dev.tr7zw.waveycapes;

import dev.tr7zw.waveycapes.support.MinecraftCapesSupport;
import dev.tr7zw.waveycapes.support.SupportManager;
import net.fabricmc.api.ModInitializer;

public class WaveyCapesMod extends WaveyCapesBase implements ModInitializer {
	@Override
	public void onInitialize() {
	    init();
	}

    @Override
    public void initSupportHooks() {

        if(doesClassExist("net.minecraftcapes.MinecraftCapes")) {
            SupportManager.mods.add(new MinecraftCapesSupport());
            LOGGER.info("Wavey Capes loaded MinecraftCapes support!");
        }
    }
}
