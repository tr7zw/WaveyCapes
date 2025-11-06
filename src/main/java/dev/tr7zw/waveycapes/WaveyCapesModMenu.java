//? if fabric {

package dev.tr7zw.waveycapes;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class WaveyCapesModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            return WaveyCapesConfigScreen.createConfigScreen(parent);
        };
    }

}
//? }
