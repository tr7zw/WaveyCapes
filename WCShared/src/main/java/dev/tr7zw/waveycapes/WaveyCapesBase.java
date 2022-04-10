package dev.tr7zw.waveycapes;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import dev.tr7zw.waveycapes.config.Config;
import dev.tr7zw.waveycapes.config.ConfigUpgrader;
import dev.tr7zw.waveycapes.config.CustomConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Option;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public abstract class WaveyCapesBase {

    public static WaveyCapesBase INSTANCE;
    public static final Logger LOGGER = LogManager.getLogger("WaveyCapes");
    public static Config config;
    private final File settingsFile = new File("config", "waveycapes.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private boolean optifinePresent = false;
    
    public void init() {
        INSTANCE = this;
        if (settingsFile.exists()) {
            try {
                config = gson.fromJson(new String(Files.readAllBytes(settingsFile.toPath()), StandardCharsets.UTF_8),
                        Config.class);
            } catch (Exception ex) {
                System.out.println("Error while loading config! Creating a new one!");
                ex.printStackTrace();
            }
        }
        if (config == null) {
            config = new Config();
            writeConfig();
        } else {
            if(ConfigUpgrader.upgradeConfig(config)) {
                writeConfig();
            }
        }
        initSupportHooks();
        try {
            Class optifine = Class.forName("net.optifine.Config");
            optifinePresent = true;
            config.capeStyle = CapeStyle.BLOCKY;
            LOGGER.warn("Optifine detected, disabling smooth cape.");
        }catch(Throwable ex) {
            return;
        }
    }
    
    public void writeConfig() {
        if (settingsFile.exists())
            settingsFile.delete();
        try {
            Files.write(settingsFile.toPath(), gson.toJson(config).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    
    public Screen createConfigScreen(Screen parent) {
        CustomConfigScreen screen = new CustomConfigScreen(parent, "text.wc.title") {

            private int rotationX = 164;
            private int rotationY = 5;
            
            @Override
            public void initialize() {
                List<Option> options = new ArrayList<>();
                if(!optifinePresent)
                    options.add(getEnumOption("text.wc.setting.capestyle", CapeStyle.class, () -> config.capeStyle, (v) -> config.capeStyle = v));
                options.add(getEnumOption("text.wc.setting.windmode", WindMode.class, () -> config.windMode, (v) -> config.windMode = v));
                options.add(getEnumOption("text.wc.setting.capemovement", CapeMovement.class, () -> config.capeMovement, (v) -> config.capeMovement = v));
                //options.add(getIntOption("text.wc.setting.capeparts", 16, 64, () -> config.capeParts, (v) -> config.capeParts = v));
                options.add(getIntOption("text.wc.setting.gravity", 5, 32, () -> config.gravity, (v) -> config.gravity = v));
                options.add(getIntOption("text.wc.setting.heightMultiplier", 4, 16, () -> config.heightMultiplier, (v) -> config.heightMultiplier = v));
                //options.add(getIntOption("text.wc.setting.maxBend", 1, 20, () -> config.maxBend, (v) -> config.maxBend = v));

                getOptions().addSmall(options.toArray(new Option[0]));

            }

            @Override
            public void save() {
                writeConfig();
            }
            
            @Override
            public boolean keyPressed(int i, int j, int k) {
                if(i == 263) { //left
                    rotationX--;
                }
                if(i == 262) { //right
                    rotationX++;
                }
                if(i == 264) { //down
                    rotationY--;
                }
                if(i == 265) { //up
                    rotationY++;
                }
                return super.keyPressed(i, j, k);
            }

        };

        return screen;
    }
    
    public abstract void initSupportHooks();
    
    /**
     * Checks if a class exists or not
     * @param name
     * @return
     */
    protected static boolean doesClassExist(String name) {
        try {
            if(Class.forName(name) != null) {
                return true;
            }
        } catch (ClassNotFoundException e) {}
        return false;
    }
    
}
