package dev.tr7zw.waveycapes;

import java.util.ArrayList;
import java.util.List;

import dev.tr7zw.transition.mc.ComponentProvider;
import dev.tr7zw.trender.gui.client.AbstractConfigScreen;
import dev.tr7zw.trender.gui.client.BackgroundPainter;
import dev.tr7zw.trender.gui.widget.WButton;
import dev.tr7zw.trender.gui.widget.WGridPanel;
import dev.tr7zw.trender.gui.widget.WPlayerPreview;
import dev.tr7zw.trender.gui.widget.data.Insets;
import dev.tr7zw.waveycapes.versionless.CapeMovement;
import dev.tr7zw.waveycapes.versionless.CapeStyle;
import dev.tr7zw.waveycapes.versionless.ModBase;
import dev.tr7zw.waveycapes.versionless.WindMode;
import dev.tr7zw.waveycapes.versionless.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;

public class WaveyCapesConfigScreen {

    public static Screen createConfigScreen(Screen parent) {
        return new CustomConfigScreen(parent).createScreen();
    }
    
    private static class CustomConfigScreen extends AbstractConfigScreen {

        public CustomConfigScreen(Screen previous) {
            super(ComponentProvider.translatable("text.wc.title"), previous);

            WGridPanel root = new WGridPanel(8);
            root.setInsets(Insets.ROOT_PANEL);
            setRootPanel(root);

            // options page
            List<OptionInstance> options = new ArrayList<>();
            options.add(getEnumOption("text.wc.setting.capestyle", CapeStyle.class, () -> ModBase.config.capeStyle,
                    (v) -> ModBase.config.capeStyle = v));
            options.add(getEnumOption("text.wc.setting.windmode", WindMode.class, () -> ModBase.config.windMode,
                    (v) -> ModBase.config.windMode = v));
            options.add(getEnumOption("text.wc.setting.capemovement", CapeMovement.class,
                    () -> ModBase.config.capeMovement, (v) -> ModBase.config.capeMovement = v));
            // options.add(getIntOption("text.wc.setting.capeparts", 16, 64, () ->
            // config.capeParts, (v) -> config.capeParts = v));
            options.add(getIntOption("text.wc.setting.gravity", 5, 32, () -> ModBase.config.gravity,
                    (v) -> ModBase.config.gravity = v));
            options.add(getIntOption("text.wc.setting.heightMultiplier", 4, 16, () -> ModBase.config.heightMultiplier,
                    (v) -> ModBase.config.heightMultiplier = v));
            // options.add(getIntOption("text.wc.setting.maxBend", 1, 20, () ->
            // config.maxBend, (v) -> config.maxBend = v));

            var optionList = createOptionList(options);
            optionList.setGap(-1);
            optionList.setSize(14 * 20, 9 * 20);

            root.add(optionList, 0, 1, 29, 25);

            WButton doneButton = new WButton(CommonComponents.GUI_DONE);
            doneButton.setOnClick(() -> {
                save();
                Minecraft.getInstance().setScreen(previous);
            });
            root.add(doneButton, 0, 26, 6, 2);
            
            var playerPreview = new WPlayerPreview();
            playerPreview.setRotationX(164);
            playerPreview.setRotationY(5);
            playerPreview.setShowBackground(true);
            root.add(playerPreview, 10, 14);

            WButton resetButton = new WButton(ComponentProvider.translatable("controls.reset"));
            resetButton.setOnClick(() -> {
                reset();
                root.layout();
            });
            root.add(resetButton, 23, 26, 6, 2);

            root.setBackgroundPainter(BackgroundPainter.VANILLA);

            root.validate(this);
            root.setHost(this);
        }

        @Override
        public void reset() {
            ModBase.config = new Config();
        }

        @Override
        public void save() {
            WaveyCapesBase.INSTANCE.writeConfig();
        }
        
        

    }

}
