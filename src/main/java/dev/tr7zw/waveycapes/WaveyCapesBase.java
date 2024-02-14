package dev.tr7zw.waveycapes;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.tr7zw.config.CustomConfigScreen;
import dev.tr7zw.util.NMSHelper;
import dev.tr7zw.waveycapes.delegate.PlayerDelegate;
import dev.tr7zw.waveycapes.support.AnimationSupport;
import dev.tr7zw.waveycapes.support.PlayerAnimatorSupport;
import dev.tr7zw.waveycapes.support.SupportManager;
import dev.tr7zw.waveycapes.versionless.CapeMovement;
import dev.tr7zw.waveycapes.versionless.CapeStyle;
import dev.tr7zw.waveycapes.versionless.ModBase;
import dev.tr7zw.waveycapes.versionless.WindMode;
import dev.tr7zw.waveycapes.versionless.config.Config;
import dev.tr7zw.waveycapes.versionless.nms.MinecraftPlayer;
import dev.tr7zw.waveycapes.versionless.util.Vector3;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

// spotless:off
//#if MC >= 11903
import org.joml.Quaternionf;
//#else
//$$import com.mojang.math.Quaternion;
//#endif
//#if MC >= 11900
import net.minecraft.client.OptionInstance;
//#else
//$$ import net.minecraft.client.Option;
//#endif
//#if MC >= 12000
import net.minecraft.client.gui.GuiGraphics;
//#else
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif
//spotless:on

public abstract class WaveyCapesBase extends ModBase {

    @Getter
    public static WaveyCapesBase INSTANCE;

    public void init() {
        INSTANCE = this;
        super.init();
        initSupportHooks();
    }

    @Override
    public Vector3 applyModAnimations(MinecraftPlayer player, Vector3 pos) {
        for (AnimationSupport sup : SupportManager.animationSupport) {
            pos = sup.applyAnimationChanges(((PlayerDelegate) player).getPlayer(), 0, pos);
        }
        return pos;
    }

    public Screen createConfigScreen(Screen parent) {
        return new CustomConfigScreen(parent, "text.wc.title") {

            private int rotationX = 164;
            private int rotationY = 5;

            @Override
            public void initialize() {
                List<Object> options = new ArrayList<>();
                options.add(getEnumOption("text.wc.setting.capestyle", CapeStyle.class, () -> config.capeStyle,
                        (v) -> config.capeStyle = v));
                options.add(getEnumOption("text.wc.setting.windmode", WindMode.class, () -> config.windMode,
                        (v) -> config.windMode = v));
                options.add(getEnumOption("text.wc.setting.capemovement", CapeMovement.class, () -> config.capeMovement,
                        (v) -> config.capeMovement = v));
                // options.add(getIntOption("text.wc.setting.capeparts", 16, 64, () ->
                // config.capeParts, (v) -> config.capeParts = v));
                options.add(getIntOption("text.wc.setting.gravity", 5, 32, () -> config.gravity,
                        (v) -> config.gravity = v));
                options.add(getIntOption("text.wc.setting.heightMultiplier", 4, 16, () -> config.heightMultiplier,
                        (v) -> config.heightMultiplier = v));
                // options.add(getIntOption("text.wc.setting.maxBend", 1, 20, () ->
                // config.maxBend, (v) -> config.maxBend = v));

                // spotless:off
                //#if MC >= 11900
                getOptions().addSmall(options.toArray(new OptionInstance[0]));
                //#else
                //$$getOptions().addSmall(options.toArray(new Option[0]));
                //#endif
                // spotless:on

            }

            @Override
            public void save() {
                writeConfig();
            }

            @Override
            public boolean keyPressed(int i, int j, int k) {
                if (i == 263) { // left
                    rotationX--;
                }
                if (i == 262) { // right
                    rotationX++;
                }
                if (i == 264) { // down
                    rotationY--;
                }
                if (i == 265) { // up
                    rotationY++;
                }
                return super.keyPressed(i, j, k);
            }

            @Override
            // spotless:off
          //#if MC >= 12000
            public void render(GuiGraphics guiGraphics, int xMouse, int yMouse, float f) {
          //#else
          //$$public void render(PoseStack guiGraphics, int xMouse, int yMouse, float f) {
          //#endif
          //spotless:on
                super.render(guiGraphics, xMouse, yMouse, f);
                if (this.minecraft.level != null) {
                    int x = minecraft.getWindow().getGuiScaledWidth() / 2;
                    int y = minecraft.getWindow().getGuiScaledHeight()
                            - (minecraft.getWindow().getGuiScaledHeight() / 3);
                    int size = (int) (40f * (minecraft.getWindow().getGuiScaledHeight() / 200f));
                    drawEntity(x, y, size, rotationX, rotationY, this.minecraft.player, f);
                }
            }

            @Override
            public void reset() {
                config = new Config();
                writeConfig();
            }

        };

    }

    // Modified version from InventoryScreen
    private void drawEntity(int x, int y, int size, float lookX, float lookY, LivingEntity livingEntity, float delta) {
        float rotationModifyer = 3;
        PoseStack poseStack = NMSUtil.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(x, y, 1050.0D);
        poseStack.scale(1.0F, 1.0F, -1.0F);
        NMSUtil.prepareViewMatrix(x, y);
        PoseStack matrixStack = new PoseStack();
        matrixStack.translate(0.0D, 1, 1000.0D);
        matrixStack.scale((float) size, (float) size, (float) size);
        // spotless:off
        //#if MC >= 11903
        Quaternionf quaternion = NMSHelper.ZP.rotationDegrees(180.0F);
        Quaternionf quaternion2 = NMSHelper.XP.rotationDegrees(lookY * rotationModifyer);
        //#else
        //$$Quaternion quaternion = NMSHelper.ZP.rotationDegrees(180.0F);
        //$$Quaternion quaternion2 = NMSHelper.XP.rotationDegrees(lookY * rotationModifyer);
        //#endif
        //spotless:on
        quaternion.mul(quaternion2);
        matrixStack.mulPose(quaternion);
        matrixStack.translate(0.0D, -1, 0D);
        float yBodyRot = livingEntity.yBodyRot;
        float yRot = NMSHelper.getYRot(livingEntity);
        float yRotO = livingEntity.yRotO;
        float yBodyRotO = livingEntity.yBodyRotO;
        float xRot = NMSHelper.getXRot(livingEntity);
        float xRotO = livingEntity.xRotO;
        float yHeadRotO = livingEntity.yHeadRotO;
        float yHeadRot = livingEntity.yHeadRot;
        Vec3 vel = livingEntity.getDeltaMovement();
        livingEntity.yBodyRot = 180.0F + (lookX * rotationModifyer);
        NMSHelper.setYRot(livingEntity, 180.0F + (lookX * rotationModifyer));
        livingEntity.yBodyRotO = livingEntity.yBodyRot;
        livingEntity.yRotO = NMSHelper.getYRot(livingEntity);
        livingEntity.setDeltaMovement(Vec3.ZERO);
        NMSHelper.setXRot(livingEntity, 0);
        livingEntity.xRotO = NMSHelper.getXRot(livingEntity);
        livingEntity.yHeadRot = NMSHelper.getYRot(livingEntity);
        livingEntity.yHeadRotO = NMSHelper.getYRot(livingEntity);
        NMSUtil.prepareLighting();
        EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        NMSUtil.conjugate(quaternion2);
        entityRenderDispatcher.overrideCameraOrientation(quaternion2);
        entityRenderDispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        // Mc renders the player in the inventory without delta, causing it to look
        // "laggy". Good luck unseeing this :)
        entityRenderDispatcher.render(livingEntity, 0.0D, 0.0D, 0.0D, 0.0F, delta, matrixStack, bufferSource, 15728880);
        bufferSource.endBatch();
        entityRenderDispatcher.setRenderShadow(true);
        livingEntity.yBodyRot = yBodyRot;
        livingEntity.yBodyRotO = yBodyRotO;
        NMSHelper.setYRot(livingEntity, yRot);
        livingEntity.yRotO = yRotO;
        NMSHelper.setXRot(livingEntity, xRot);
        livingEntity.xRotO = xRotO;
        livingEntity.yHeadRotO = yHeadRotO;
        livingEntity.yHeadRot = yHeadRot;
        livingEntity.setDeltaMovement(vel);
        poseStack.popPose();
        NMSUtil.resetViewMatrix();
        Lighting.setupFor3DItems();
    }

    @Override
    public void initSupportHooks() {
        if (doesClassExist("dev.kosmx.playerAnim.core.impl.AnimationProcessor")) {
            SupportManager.animationSupport.add(new PlayerAnimatorSupport());
            LOGGER.info("Wavey Capes loaded PlayerAnimator support!");
        }
    }

}
