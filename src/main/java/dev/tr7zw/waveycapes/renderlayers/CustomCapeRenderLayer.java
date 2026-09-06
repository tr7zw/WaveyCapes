package dev.tr7zw.waveycapes.renderlayers;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.tr7zw.transition.mc.*;
import dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper;
import dev.tr7zw.waveycapes.WaveyCapesBase;

import dev.tr7zw.waveycapes.versionless.*;
import dev.tr7zw.waveycapes.versionless.util.*;
import net.minecraft.client.Minecraft;
//? if >= 1.21.11 {

import net.minecraft.client.model.player.*;
//? } else {

/*import net.minecraft.client.model.*;
*///? }
import net.minecraft.client.renderer.debug.*;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

//? if >= 1.21.9 {

import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.phys.*;
import org.joml.*;

import java.lang.Math;
//? } else {

/*import net.minecraft.client.renderer.MultiBufferSource;
*///? }
   //? if < 1.21.3 {

/*import net.minecraft.world.entity.player.PlayerModelPart;
*///? }

//? if < 1.21.2 {

/*import net.minecraft.util.Mth;
import net.minecraft.client.player.AbstractClientPlayer;
*///? }

//? if >= 1.21.9 {

public class CustomCapeRenderLayer
        extends RenderLayer<net.minecraft.client.renderer.entity.state.AvatarRenderState, PlayerModel> {
    //? } else if >= 1.21.2 {
    /*
     public class CustomCapeRenderLayer extends RenderLayer<net.minecraft.client.renderer.entity.state.AvatarRenderState, PlayerModel> {
    *///? } else {

    /*public class CustomCapeRenderLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    *///? }

    //? if >= 1.21.9 {

    public CustomCapeRenderLayer(
            RenderLayerParent<net.minecraft.client.renderer.entity.state.AvatarRenderState, PlayerModel> renderLayerParent) {
        super(renderLayerParent);
    }
    //? } else if >= 1.21.2 {
    /*
     public CustomCapeRenderLayer(RenderLayerParent<net.minecraft.client.renderer.entity.state.AvatarRenderState, PlayerModel> renderLayerParent) {
        super(renderLayerParent);
     }
    *///? } else {

    /*public CustomCapeRenderLayer(
           RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderLayerParent) {
       super(renderLayerParent);
    }
    *///? }

    //? if >= 1.21.9 {

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight,
            AvatarRenderState renderState, float f, float g) {
        PlayerWrapper capeRenderInfo = new PlayerWrapper(renderState);
        float delta = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
        //? } else if >= 1.21.2 {
        /*
         public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight,
                net.minecraft.client.renderer.entity.state.AvatarRenderState renderState, float yRot, float xRot) {
            PlayerWrapper capeRenderInfo = new PlayerWrapper(renderState);
                 float delta = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
        *///? } else {

        /*public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight,
               AbstractClientPlayer renderState, float f, float g, float delta, float j, float k, float l) {
           PlayerWrapper capeRenderInfo = new PlayerWrapper(renderState);
        *///? }
        if (capeRenderInfo.isPlayerInvisible())
            return;
        if (capeRenderInfo.hasElytraEquipped())
            return;

        if (!capeRenderInfo.isCapeVisible()) {
            return;
        }

        poseStack.pushPose();
        //? if >= 1.21.2 {

        getParentModel().root().translateAndRotate(poseStack);
        //? }
        getParentModel().body.translateAndRotate(poseStack);

        if (capeRenderInfo.hasChestplateEquipped()) {
            poseStack.translate(0.0F, -0.053125F, 0.06875F);
        }

        if (ModBase.config.computeGravityVector) {
            //? if >= 1.21.9 {

            if (capeRenderInfo.getAvatar() instanceof CapeHolder capeHolder && capeHolder.canUpdateGravityVector()) {
                //? } else {
                /*        if (capeRenderInfo.getEntity() instanceof CapeHolder capeHolder) {
                 *///? }
                var simulation = capeHolder.getSimulation();
                if (simulation != null) {
                    var bodyPos = MathUtil.getWorldSpacePosition(poseStack.last().pose());

                    poseStack.pushPose();
                    poseStack.translate(0.0F, 1.0F, 0);
                    var upPos = MathUtil.getWorldSpacePosition(poseStack.last().pose());
                    poseStack.popPose();

                    //net.minecraft.gizmos.Gizmos.line(bodyPos.add(1, 1, 1), upPos.add(1, 1, 1), 0xFF00FF00, 10.0F);
                    var orientation = upPos.subtract(bodyPos).normalize();
                    // remove the players yaw from the orientation vector, so the cape is not affected by the players yaw
                    //? if >= 1.21.9 {

                    float bodyYRot = capeRenderInfo.getAvatar().yBodyRot - 90;
                    //? } else {
                    /*float bodyYRot = capeRenderInfo.getEntity().yBodyRot - 90;
                     *///? }
                    var relativeOrientation = new Vector3(
                            (float) (orientation.x() * Mth.cos(-bodyYRot * MathUtil.DEG_TO_RAD)
                                    - orientation.z() * Mth.sin(-bodyYRot * MathUtil.DEG_TO_RAD)),
                            (float) orientation.y(), (float) (orientation.x() * Mth.sin(-bodyYRot * MathUtil.DEG_TO_RAD)
                                    + orientation.z() * Mth.cos(-bodyYRot * MathUtil.DEG_TO_RAD)));
                    /*
                    net.minecraft.gizmos.Gizmos.line(bodyPos.add(-1, 1, -1),
                        bodyPos.add(-1 + relativeOrientation.x, 1 + relativeOrientation.y, -1 + relativeOrientation.z),
                        0xFFFFFF00, 10.0F);
                     */
                    simulation.setGravityDirection(relativeOrientation);
                    capeHolder.setGravityVectorRequest(false);
                }
            }
        }

        //? if >= 1.21.9 {

        WaveyCapesBase.INSTANCE.getCapeNodeCollector().submitCape(submitNodeCollector, capeRenderInfo, poseStack,
                packedLight, delta);
        //? } else {

        /*WaveyCapesBase.INSTANCE.getCapeNodeCollector().submitCape(multiBufferSource, capeRenderInfo, poseStack, packedLight, delta);
        *///? }

        poseStack.popPose();
    }

}
