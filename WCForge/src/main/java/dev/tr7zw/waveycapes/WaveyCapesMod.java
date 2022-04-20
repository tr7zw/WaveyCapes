package dev.tr7zw.waveycapes;

import net.minecraftforge.fml.common.Mod;

@Mod(modid = "waveycapes", name = "waveycapes", version = "@VER@", clientSideOnly = true)
public class WaveyCapesMod extends WaveyCapesBase {

	public WaveyCapesMod() {
        try {
            Class clientClass = net.minecraft.client.Minecraft.class;
        }catch(Throwable ex) {
            LOGGER.warn("WaveyCapes Mod installed on a Server. Going to sleep.");
            return;
        }
	    init();
//        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST,
//                () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (remote, isServer) -> true));
//        ModLoadingContext.get().registerExtensionPoint(
//                ExtensionPoint.CONFIGGUIFACTORY,
//                () -> new BiFunction<Minecraft, Screen, Screen>() {
//                    @Override
//                    public Screen apply(Minecraft t, Screen screen) {
//                        return createConfigScreen(screen);
//                    }
//                }
//        );
	}

    @Override
    public void initSupportHooks() {


    }
	
}
