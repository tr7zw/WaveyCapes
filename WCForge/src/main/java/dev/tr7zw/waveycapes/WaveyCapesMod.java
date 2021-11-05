package dev.tr7zw.waveycapes;

import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

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
		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(
                        () -> ModLoadingContext.get().getActiveContainer().getModInfo().getVersion().toString(),
                        (remote, isServer) -> true));
	}

    @Override
    public void initSupportHooks() {
        // no forge mods here for now
    }
	
}
