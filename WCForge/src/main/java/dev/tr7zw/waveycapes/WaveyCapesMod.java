package dev.tr7zw.waveycapes;

import net.minecraftforge.fml.common.Mod;

@Mod(modid = "waveycapes", name = "waveycapes", version = "@VER@", clientSideOnly = true, guiFactory = "dev.tr7zw.waveycapes.config.WaveyCapesModGuiFactory")
public class WaveyCapesMod extends WaveyCapesBase {

	public WaveyCapesMod() {
        try {
            Class clientClass = net.minecraft.client.Minecraft.class;
        }catch(Throwable ex) {
            LOGGER.warn("WaveyCapes Mod installed on a Server. Going to sleep.");
            return;
        }
	    init();
	    
	}

    @Override
    public void initSupportHooks() {


    }
	
}
