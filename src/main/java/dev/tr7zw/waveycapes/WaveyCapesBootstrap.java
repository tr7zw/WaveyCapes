//#if FORGE
//$$package dev.tr7zw.waveycapes;
//$$
//$$import net.minecraftforge.api.distmarker.Dist;
//$$import net.minecraftforge.fml.DistExecutor;
//$$import net.minecraftforge.fml.common.Mod;
//$$import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
//$$import dev.tr7zw.transition.loader.ModLoaderUtil;
//$$
//$$@Mod("waveycapes")
//$$public class WaveyCapesBootstrap {
//$$
//$$	public WaveyCapesBootstrap(FMLJavaModLoadingContext context) {
//$$        ModLoaderUtil.setModLoadingContext(context);
//$$		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> { 
//$$         new WaveyCapesMod().init();
//$$        });
//$$	}
//$$	
//$$}
//#elseif NEOFORGE
//$$package dev.tr7zw.waveycapes;
//$$
//$$import net.neoforged.api.distmarker.Dist;
//$$import net.neoforged.fml.loading.FMLEnvironment;
//$$import net.neoforged.fml.common.Mod;
//$$
//$$@Mod("waveycapes")
//$$public class WaveyCapesBootstrap {
//$$
//$$    public WaveyCapesBootstrap() {
//$$            if(FMLEnvironment.dist == Dist.CLIENT) {
//$$                new WaveyCapesMod().init();
//$$            }
//$$    }
//$$
//$$}
//#endif