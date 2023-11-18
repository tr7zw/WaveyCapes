package dev.tr7zw.waveycapes;

//import dev.tr7zw.waveycapes.support.MinecraftCapesSupport;
import dev.tr7zw.waveycapes.support.SupportManager;
import net.neoforged.fml.IExtensionPoint;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.ConfigScreenHandler.ConfigScreenFactory;

@Mod("waveycapes")
public class WaveyCapesMod extends WaveyCapesBase {

    public WaveyCapesMod() {
        try {
            Class clientClass = net.minecraft.client.Minecraft.class;
        } catch (Throwable ex) {
            LOGGER.warn("WaveyCapes Mod installed on a Server. Going to sleep.");
            return;
        }
        init();
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenFactory.class,
                () -> new ConfigScreenFactory((mc, screen) -> {
                    return createConfigScreen(screen);
                }));
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(
                        () -> ModLoadingContext.get().getActiveContainer().getModInfo().getVersion().toString(),
                        (remote, isServer) -> true));
    }

    @Override
    public void initSupportHooks() {

        if (doesClassExist("net.minecraftcapes.MinecraftCapes")) {
//            SupportManager.mods.add(new MinecraftCapesSupport());
            LOGGER.info("Wavey Capes loaded MinecraftCapes support!");
        }
    }

}
