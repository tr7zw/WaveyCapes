//package dev.tr7zw.waveycapes.config;
//
//import java.util.concurrent.atomic.AtomicReference;
//import java.util.function.Consumer;
//import java.util.function.Supplier;
//
//import com.mojang.blaze3d.vertex.PoseStack;
//
//import net.minecraft.client.BooleanOption;
//import net.minecraft.client.CycleOption;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.ProgressOption;
//import net.minecraft.client.gui.components.AbstractWidget;
//import net.minecraft.client.gui.components.Button;
//import net.minecraft.client.gui.components.Button.OnPress;
//import net.minecraft.client.gui.components.OptionsList;
//import net.minecraft.client.gui.components.SliderButton;
//import net.minecraft.client.gui.screens.Screen;
//import net.minecraft.network.chat.CommonComponents;
//import net.minecraft.network.chat.TextComponent;
//import net.minecraft.network.chat.TranslatableComponent;
//
//public abstract class CustomConfigScreen extends Screen {
//
//    protected final Screen lastScreen;
//    private OptionsList list;
//
//    public CustomConfigScreen(Screen lastScreen, String title) {
//        super(new TranslatableComponent(title));
//        this.lastScreen = lastScreen;
//    }
//
//    @Override
//    public void removed() {
//        save();
//    }
//
//    @Override
//    public void onClose() {
//        this.minecraft.setScreen(this.lastScreen);
//    }
//
//    public OptionsList getOptions() {
//        return list;
//    }
//
//    protected void init() {
//        this.list = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
//        this.addWidget(this.list);
//        this.createFooter();
//        initialize();
//    }
//
//    public abstract void initialize();
//
//    public abstract void save();
//
//    protected void createFooter() {
//        this.addButton(
//                new Button(this.width / 2 - 100, this.height - 27, 200, 20, CommonComponents.GUI_DONE, new OnPress() {
//
//                    @Override
//                    public void onPress(Button button) {
//                        CustomConfigScreen.this.onClose();
//                    }
//                }));
//    }
//
//    public void render(PoseStack poseStack, int i, int j, float f) {
//        this.renderBackground(poseStack);
//        this.list.render(poseStack, i, j, f);
//        drawCenteredString(poseStack, this.font, this.title, this.width / 2, 20, 16777215);
//        super.render(poseStack, i, j, f);
//    }
//
//    private void updateText(ProgressOption option) {
//        AbstractWidget widget = getOptions().findOption(option);
//        if (widget instanceof SliderButton) {
//            ((SliderButton) widget).setMessage(option.getMessage(Minecraft.getInstance().options));
//        }
//    }
//
//    public BooleanOption getBooleanOption(String translationKey, Supplier<Boolean> current, Consumer<Boolean> update) {
//        return new BooleanOption(translationKey, title, (options) -> current.get(), (options, b) -> update.accept(b));
//    }
//
//    public BooleanOption getOnOffOption(String translationKey, Supplier<Boolean> current, Consumer<Boolean> update) {
//        return getBooleanOption(translationKey, current, update);
//    }
//
//    public ProgressOption getDoubleOption(String translationKey, float min, float max, float steps,
//            Supplier<Double> current, Consumer<Double> update) {
//        TranslatableComponent comp = new TranslatableComponent(translationKey);
//        return new ProgressOption(translationKey, min, max, steps, (options) -> current.get(),
//                (options, val) -> update.accept(val),
//                (options, opt) -> comp.append(new TextComponent(": " + opt.get(options))));
//    }
//
//    public ProgressOption getIntOption(String translationKey, float min, float max, Supplier<Integer> current,
//            Consumer<Integer> update) {
//        TranslatableComponent comp = new TranslatableComponent(translationKey);
//        AtomicReference<ProgressOption> option = new AtomicReference<>();
//        option.set(
//                new ProgressOption(translationKey, min, max, 1, (options) -> (double) current.get(), (options, val) -> {
//                    update.accept(val.intValue());
//                    updateText(option.get());
//                }, (options, opt) -> comp.copy().append(": " + current.get())));
//        return option.get();
//    }
//
//    public <T extends Enum> CycleOption getEnumOption(String translationKey, Class<T> targetEnum, Supplier<T> current,
//            Consumer<T> update) {
//        return new CycleOption(translationKey, (options,
//                integer) -> update.accept(targetEnum.getEnumConstants()[(current.get().ordinal() + integer.intValue())
//                        % targetEnum.getEnumConstants().length]),
//                (options, cycleOption) -> new TranslatableComponent(translationKey + "." + current.get().name()));
//    }
//
//}