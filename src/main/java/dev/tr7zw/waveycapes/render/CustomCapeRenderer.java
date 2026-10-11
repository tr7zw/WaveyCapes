package dev.tr7zw.waveycapes.render;

//? if >= 1.19.3 {

import org.joml.Matrix4f;
import org.joml.Vector4f;
//? } else {
/*
 import com.mojang.math.Matrix4f;
 import com.mojang.math.Vector4f;
*///? }
   //? if < 1.21.2 {

/*import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
*///? }
   //? if < 1.21.5 {

/*import com.mojang.blaze3d.systems.RenderSystem;
*///? }

import dev.tr7zw.waveycapes.*;
import dev.tr7zw.waveycapes.support.*;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.tr7zw.transition.mc.MathUtil;
import dev.tr7zw.transition.mc.VertexConsumerUtil;
import dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper;
import dev.tr7zw.waveycapes.versionless.*;
import dev.tr7zw.waveycapes.versionless.sim.BasicSimulation;
import dev.tr7zw.waveycapes.versionless.util.Vector3;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class CustomCapeRenderer {

    private static final int PART_COUNT = 16;
    private final ModelPart[] customCape = NMSUtil.buildCape(64, 64, x -> 0, y -> y);

    private static final float CAPE_WIDTH = 10F / 16F;
    private static final float CAPE_HEIGHT = 1F;
    private static final float CAPE_DEPTH = 1F / 16F;

    private final Matrix4f[] positionMatrices = new Matrix4f[PART_COUNT];
    private final Vector3[] frontNormalVecs = new Vector3[PART_COUNT];
    private final Vector3[] backNormalVecs = new Vector3[PART_COUNT];

    public CustomCapeRenderer() {
        for (int part = 0; part < PART_COUNT; part++) {
            positionMatrices[part] = new Matrix4f();
            frontNormalVecs[part] = new Vector3();
            backNormalVecs[part] = new Vector3();
        }
    }

    public void render(PlayerWrapper capeRenderInfo, CapeRenderer renderer, VertexConsumer vertexConsumer,
            PoseStack poseStack, int packedLight, float delta) {

        if (ModBase.config.capeStyle == CapeStyle.SMOOTH && renderer.vanillaUvValues()) {
            renderSmoothCape(poseStack, vertexConsumer, capeRenderInfo, renderer, delta, packedLight);
        } else {
            ModelPart[] parts = customCape;
            for (int part = 0; part < PART_COUNT; part++) {
                ModelPart model = parts[part];
                modifyPoseStack(poseStack, capeRenderInfo, delta, part);
                renderer.render(capeRenderInfo, part, model, poseStack, vertexConsumer, packedLight,
                        OverlayTexture.NO_OVERLAY);
                poseStack.popPose();
            }
        }
    }

    private void renderSmoothCape(PoseStack poseStack, VertexConsumer bufferBuilder, PlayerWrapper capeRenderInfo,
            CapeRenderer renderer, float delta, int light) {
        // uv layout is vanilla, but the sheet isn't always 64x32, so count in texels of the real size
        float texelU = 1F / renderer.uvSheetWidth();
        float texelV = 1F / renderer.uvSheetHeight();
        //? if < 1.21.5 {

        /*RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        *///? }

        float alpha = SupportManager.getAlphaSupplier().get();

        /*float capeWidth = 10F / 16F;
        float capeHeight = 1.0F;
        float capeDepth = 1F / 16F;*/

        for (int part = 0; part < PART_COUNT; part++) {
            modifyPoseStack(poseStack, capeRenderInfo, delta, part);
            //? if >= 1.19.3 {

            positionMatrices[part].set(poseStack.last().pose());
            //? } else {
            /*
             positionMatrices[part] = new Matrix4f(poseStack.last().pose());
            *///? }
            frontNormalVecs[part] = getNormalVec(positionMatrices[Math.max(part - 1, 0)],
                    positionMatrices[Math.max(part - 1, 0)], positionMatrices[part],
                    CAPE_WIDTH / 2F, part * (CAPE_HEIGHT / PART_COUNT), -CAPE_DEPTH,
                    -CAPE_WIDTH / 2F, part * (CAPE_HEIGHT / PART_COUNT), -CAPE_DEPTH,
                    CAPE_WIDTH / 2F, (part + 1) * (CAPE_HEIGHT / PART_COUNT), -CAPE_DEPTH,
                    light == 15728880, frontNormalVecs[part]);
            backNormalVecs[part] = getNormalVec(positionMatrices[Math.max(part - 1, 0)],
                    positionMatrices[Math.max(part - 1, 0)], positionMatrices[part],
                    CAPE_WIDTH / 2F, (part + 1) * (CAPE_HEIGHT / PART_COUNT), 0,
                    -CAPE_WIDTH / 2F, (part + 1) * (CAPE_HEIGHT / PART_COUNT), 0,
                    CAPE_WIDTH / 2F, part * (CAPE_HEIGHT / PART_COUNT), 0, light == 15728880, backNormalVecs[part]);

            poseStack.popPose();
        }

        for (int part = 0; part < PART_COUNT; part++) {
            if (part == 0) {
                float minU = 1 * texelU;
                float maxU = 11 * texelU;

                float minV = 0;
                float maxV = texelV;

                Vector3 normalVec = getNormalVec(positionMatrices[0], positionMatrices[0], positionMatrices[0],
                        CAPE_WIDTH / 2, 0, 0, -CAPE_WIDTH / 2, 0, 0,
                        CAPE_WIDTH / 2, 0, CAPE_DEPTH, light == 15728880, NORMAL_FACE);

                VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[0], CAPE_WIDTH / 2, 0, 0, maxU, maxV,
                        OverlayTexture.NO_OVERLAY, light, normalVec.x, normalVec.y, normalVec.z, alpha);
                VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[0], -CAPE_WIDTH / 2, 0, 0, minU, maxV,
                        OverlayTexture.NO_OVERLAY, light, normalVec.x, normalVec.y, normalVec.z, alpha);
                VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[0], -CAPE_WIDTH / 2, 0, -CAPE_DEPTH, minU,
                        minV, OverlayTexture.NO_OVERLAY, light, normalVec.x, normalVec.y, normalVec.z, alpha);
                VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[0], CAPE_WIDTH / 2, 0, -CAPE_DEPTH, maxU,
                        minV, OverlayTexture.NO_OVERLAY, light, normalVec.x, normalVec.y, normalVec.z, alpha);
            }

            if (part == PART_COUNT - 1) {
                float minU = 11 * texelU;
                float maxU = 21 * texelU;

                float minV = 0;
                float maxV = texelV;

                Vector3 normalVec = getNormalVec(positionMatrices[part], positionMatrices[part], positionMatrices[part],
                        CAPE_WIDTH / 2F, CAPE_HEIGHT, -CAPE_DEPTH,
                        -CAPE_WIDTH / 2F, CAPE_HEIGHT, -CAPE_DEPTH,
                        CAPE_WIDTH / 2F, CAPE_HEIGHT, 0, light == 15728880, NORMAL_FACE);

                VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[part], CAPE_WIDTH / 2F, CAPE_HEIGHT,
                        -CAPE_DEPTH, maxU, minV, OverlayTexture.NO_OVERLAY, light, normalVec.x, normalVec.y,
                        normalVec.z, alpha);
                VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[part], -CAPE_WIDTH / 2F, CAPE_HEIGHT,
                        -CAPE_DEPTH, minU, minV, OverlayTexture.NO_OVERLAY, light, normalVec.x, normalVec.y,
                        normalVec.z, alpha);
                VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[part], -CAPE_WIDTH / 2F, CAPE_HEIGHT, 0,
                        minU, maxV, OverlayTexture.NO_OVERLAY, light, normalVec.x, normalVec.y, normalVec.z, alpha);
                VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[part], CAPE_WIDTH / 2F, CAPE_HEIGHT, 0,
                        maxU, maxV, OverlayTexture.NO_OVERLAY, light, normalVec.x, normalVec.y, normalVec.z, alpha);
            }

            float minU = 0;
            float maxU = 1 * texelU;

            float minV = texelV * (part + 1);
            float maxV = minV + texelV;

            Vector3 normalVec = getNormalVec(positionMatrices[part], positionMatrices[part],
                    positionMatrices[Math.max(part - 1, 0)],
                    -CAPE_WIDTH / 2F, (part + 1) * (CAPE_HEIGHT / PART_COUNT), 0,
                    -CAPE_WIDTH / 2F, (part + 1) * (CAPE_HEIGHT / PART_COUNT), -CAPE_DEPTH,
                    -CAPE_WIDTH / 2F, part * (CAPE_HEIGHT / PART_COUNT), 0, light == 15728880, NORMAL_FACE);

            VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[part], -CAPE_WIDTH / 2F,
                    (part + 1) * (CAPE_HEIGHT / PART_COUNT), 0, minU, maxV, OverlayTexture.NO_OVERLAY, light,
                    normalVec.x, normalVec.y, normalVec.z, alpha);
            VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[part], -CAPE_WIDTH / 2F,
                    (part + 1) * (CAPE_HEIGHT / PART_COUNT), -CAPE_DEPTH, maxU, maxV, OverlayTexture.NO_OVERLAY, light,
                    normalVec.x, normalVec.y, normalVec.z, alpha);
            VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[Math.max(part - 1, 0)], -CAPE_WIDTH / 2F,
                    part * (CAPE_HEIGHT / PART_COUNT), -CAPE_DEPTH, maxU, minV, OverlayTexture.NO_OVERLAY, light,
                    normalVec.x, normalVec.y, normalVec.z, alpha);
            VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[Math.max(part - 1, 0)], -CAPE_WIDTH / 2F,
                    part * (CAPE_HEIGHT / PART_COUNT), 0, minU, minV, OverlayTexture.NO_OVERLAY, light, normalVec.x,
                    normalVec.y, normalVec.z, alpha);

            minU = 11 * texelU;
            maxU = 12 * texelU;

            normalVec = getNormalVec(positionMatrices[part], positionMatrices[part],
                    positionMatrices[Math.max(part - 1, 0)],
                    CAPE_WIDTH / 2F, (part + 1) * (CAPE_HEIGHT / PART_COUNT), -CAPE_DEPTH,
                    CAPE_WIDTH / 2F, (part + 1) * (CAPE_HEIGHT / PART_COUNT), 0,
                    CAPE_WIDTH / 2F, part * (CAPE_HEIGHT / PART_COUNT), -CAPE_DEPTH, light == 15728880, NORMAL_FACE);

            VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[part], CAPE_WIDTH / 2F,
                    (part + 1) * (CAPE_HEIGHT / PART_COUNT), -CAPE_DEPTH, minU, maxV, OverlayTexture.NO_OVERLAY, light,
                    normalVec.x, normalVec.y, normalVec.z, alpha);
            VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[part], CAPE_WIDTH / 2F,
                    (part + 1) * (CAPE_HEIGHT / PART_COUNT), 0, maxU, maxV, OverlayTexture.NO_OVERLAY, light,
                    normalVec.x, normalVec.y, normalVec.z, alpha);
            VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[Math.max(part - 1, 0)], CAPE_WIDTH / 2F,
                    part * (CAPE_HEIGHT / PART_COUNT), 0, maxU, minV, OverlayTexture.NO_OVERLAY, light, normalVec.x,
                    normalVec.y, normalVec.z, alpha);
            VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[Math.max(part - 1, 0)], CAPE_WIDTH / 2F,
                    part * (CAPE_HEIGHT / PART_COUNT), -CAPE_DEPTH, minU, minV, OverlayTexture.NO_OVERLAY, light,
                    normalVec.x, normalVec.y, normalVec.z, alpha);

            minU = 1 * texelU;
            maxU = 11 * texelU;

            Vector3 normalVecTop = average(frontNormalVecs[part], frontNormalVecs[Math.max(part - 1, 0)], NORMAL_TOP);
            Vector3 normalVecBottom = average(frontNormalVecs[part],
                    frontNormalVecs[Math.min(part + 1, PART_COUNT - 1)], NORMAL_BOTTOM);

            VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[Math.max(part - 1, 0)], CAPE_WIDTH / 2,
                    part * (CAPE_HEIGHT / PART_COUNT), -CAPE_DEPTH, maxU, minV, OverlayTexture.NO_OVERLAY, light,
                    normalVecTop.x, normalVecTop.y, normalVecTop.z, alpha);
            VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[Math.max(part - 1, 0)], -CAPE_WIDTH / 2,
                    part * (CAPE_HEIGHT / PART_COUNT), -CAPE_DEPTH, minU, minV, OverlayTexture.NO_OVERLAY, light,
                    normalVecTop.x, normalVecTop.y, normalVecTop.z, alpha);
            VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[part], -CAPE_WIDTH / 2,
                    (part + 1) * (CAPE_HEIGHT / PART_COUNT), -CAPE_DEPTH, minU, maxV, OverlayTexture.NO_OVERLAY, light,
                    normalVecBottom.x, normalVecBottom.y, normalVecBottom.z, alpha);
            VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[part], CAPE_WIDTH / 2,
                    (part + 1) * (CAPE_HEIGHT / PART_COUNT), -CAPE_DEPTH, maxU, maxV, OverlayTexture.NO_OVERLAY, light,
                    normalVecBottom.x, normalVecBottom.y, normalVecBottom.z, alpha);

            minU = 12 * texelU;
            maxU = 22 * texelU;

            normalVecTop = average(backNormalVecs[part], backNormalVecs[Math.max(part - 1, 0)], NORMAL_TOP);
            normalVecBottom = average(backNormalVecs[part], backNormalVecs[Math.min(part + 1, PART_COUNT - 1)],
                    NORMAL_BOTTOM);

            VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[Math.max(part - 1, 0)], CAPE_WIDTH / 2,
                    part * (CAPE_HEIGHT / PART_COUNT), 0, minU, minV, OverlayTexture.NO_OVERLAY, light, normalVecTop.x,
                    normalVecTop.y, normalVecTop.z, alpha);
            VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[Math.max(part - 1, 0)], -CAPE_WIDTH / 2,
                    part * (CAPE_HEIGHT / PART_COUNT), 0, maxU, minV, OverlayTexture.NO_OVERLAY, light, normalVecTop.x,
                    normalVecTop.y, normalVecTop.z, alpha);
            VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[part], -CAPE_WIDTH / 2,
                    (part + 1) * (CAPE_HEIGHT / PART_COUNT), 0, maxU, maxV, OverlayTexture.NO_OVERLAY, light,
                    normalVecBottom.x, normalVecBottom.y, normalVecBottom.z, alpha);
            VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[part], CAPE_WIDTH / 2,
                    (part + 1) * (CAPE_HEIGHT / PART_COUNT), 0, minU, maxV, OverlayTexture.NO_OVERLAY, light,
                    normalVecBottom.x, normalVecBottom.y, normalVecBottom.z, alpha);
        }
    }

    private void modifyPoseStack(PoseStack poseStack, PlayerWrapper capeRenderInfo, float h, int part) {
        if (WaveyCapesBase.config.capeMovement != CapeMovement.VANILLA) {
            modifyPoseStackSimulation(poseStack, capeRenderInfo, h, part);
            return;
        }

        //? if >= 1.21.2 {
        var renderState = capeRenderInfo.getRenderState();
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.0D, 0.125D);
        //? }

        //? if >= 1.21.9 {

        var entity = capeRenderInfo.getAvatar();
        //? } else if >= 1.21.2 {
        /*
        var entity = capeRenderInfo.getEntity();
         */
        //? }

        //? if >= 26.3 {

        poseStack.rotate(MathUtil.XP.rotationDegrees(6.0F + renderState.capeLean / 2.0F + renderState.capeFlap
                + getNatrualWindSwing(part, entity.isUnderWater())));
        poseStack.rotate(MathUtil.ZP.rotationDegrees(renderState.capeLean2 / 2.0F));
        poseStack.rotate(MathUtil.YP.rotationDegrees(180.0F - renderState.capeLean2 / 2.0F));
        //? } else if >= 1.21.2 {
        /*
        poseStack.mulPose(MathUtil.XP.rotationDegrees(6.0F + renderState.capeLean / 2.0F + renderState.capeFlap
                + getNatrualWindSwing(part, entity.isUnderWater())));
        poseStack.mulPose(MathUtil.ZP.rotationDegrees(renderState.capeLean2 / 2.0F));
        poseStack.mulPose(MathUtil.YP.rotationDegrees(180.0F - renderState.capeLean2 / 2.0F));
         */
        //? } else {

        /*modifyPoseStackVanilla(poseStack, (AbstractClientPlayer) capeRenderInfo.getEntity(), h, part);
        *///? }
    }

    private void modifyPoseStackSimulation(PoseStack poseStack, PlayerWrapper capeRenderInfo, float delta, int part) {
        //? if >= 1.21.9 {

        var entity = capeRenderInfo.getAvatar();
        //? } else {

        /*var entity = capeRenderInfo.getEntity();
        *///? }
        BasicSimulation simulation = ((CapeHolder) entity).getSimulation();
        if (simulation == null) {
            // race condition where the cape is being rendered before the simulation is initialized, just render it without movement instead of crashing
            poseStack.pushPose();
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.0D, 0.125D);

        var first = simulation.getPoints().get(0);
        var current = simulation.getPoints().get(part);
        float x = current.getLerpX(delta) - first.getLerpX(delta);
        if (x > 0) {
            x = 0;
        }
        float y = first.getLerpY(delta) - part - current.getLerpY(delta);
        float z = first.getLerpZ(delta) - current.getLerpZ(delta);

        float sidewaysRotationOffset = 0;
        float partRotation = getRotation(delta, part, simulation);

        float height = 0;
        //        if (abstractClientPlayer.isCrouching()) {
        //            height += 25.0F;
        //            poseStack.translate(0, 0.15F, 0);
        //        }

        float naturalWindSwing = getNatrualWindSwing(part, entity.isUnderWater());

        // vanilla rotating and wind
        //? if >= 26.3 {

        poseStack.rotate(MathUtil.XP.rotationDegrees(6.0F + height + naturalWindSwing));
        poseStack.rotate(MathUtil.ZP.rotationDegrees(sidewaysRotationOffset / 2.0F));
        poseStack.rotate(MathUtil.YP.rotationDegrees(180.0F - sidewaysRotationOffset / 2.0F));
        //? } else {
        /*
                poseStack.mulPose(MathUtil.XP.rotationDegrees(6.0F + height + naturalWindSwing));
        poseStack.mulPose(MathUtil.ZP.rotationDegrees(sidewaysRotationOffset / 2.0F));
        poseStack.mulPose(MathUtil.YP.rotationDegrees(180.0F - sidewaysRotationOffset / 2.0F));
         */
        //? }

        poseStack.translate(-z / PART_COUNT, y / PART_COUNT, x / PART_COUNT); // movement from the simulation
        // offsetting so the rotation is on the cape part
        // float offset = (float) (part * (16 / partCount))/16; // to fold the entire
        // cape into one position for debugging
        poseStack.translate(0, /*-offset*/ +(0.48 / 16), -(0.48 / 16)); // (0.48/16)
        poseStack.translate(0, part * 1f / PART_COUNT, part * (0) / PART_COUNT);
        //? if >= 26.3 {
        poseStack.rotate(MathUtil.XP.rotationDegrees(-partRotation)); // apply actual rotation
        //? } else {
        /*
        poseStack.mulPose(MathUtil.XP.rotationDegrees(-partRotation)); // apply actual rotation
         */
        //? }
        // undoing the rotation
        poseStack.translate(0, -part * 1f / PART_COUNT, -part * (0) / PART_COUNT);
        poseStack.translate(0, -(0.48 / 16), (0.48 / 16));

    }

    private float getRotation(float delta, int part, BasicSimulation simulation) {
        if (part == PART_COUNT - 1) {
            return getRotation(delta, part - 1, simulation);
        }
        var a = simulation.getPoints().get(part);
        var b = simulation.getPoints().get(part + 1);
        return (float) getAngle(b.getLerpX(delta) - a.getLerpX(delta), b.getLerpY(delta) - a.getLerpY(delta));
    }

    private double getAngle(float x, float y) {
        return Math.toDegrees(Math.atan2(x, y)) + 180;
    }

    //? if < 1.21.2 {

    /*private void modifyPoseStackVanilla(PoseStack poseStack, AbstractClientPlayer abstractClientPlayer, float h,
            int part) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.0D, 0.125D);
       double d = Mth.lerp(h, abstractClientPlayer.xCloakO, abstractClientPlayer.xCloak)
               - Mth.lerp(h, abstractClientPlayer.xo, abstractClientPlayer.getX());
       double e = Mth.lerp(h, abstractClientPlayer.yCloakO, abstractClientPlayer.yCloak)
                - Mth.lerp(h, abstractClientPlayer.yo, abstractClientPlayer.getY());
       double m = Mth.lerp(h, abstractClientPlayer.zCloakO, abstractClientPlayer.zCloak)
               - Mth.lerp(h, abstractClientPlayer.zo, abstractClientPlayer.getZ());
       float n = abstractClientPlayer.yBodyRotO + abstractClientPlayer.yBodyRot - abstractClientPlayer.yBodyRotO;
       double o = Mth.sin(n * 0.017453292F);
       double p = -Mth.cos(n * 0.017453292F);
       float height = (float) e * 10.0F;
        height = Mth.clamp(height, -6.0F, 32.0F);
       float swing = (float) (d * o + m * p) * easeOutSine(1.0F / PART_COUNT * part) * 100;
       swing = Mth.clamp(swing, 0.0F, 150.0F * easeOutSine(1F / PART_COUNT * part));
       float sidewaysRotationOffset = (float) (d * p - m * o) * 100.0F;
       sidewaysRotationOffset = Mth.clamp(sidewaysRotationOffset, -20.0F, 20.0F);
       float t = Mth.lerp(h, abstractClientPlayer.oBob, abstractClientPlayer.bob);
       height += Mth.sin(Mth.lerp(h, abstractClientPlayer.walkDistO, abstractClientPlayer.walkDist) * 6.0F) * 32.0F
               * t;
        //        if (abstractClientPlayer.isCrouching()) {
       //            height += 25.0F;
       //            poseStack.translate(0, 0.15F, 0);
       //        }
    
       float naturalWindSwing = getNatrualWindSwing(part, abstractClientPlayer.isUnderWater());
    
       poseStack.mulPose(MathUtil.XP.rotationDegrees(6.0F + swing / 2.0F + height + naturalWindSwing));
       poseStack.mulPose(MathUtil.ZP.rotationDegrees(sidewaysRotationOffset / 2.0F));
       poseStack.mulPose(MathUtil.YP.rotationDegrees(180.0F - sidewaysRotationOffset / 2.0F));
    }
    
    private static float easeOutSine(float x) {
    return Mth.sin((float) ((x * Math.PI) / 2f));
    }
    *///? }

    private float getNatrualWindSwing(int part, boolean underwater) {
        if (WaveyCapesBase.config.windMode != WindMode.WAVES) {
            return 0;
        }
        long highlightedPart = (System.currentTimeMillis() / (underwater ? 9 : 3)) % 360;
        float relativePart = (float) (part + 1) / PART_COUNT;
        return (float) (Math.sin(Math.toRadians((relativePart) * 360 - (highlightedPart))) * 3);
    }

    // scratch space for getNormalVec, capes are only ever built on the render thread
    private static final Vector3 NORMAL_ORIGIN = new Vector3();
    private static final Vector3 NORMAL_EDGE = new Vector3();
    private static final Vector3 NORMAL_FACE = new Vector3();
    private static final Vector3 NORMAL_TOP = new Vector3();
    private static final Vector3 NORMAL_BOTTOM = new Vector3();
    //? if >= 1.19.3 {

    private static final Vector4f TRANSFORM_SCRATCH = new Vector4f();
    //? }

    private static Vector3 getNormalVec(Matrix4f matrix1, Matrix4f matrix2, Matrix4f matrix3, float x1, float y1,
            float z1, float x2, float y2, float z2, float x3, float y3, float z3, boolean inverse, Vector3 dest) {
        Vector3 origin = transform(matrix1, x1, y1, z1, NORMAL_ORIGIN);
        Vector3 normal = transform(matrix2, x2, y2, z2, dest);
        Vector3 edge = transform(matrix3, x3, y3, z3, NORMAL_EDGE);

        normal.subtract(origin);
        edge.subtract(origin);

        normal.cross(edge);
        normal.normalize();
        return inverse ? normal.mul(-1) : normal;
    }

    private static Vector3 average(Vector3 a, Vector3 b, Vector3 dest) {
        dest.x = (a.x + b.x) / 2;
        dest.y = (a.y + b.y) / 2;
        dest.z = (a.z + b.z) / 2;
        return dest;
    }

    private static Vector3 transform(Matrix4f matrix, float x, float y, float z, Vector3 dest) {
        //? if >= 1.19.3 {

        Vector4f vector4f = matrix.transform(TRANSFORM_SCRATCH.set(x, y, z, 1));
        //? } else {
        /*
         Vector4f vector4f = new Vector4f(x, y, z, 1);
         vector4f.transform(matrix);
        *///? }
        dest.x = vector4f.x();
        dest.y = vector4f.y();
        dest.z = vector4f.z();
        return dest;
    }

}
