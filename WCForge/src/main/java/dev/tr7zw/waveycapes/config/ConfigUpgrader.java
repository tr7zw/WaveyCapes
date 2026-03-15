package dev.tr7zw.waveycapes.config;

import dev.tr7zw.waveycapes.CapeMovement;
import dev.tr7zw.waveycapes.CapeStyle;

public class ConfigUpgrader {

    public static boolean upgradeConfig(Config config) {
        boolean changed = false;

        // check for more changes here
        if(config.configVersion == 1) {
            config.configVersion = 2;
            if(config.gravity < 0)
                config.gravity *= -1;//fixed gravity
        }
        if(config.configVersion == 2) {
            config.configVersion = 3;
            config.capeStyle = CapeStyle.SMOOTH;
        }
        if(config.configVersion == 3) {
            config.configVersion = 4;
            config.capeMovement = CapeMovement.BASIC_SIMULATION_3D;
            changed = true;
        }

        return changed;
    }

}
