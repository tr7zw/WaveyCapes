package dev.tr7zw.waveycapes;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.tr7zw.transition.mc.EntityUtil;
import dev.tr7zw.transition.mc.MathUtil;
import dev.tr7zw.waveycapes.delegate.PlayerDelegate;
import dev.tr7zw.waveycapes.support.AnimationSupport;
import dev.tr7zw.waveycapes.support.PlayerAnimatorSupport;
import dev.tr7zw.waveycapes.support.ShoulderSurfingSupport;
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

    // Modified version from InventoryScreen
    void drawEntity(int x, int y, int size, float lookX, float lookY, LivingEntity livingEntity, float delta) {
        float rotationModifyer = 3;
        //        PoseStack poseStack = NMSUtil.getPoseStack();
        //        poseStack.pushPose();
        //        poseStack.translate(x, y, 1050.0D);
        //        poseStack.scale(1.0F, 1.0F, -1.0F);
        NMSUtil.prepareViewMatrix(x, y);
        PoseStack matrixStack = new PoseStack();
        matrixStack.translate(x, y, 1000.0D);
        matrixStack.scale((float) size, (float) size, (float) size);
        matrixStack.scale(1.0F, 1.0F, -1.0F);
        var quaternion = MathUtil.ZP.rotationDegrees(180.0F);
        var quaternion2 = MathUtil.XP.rotationDegrees(lookY * rotationModifyer);
        quaternion.mul(quaternion2);
        matrixStack.mulPose(quaternion);
        matrixStack.translate(0.0D, -1, 0D);
        float yBodyRot = livingEntity.yBodyRot;
        float yRot = EntityUtil.getYRot(livingEntity);
        float yRotO = livingEntity.yRotO;
        float yBodyRotO = livingEntity.yBodyRotO;
        float xRot = EntityUtil.getXRot(livingEntity);
        float xRotO = livingEntity.xRotO;
        float yHeadRotO = livingEntity.yHeadRotO;
        float yHeadRot = livingEntity.yHeadRot;
        Vec3 vel = livingEntity.getDeltaMovement();
        livingEntity.yBodyRot = (180.0F + lookX * rotationModifyer);
        EntityUtil.setYRot(livingEntity, (180.0F + lookX * rotationModifyer));
        livingEntity.yBodyRotO = livingEntity.yBodyRot;
        livingEntity.yRotO = EntityUtil.getYRot(livingEntity);
        livingEntity.setDeltaMovement(Vec3.ZERO);
        EntityUtil.setXRot(livingEntity, 0);
        livingEntity.xRotO = EntityUtil.getXRot(livingEntity);
        livingEntity.yHeadRot = EntityUtil.getYRot(livingEntity);
        livingEntity.yHeadRotO = EntityUtil.getYRot(livingEntity);
        NMSUtil.prepareLighting();
        EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        MathUtil.conjugate(quaternion2);
        entityRenderDispatcher.overrideCameraOrientation(quaternion2);
        entityRenderDispatcher.setRenderShadow(false);
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        // Mc renders the player in the inventory without delta, causing it to look
        // "laggy". Good luck unseeing this :)
        //#if MC >= 12102
        entityRenderDispatcher.render(livingEntity, 0.0D, 0.0D, 0.0D, 0.0F, matrixStack, bufferSource, 15728880);
        //#else
        //$$entityRenderDispatcher.render(livingEntity, 0.0D, 0.0D, 0.0D, 0.0F, delta, matrixStack, bufferSource, 15728880);
        //#endif
        bufferSource.endBatch();
        entityRenderDispatcher.setRenderShadow(true);
        livingEntity.yBodyRot = yBodyRot;
        livingEntity.yBodyRotO = yBodyRotO;
        EntityUtil.setYRot(livingEntity, yRot);
        livingEntity.yRotO = yRotO;
        EntityUtil.setXRot(livingEntity, xRot);
        livingEntity.xRotO = xRotO;
        livingEntity.yHeadRotO = yHeadRotO;
        livingEntity.yHeadRot = yHeadRot;
        livingEntity.setDeltaMovement(vel);
        //        poseStack.popPose();
        NMSUtil.resetViewMatrix();
        Lighting.setupFor3DItems();
    }

    @Override
    public void initSupportHooks() {
        //#if MC >= 18000
        if (doesClassExist("dev.kosmx.playerAnim.core.impl.AnimationProcessor")) {
            SupportManager.animationSupport.add(new PlayerAnimatorSupport());
            LOGGER.info("Wavey Capes loaded PlayerAnimator support!");
        }
        //#endif
        if (doesClassExist("com.github.exopandora.shouldersurfing.api.client.ICameraEntityRenderer")) {
            ShoulderSurfingSupport.init();
            LOGGER.info("Wavey Capes loaded Shoulder Surfing support!");
        }
    }

}
