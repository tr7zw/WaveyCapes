package dev.tr7zw.waveycapes;

import java.util.ArrayList;
import java.util.List;

import dev.tr7zw.transition.mc.ComponentProvider;
import dev.tr7zw.trender.gui.client.AbstractConfigScreen;
import dev.tr7zw.trender.gui.client.BackgroundPainter;
import dev.tr7zw.trender.gui.widget.WButton;
import dev.tr7zw.trender.gui.widget.WGridPanel;
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
        //        return new CustomConfigScreen(parent, "text.wc.title") {
        //
        //            private int rotationX = 164;
        //            private int rotationY = 5;
        //
        //
        //            @Override
        //            public boolean keyPressed(int i, int j, int k) {
        //                if (i == 263) { // left
        //                    rotationX--;
        //                }
        //                if (i == 262) { // right
        //                    rotationX++;
        //                }
        //                if (i == 264) { // down
        //                    rotationY--;
        //                }
        //                if (i == 265) { // up
        //                    rotationY++;
        //                }
        //                return super.keyPressed(i, j, k);
        //            }
        //
        //            @Override
        //            //#if MC >= 12000
        //            public void render(GuiGraphics guiGraphics, int xMouse, int yMouse, float f) {
        //                //#else
        //                //$$public void render(PoseStack guiGraphics, int xMouse, int yMouse, float f) {
        //                //#endif
        //                super.render(guiGraphics, xMouse, yMouse, f);
        //                if (this.minecraft.level != null) {
        //                    int x = minecraft.getWindow().getGuiScaledWidth() / 2;
        //                    int y = minecraft.getWindow().getGuiScaledHeight()
        //                            - (minecraft.getWindow().getGuiScaledHeight() / 3);
        //                    int size = (int) (40f * (minecraft.getWindow().getGuiScaledHeight() / 200f));
        //                    drawEntity(x, y, size, rotationX, rotationY, this.minecraft.player, f);
        //                }
        //            }
        //
        //
        //        };

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
