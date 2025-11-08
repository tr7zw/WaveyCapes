package dev.tr7zw.waveycapes;

//? if >= 1.19.3 {

import org.joml.Matrix4f;
import org.joml.Vector4f;
//? } else {
/*
 import com.mojang.math.Matrix4f;
 import com.mojang.math.Vector4f;
*///? }
   //? if < 1.21.2 {
   /*
    import net.minecraft.client.player.AbstractClientPlayer;
    import net.minecraft.util.Mth;
   *///? }
   //? if < 1.21.5 {
   /*
    import com.mojang.blaze3d.systems.RenderSystem;
   *///? }

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import dev.tr7zw.transition.mc.MathUtil;
import dev.tr7zw.transition.mc.VertexConsumerUtil;
import dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper;
import dev.tr7zw.waveycapes.support.ModSupport;
import dev.tr7zw.waveycapes.support.SupportManager;
import dev.tr7zw.waveycapes.versionless.*;
import dev.tr7zw.waveycapes.versionless.sim.BasicSimulation;
import dev.tr7zw.waveycapes.versionless.util.Vector3;
import dev.tr7zw.waveycapes.versionless.util.Vector4;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class CustomCapeRenderer {

    private static final int PART_COUNT = 16;
    private final ModelPart[] customCape = NMSUtil.buildCape(64, 64, x -> 0, y -> y);

    private static final float CAPE_WIDTH = 10F / 16F;
    private static final float CAPE_HEIGHT = 1F;
    private static final float CAPE_DEPTH = 1F / 16F;

    public void render(PlayerWrapper capeRenderInfo, PoseStack poseStack, MultiBufferSource multiBufferSource,
            int packedLight, float delta) {
        CapeRenderer renderer = getCapeRenderer(capeRenderInfo);
        if (renderer == null)
            return;

        if (!prepareCape(capeRenderInfo)) {
            return;
        }

        VertexConsumer bufferBuilder = renderer.getVertexConsumer(multiBufferSource, capeRenderInfo);
        if (bufferBuilder == null) {
            return;
        }

        if (ModBase.config.capeStyle == CapeStyle.SMOOTH && renderer.vanillaUvValues()) {
            renderSmoothCape(poseStack, bufferBuilder, capeRenderInfo, delta, packedLight);
        } else {
            ModelPart[] parts = customCape;
            for (int part = 0; part < PART_COUNT; part++) {
                ModelPart model = parts[part];
                modifyPoseStack(poseStack, capeRenderInfo, delta, part);
                renderer.render(capeRenderInfo, part, model, poseStack, bufferBuilder, packedLight,
                        OverlayTexture.NO_OVERLAY);
                poseStack.popPose();
            }
        }
    }

    private boolean prepareCape(PlayerWrapper capeRenderInfo) {
        //? if >= 1.21.9 {

        CapeHolder holder = (CapeHolder) capeRenderInfo.getAvatar();
        //? } else {
        /*
         CapeHolder holder = (CapeHolder) capeRenderInfo.getEntity();
        *///? }
        if (holder == null) {
            return false;
        }
        holder.updateSimulation(PART_COUNT);
        return true;
    }

    private void renderSmoothCape(PoseStack poseStack, VertexConsumer bufferBuilder, PlayerWrapper capeRenderInfo,
            float delta, int light) {
        //? if < 1.21.5 {
        /*
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
        *///? }

        float alpha = SupportManager.getAlphaSupplier().get();

        /*float capeWidth = 10F / 16F;
        float capeHeight = 1.0F;
        float capeDepth = 1F / 16F;*/

        Matrix4f[] positionMatrices = new Matrix4f[PART_COUNT];
        Vector3[] frontNormalVecs = new Vector3[PART_COUNT];
        Vector3[] backNormalVecs = new Vector3[PART_COUNT];
        for (int part = 0; part < PART_COUNT; part++) {
            modifyPoseStack(poseStack, capeRenderInfo, delta, part);
            positionMatrices[part] = new Matrix4f(poseStack.last().pose());
            frontNormalVecs[part] = getNormalVec(
                    positionMatrices[Math.max(part - 1, 0)],
                    positionMatrices[Math.max(part - 1, 0)],
                    positionMatrices[part],
                    new Vector3(CAPE_WIDTH / 2F, part * (CAPE_HEIGHT / PART_COUNT), -CAPE_DEPTH),
                    new Vector3(-CAPE_WIDTH / 2F, part * (CAPE_HEIGHT / PART_COUNT), -CAPE_DEPTH),
                    new Vector3(CAPE_WIDTH / 2F, (part + 1) * (CAPE_HEIGHT / PART_COUNT), -CAPE_DEPTH),
                    light == 15728880
            );
            backNormalVecs[part] = getNormalVec(
                    positionMatrices[Math.max(part - 1, 0)],
                    positionMatrices[Math.max(part - 1, 0)],
                    positionMatrices[part],
                    new Vector3(CAPE_WIDTH / 2F, (part + 1) * (CAPE_HEIGHT / PART_COUNT), 0),
                    new Vector3(-CAPE_WIDTH / 2F, (part + 1) * (CAPE_HEIGHT / PART_COUNT), 0),
                    new Vector3(CAPE_WIDTH / 2F, part * (CAPE_HEIGHT / PART_COUNT), 0),
                    light == 15728880
            );

            poseStack.popPose();
        }

        for (int part = 0; part < PART_COUNT; part++) {
            if (part == 0) {
                float minU = 1 / 64F;
                float maxU = 11 / 64F;

                float minV = 0;
                float maxV = 1 / 32F;

                Vector3 normalVec = getNormalVec(positionMatrices[0], positionMatrices[0], positionMatrices[0], new Vector3(CAPE_WIDTH / 2, 0, 0), new Vector3(-CAPE_WIDTH / 2, 0, 0),
                        new Vector3(CAPE_WIDTH / 2, 0, CAPE_DEPTH), light == 15728880);

                VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[0], CAPE_WIDTH / 2, 0, 0, maxU, maxV, OverlayTexture.NO_OVERLAY, light,
                        normalVec.x, normalVec.y, normalVec.z, alpha);
                VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[0], -CAPE_WIDTH / 2, 0, 0, minU, maxV, OverlayTexture.NO_OVERLAY, light,
                        normalVec.x, normalVec.y, normalVec.z, alpha);
                VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[0], -CAPE_WIDTH / 2, 0, -CAPE_DEPTH, minU, minV, OverlayTexture.NO_OVERLAY, light,
                        normalVec.x, normalVec.y, normalVec.z, alpha);
                VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[0], CAPE_WIDTH / 2, 0, -CAPE_DEPTH, maxU, minV, OverlayTexture.NO_OVERLAY, light,
                        normalVec.x, normalVec.y, normalVec.z, alpha);
            }

            if (part == PART_COUNT - 1) {
                float minU = 11 / 64F;
                float maxU = 21 / 64F;

                float minV = 0;
                float maxV = 1 / 32F;

                Vector3 normalVec = getNormalVec(positionMatrices[part], positionMatrices[part], positionMatrices[part],
                        new Vector3(CAPE_WIDTH / 2F, CAPE_HEIGHT, -CAPE_DEPTH), new Vector3(-CAPE_WIDTH / 2F, CAPE_HEIGHT, -CAPE_DEPTH),
                        new Vector3(CAPE_WIDTH / 2F, CAPE_HEIGHT, 0), light == 15728880);

                VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[part], CAPE_WIDTH / 2F, CAPE_HEIGHT, -CAPE_DEPTH, maxU, minV, OverlayTexture.NO_OVERLAY, light,
                        normalVec.x, normalVec.y, normalVec.z, alpha);
                VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[part], -CAPE_WIDTH / 2F, CAPE_HEIGHT, -CAPE_DEPTH, minU, minV, OverlayTexture.NO_OVERLAY, light,
                        normalVec.x, normalVec.y, normalVec.z, alpha);
                VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[part], -CAPE_WIDTH / 2F, CAPE_HEIGHT, 0, minU, maxV, OverlayTexture.NO_OVERLAY, light,
                        normalVec.x, normalVec.y, normalVec.z, alpha);
                VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[part], CAPE_WIDTH / 2F, CAPE_HEIGHT, 0, maxU, maxV, OverlayTexture.NO_OVERLAY, light,
                        normalVec.x, normalVec.y, normalVec.z, alpha);
            }

            float minU = 0;
            float maxU = 1 / 64F;

            float minV = (1 / 32F) * (part + 1);
            float maxV = minV + (1 / 32F);

            Vector3 normalVec = getNormalVec(positionMatrices[part], positionMatrices[part], positionMatrices[Math.max(part - 1, 0)],
                    new Vector3(-CAPE_WIDTH / 2F, (part + 1) * (CAPE_HEIGHT / PART_COUNT), 0),
                    new Vector3(-CAPE_WIDTH / 2F, (part + 1) * (CAPE_HEIGHT / PART_COUNT), -CAPE_DEPTH),
                    new Vector3(-CAPE_WIDTH / 2F, part * (CAPE_HEIGHT / PART_COUNT), 0), light == 15728880);

            VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[part], -CAPE_WIDTH / 2F, (part + 1) * (CAPE_HEIGHT / PART_COUNT), 0, minU, maxV, OverlayTexture.NO_OVERLAY, light,
                    normalVec.x, normalVec.y, normalVec.z, alpha);
            VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[part], -CAPE_WIDTH / 2F, (part + 1) * (CAPE_HEIGHT / PART_COUNT), -CAPE_DEPTH, maxU, maxV, OverlayTexture.NO_OVERLAY, light,
                    normalVec.x, normalVec.y, normalVec.z, alpha);
            VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[Math.max(part - 1, 0)], -CAPE_WIDTH / 2F, part * (CAPE_HEIGHT / PART_COUNT), -CAPE_DEPTH, maxU, minV, OverlayTexture.NO_OVERLAY, light,
                    normalVec.x, normalVec.y, normalVec.z, alpha);
            VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[Math.max(part - 1, 0)], -CAPE_WIDTH / 2F, part * (CAPE_HEIGHT / PART_COUNT), 0, minU, minV, OverlayTexture.NO_OVERLAY, light,
                    normalVec.x, normalVec.y, normalVec.z, alpha);

            minU = 11 / 64F;
            maxU = 12 / 64F;

            normalVec = getNormalVec(positionMatrices[part], positionMatrices[part], positionMatrices[Math.max(part - 1, 0)],
                    new Vector3(CAPE_WIDTH / 2F, (part + 1) * (CAPE_HEIGHT / PART_COUNT), -CAPE_DEPTH),
                    new Vector3(CAPE_WIDTH / 2F, (part + 1) * (CAPE_HEIGHT / PART_COUNT), 0),
                    new Vector3(CAPE_WIDTH / 2F, part * (CAPE_HEIGHT / PART_COUNT), -CAPE_DEPTH), light == 15728880);

            VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[part], CAPE_WIDTH / 2F, (part + 1) * (CAPE_HEIGHT / PART_COUNT), -CAPE_DEPTH, minU, maxV, OverlayTexture.NO_OVERLAY, light,
                    normalVec.x, normalVec.y, normalVec.z, alpha);
            VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[part], CAPE_WIDTH / 2F, (part + 1) * (CAPE_HEIGHT / PART_COUNT), 0, maxU, maxV, OverlayTexture.NO_OVERLAY, light,
                    normalVec.x, normalVec.y, normalVec.z, alpha);
            VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[Math.max(part - 1, 0)], CAPE_WIDTH / 2F, part * (CAPE_HEIGHT / PART_COUNT), 0, maxU, minV, OverlayTexture.NO_OVERLAY, light,
                    normalVec.x, normalVec.y, normalVec.z, alpha);
            VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[Math.max(part - 1, 0)], CAPE_WIDTH / 2F, part * (CAPE_HEIGHT / PART_COUNT), -CAPE_DEPTH, minU, minV, OverlayTexture.NO_OVERLAY, light,
                    normalVec.x, normalVec.y, normalVec.z, alpha);

            minU = 1 / 64F;
            maxU = 11 / 64F;

            Vector3 normalVecTop = frontNormalVecs[part].clone().add(frontNormalVecs[Math.max(part - 1, 0)]).div(2);
            Vector3 normalVecBottom = frontNormalVecs[part].clone().add(frontNormalVecs[Math.min(part + 1, PART_COUNT - 1)]).div(2);

            VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[Math.max(part - 1, 0)], CAPE_WIDTH / 2, part * (CAPE_HEIGHT / PART_COUNT), -CAPE_DEPTH, maxU, minV, OverlayTexture.NO_OVERLAY, light,
                    normalVecTop.x, normalVecTop.y, normalVecTop.z, alpha);
            VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[Math.max(part - 1, 0)], -CAPE_WIDTH / 2, part * (CAPE_HEIGHT / PART_COUNT), -CAPE_DEPTH, minU, minV, OverlayTexture.NO_OVERLAY, light,
                    normalVecTop.x, normalVecTop.y, normalVecTop.z, alpha);
            VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[part], -CAPE_WIDTH / 2, (part + 1) * (CAPE_HEIGHT / PART_COUNT), -CAPE_DEPTH, minU, maxV, OverlayTexture.NO_OVERLAY, light,
                    normalVecBottom.x, normalVecBottom.y, normalVecBottom.z, alpha);
            VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[part], CAPE_WIDTH / 2, (part + 1) * (CAPE_HEIGHT / PART_COUNT), -CAPE_DEPTH, maxU, maxV, OverlayTexture.NO_OVERLAY, light,
                    normalVecBottom.x, normalVecBottom.y, normalVecBottom.z, alpha);

            minU = 12 / 64F;
            maxU = 22 / 64F;

            normalVecTop = backNormalVecs[part].clone().add(backNormalVecs[Math.max(part - 1, 0)]).div(2);
            normalVecBottom = backNormalVecs[part].clone().add(backNormalVecs[Math.min(part + 1, PART_COUNT - 1)]).div(2);

            VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[Math.max(part - 1, 0)], CAPE_WIDTH / 2, part * (CAPE_HEIGHT / PART_COUNT), 0, minU, maxV, OverlayTexture.NO_OVERLAY, light,
                    normalVecTop.x, normalVecTop.y, normalVecTop.z, alpha);
            VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[Math.max(part - 1, 0)], -CAPE_WIDTH / 2, part * (CAPE_HEIGHT / PART_COUNT), 0, maxU, maxV, OverlayTexture.NO_OVERLAY, light,
                    normalVecTop.x, normalVecTop.y, normalVecTop.z, alpha);
            VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[part], -CAPE_WIDTH / 2, (part + 1) * (CAPE_HEIGHT / PART_COUNT), 0, maxU, minV, OverlayTexture.NO_OVERLAY, light,
                    normalVecBottom.x, normalVecBottom.y, normalVecBottom.z, alpha);
            VertexConsumerUtil.addVertex(bufferBuilder, positionMatrices[part], CAPE_WIDTH / 2, (part + 1) * (CAPE_HEIGHT / PART_COUNT), 0, minU, minV, OverlayTexture.NO_OVERLAY, light,
                    normalVecBottom.x, normalVecBottom.y, normalVecBottom.z, alpha);
        }
    }

    private void modifyPoseStack(PoseStack poseStack, PlayerWrapper capeRenderInfo, float h, int part) {
        if (WaveyCapesBase.config.capeMovement != CapeMovement.VANILLA) {
            modifyPoseStackSimulation(poseStack, capeRenderInfo, h, part);
            return;
        }
        //? if < 1.21.2 {
        /*
         modifyPoseStackVanilla(poseStack, (AbstractClientPlayer) capeRenderInfo.getEntity(), h, part);
        *///? } else {

        var renderState = capeRenderInfo.getRenderState();
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.0D, 0.125D);
        //? if >= 1.21.9 {

        var entity = capeRenderInfo.getAvatar();
        //? } else {
        /*
         var entity = capeRenderInfo.getEntity();
        *///? }
        poseStack.mulPose(MathUtil.XP.rotationDegrees(6.0F + renderState.capeLean / 2.0F + renderState.capeFlap
                + getNatrualWindSwing(part, entity.isUnderWater())));
        poseStack.mulPose(MathUtil.ZP.rotationDegrees(renderState.capeLean2 / 2.0F));
        poseStack.mulPose(MathUtil.YP.rotationDegrees(180.0F - renderState.capeLean2 / 2.0F));
        //? }
    }

    private void modifyPoseStackSimulation(PoseStack poseStack, PlayerWrapper capeRenderInfo, float delta, int part) {
        //? if >= 1.21.9 {

        var entity = capeRenderInfo.getAvatar();
        //? } else {
        /*
         var entity = capeRenderInfo.getEntity();
        *///? }
        BasicSimulation simulation = ((CapeHolder) entity).getSimulation();
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.0D, 0.125D);

        float x = simulation.getPoints().get(part).getLerpX(delta) - simulation.getPoints().get(0).getLerpX(delta);
        if (x > 0) {
            x = 0;
        }
        float y = simulation.getPoints().get(0).getLerpY(delta) - part
                - simulation.getPoints().get(part).getLerpY(delta);
        float z = simulation.getPoints().get(0).getLerpZ(delta) - simulation.getPoints().get(part).getLerpZ(delta);

        float sidewaysRotationOffset = 0;
        float partRotation = getRotation(delta, part, simulation);

        float height = 0;
        //        if (abstractClientPlayer.isCrouching()) {
        //            height += 25.0F;
        //            poseStack.translate(0, 0.15F, 0);
        //        }

        float naturalWindSwing = getNatrualWindSwing(part, entity.isUnderWater());

        // vanilla rotating and wind
        poseStack.mulPose(MathUtil.XP.rotationDegrees(6.0F + height + naturalWindSwing));
        poseStack.mulPose(MathUtil.ZP.rotationDegrees(sidewaysRotationOffset / 2.0F));
        poseStack.mulPose(MathUtil.YP.rotationDegrees(180.0F - sidewaysRotationOffset / 2.0F));
        poseStack.translate(-z / PART_COUNT, y / PART_COUNT, x / PART_COUNT); // movement from the simulation
        // offsetting so the rotation is on the cape part
        // float offset = (float) (part * (16 / partCount))/16; // to fold the entire
        // cape into one position for debugging
        poseStack.translate(0, /*-offset*/ +(0.48 / 16), -(0.48 / 16)); // (0.48/16)
        poseStack.translate(0, part * 1f / PART_COUNT, part * (0) / PART_COUNT);
        poseStack.mulPose(MathUtil.XP.rotationDegrees(-partRotation)); // apply actual rotation
        // undoing the rotation
        poseStack.translate(0, -part * 1f / PART_COUNT, -part * (0) / PART_COUNT);
        poseStack.translate(0, -(0.48 / 16), (0.48 / 16));

    }

    private float getRotation(float delta, int part, BasicSimulation simulation) {
        if (part == PART_COUNT - 1) {
            return getRotation(delta, part - 1, simulation);
        }
        return (float) getAngle(simulation.getPoints().get(part).getLerpedPos(delta),
                simulation.getPoints().get(part + 1).getLerpedPos(delta));
    }

    private double getAngle(Vector3 a, Vector3 b) {
        Vector3 angle = b.subtract(a);
        return Math.toDegrees(Math.atan2(angle.x, angle.y)) + 180;
    }

    //? if < 1.21.2 {
    /*
        private void modifyPoseStackVanilla(PoseStack poseStack, AbstractClientPlayer abstractClientPlayer, float h,
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
        long highlightedPart = (System.currentTimeMillis() / (underwater ? 9 : 3)) % 360;
        float relativePart = (float) (part + 1) / PART_COUNT;
        if (WaveyCapesBase.config.windMode == WindMode.WAVES) {
            return (float) (Math.sin(Math.toRadians((relativePart) * 360 - (highlightedPart))) * 3);
        }
        return 0;
    }

    private static Vector3 getNormalVec(Matrix4f matrix1, Matrix4f matrix2, Matrix4f matrix3, Vector3 vector1,
            Vector3 vector2, Vector3 vector3, boolean inverse) {
        Vector3 vector1Transformed = transform(matrix1, new Vector4(vector1.x, vector1.y, vector1.z, 1)).toVec3();
        Vector3 vector2Transformed = transform(matrix2, new Vector4(vector2.x, vector2.y, vector2.z, 1)).toVec3();
        Vector3 vector3Transformed = transform(matrix3, new Vector4(vector3.x, vector3.y, vector3.z, 1)).toVec3();

        vector2Transformed.subtract(vector1Transformed);
        vector3Transformed.subtract(vector1Transformed);

        vector2Transformed.cross(vector3Transformed);
        vector2Transformed.normalize();
        return inverse ? vector2Transformed.mul(-1) : vector2Transformed;
    }

    private static Vector4 transform(Matrix4f matrix, Vector4 vector) {
        //? if >= 1.19.3 {

        Vector4f vector4f = matrix.transform(new Vector4f(vector.x, vector.y, vector.z, vector.w));
        return new Vector4(vector4f.x, vector4f.y, vector4f.z, vector4f.w);
        //? } else {
        /*
         Vector4f vector4f = new Vector4f(vector.x, vector.y, vector.z, vector.w);
         vector4f.transform(matrix);
         return new Vector4(vector4f.x(), vector4f.y(), vector4f.z(), vector4f.w());
        *///? }
    }

    private static VanillaCapeRenderer vanillaCape = new VanillaCapeRenderer();

    private CapeRenderer getCapeRenderer(PlayerWrapper capeRenderInfo) {
        for (ModSupport support : SupportManager.getSupportedMods()) {
            if (support.shouldBeUsed(capeRenderInfo)) {
                return support.getRenderer();
            }
        }
        if (capeRenderInfo.getCapeTexture() == null || !capeRenderInfo.isCapeVisible()) {
            return null;
        } else {
            return vanillaCape;
        }
    }

}
