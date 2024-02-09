//#if FORGE
//$$package dev.tr7zw.waveycapes;
//$$
//$$import net.minecraftforge.api.distmarker.Dist;
//$$import net.minecraftforge.fml.DistExecutor;
//$$import net.minecraftforge.fml.common.Mod;
//$$
//$$@Mod("waveycapes")
//$$public class WaveyCapesBootstrap {
//$$
//$$	public WaveyCapesBootstrap() {
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
//$$import net.neoforged.fml.DistExecutor;
//$$import net.neoforged.fml.common.Mod;
//$$
//$$@Mod("waveycapes")
//$$public class WaveyCapesBootstrap {
//$$
//$$	public WaveyCapesBootstrap() {
//$$		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> { 
//$$         new WaveyCapesMod().init();
//$$        });
//$$	}
//$$	
//$$}
//#endif