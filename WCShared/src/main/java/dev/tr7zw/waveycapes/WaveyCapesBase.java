package dev.tr7zw.waveycapes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class WaveyCapesBase {

    public static WaveyCapesBase INSTANCE;
    public static final Logger LOGGER = LogManager.getLogger("WaveyCapes");
    
    public void init() {
        INSTANCE = this;
        initSupportHooks();
    }
    
    public abstract void initSupportHooks();
    
}
