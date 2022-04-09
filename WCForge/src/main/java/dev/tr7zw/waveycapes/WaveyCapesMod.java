package dev.tr7zw.waveycapes;

import dev.tr7zw.waveycapes.support.MinecraftCapesSupport;
import dev.tr7zw.waveycapes.support.SupportManager;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fmlclient.ConfigGuiHandler.ConfigGuiFactory;

@Mod("waveycapes")
public class WaveyCapesMod extends WaveyCapesBase {

	public WaveyCapesMod() {
        try {
            Class clientClass = net.minecraft.client.Minecraft.class;
        }catch(Throwable ex) {
            LOGGER.warn("WaveyCapes Mod installed on a Server. Going to sleep.");
            return;
        }
	    init();
	    ModLoadingContext.get().registerExtensionPoint(ConfigGuiFactory.class, () -> new ConfigGuiFactory((mc, screen) -> {
            return createConfigScreen(screen);
        }));
		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(
                        () -> ModLoadingContext.get().getActiveContainer().getModInfo().getVersion().toString(),
                        (remote, isServer) -> true));
	}

    @Override
    public void initSupportHooks() {

        if(doesClassExist("net.minecraftcapes.MinecraftCapes")) {
            SupportManager.mods.add(new MinecraftCapesSupport());
            LOGGER.info("Wavey Capes loaded MinecraftCapes support!");
        }
    }
	
}
