package dev.tr7zw.waveycapes;

import dev.tr7zw.util.ModLoaderUtil;
import dev.tr7zw.waveycapes.support.EarsSupport;
import dev.tr7zw.waveycapes.support.MinecraftCapesSupport;
import dev.tr7zw.waveycapes.support.SupportManager;
//spotless:off
//#if FABRIC
import net.fabricmc.api.ClientModInitializer;
import dev.tr7zw.waveycapes.support.MoreBannerFeaturesSupport;

public class WaveyCapesMod extends WaveyCapesBase implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        init();
    }
//#else
//$$public class WaveyCapesMod extends WaveyCapesBase{  
//#endif
//spotless:on

    @Override
    public void initSupportHooks() {
        super.initSupportHooks();
        // spotless:off
      //#if FABRIC
        if (doesClassExist("de.kxmischesdomi.morebannerfeatures.MoreBannerFeatures")) {
            SupportManager.mods.add(new MoreBannerFeaturesSupport());
            LOGGER.info("Wavey Capes loaded MoreBannerFeatures support!");
        }
      //#endif
      //spotless:on

        if (doesClassExist("net.minecraftcapes.MinecraftCapes")) {
            SupportManager.mods.add(new MinecraftCapesSupport());
            LOGGER.info("Wavey Capes loaded MinecraftCapes support!");
        }

        if (doesClassExist("com.unascribed.ears.common.EarsVersion")) {
            SupportManager.mods.add(new EarsSupport());
            LOGGER.info("Wavey Capes loaded Ears support!");
        }
    }

    @Override
    public void init() {
        super.init();
        ModLoaderUtil.disableDisplayTest();
        ModLoaderUtil.registerConfigScreen(WaveyCapesBase.INSTANCE::createConfigScreen);
    }

}
