package dev.tr7zw.waveycapes.renderlayers;

import dev.tr7zw.waveycapes.CapeHolder;
import dev.tr7zw.waveycapes.CapeMovement;
import dev.tr7zw.waveycapes.WaveyCapesBase;
import dev.tr7zw.waveycapes.sim.BasicSimulation;
import dev.tr7zw.waveycapes.sim.CapePoint;
import dev.tr7zw.waveycapes.sim.Vector3;
import dev.tr7zw.waveycapes.sim.Vector4;
import dev.tr7zw.waveycapes.util.Matrix4f;
import dev.tr7zw.waveycapes.util.Mth;
import dev.tr7zw.waveycapes.util.PoseStack;
import dev.tr7zw.waveycapes.util.Vector3f;
import dev.tr7zw.waveycapes.util.Vector4f;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;

public class SmoothCapeRenderer {

    public void renderSmoothCape(CustomCapeRenderLayer layer, AbstractClientPlayer abstractClientPlayer, float delta) {
        WorldRenderer worldrenderer = Tessellator.getInstance().getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();

        // Pass 1: Collect all matrices and compute normals
        Matrix4f[] matrices = new Matrix4f[CustomCapeRenderLayer.partCount];
        Vector3[] frontNormalVecs = new Vector3[CustomCapeRenderLayer.partCount];
        Vector3[] backNormalVecs = new Vector3[CustomCapeRenderLayer.partCount];

        for (int part = 0; part < CustomCapeRenderLayer.partCount; part++) {
            modifyPoseStack(layer, poseStack, abstractClientPlayer, delta, part);
            matrices[part] = poseStack.last().pose();
            poseStack.popPose();
        }

        // Compute normals for front and back faces
        for (int part = 0; part < CustomCapeRenderLayer.partCount; part++) {
            frontNormalVecs[part] = getNormalVec(matrices[part],
                    0.3F, (part + 1) * (0.96F / CustomCapeRenderLayer.partCount), 0F,
                    -0.3F, (part + 1) * (0.96F / CustomCapeRenderLayer.partCount), 0F,
                    0.3F, part * (0.96F / CustomCapeRenderLayer.partCount), 0F);
            backNormalVecs[part] = getNormalVec(matrices[part],
                    -0.3F, (part + 1) * (0.96F / CustomCapeRenderLayer.partCount), -0.06F,
                    0.3F, (part + 1) * (0.96F / CustomCapeRenderLayer.partCount), -0.06F,
                    -0.3F, part * (0.96F / CustomCapeRenderLayer.partCount), -0.06F);
        }

        // Pass 2: Render geometry using stored matrices and computed normals
        Matrix4f oldPositionMatrix = null;
        for (int part = 0; part < CustomCapeRenderLayer.partCount; part++) {
            Matrix4f matrix = matrices[part];

            if (oldPositionMatrix == null) {
                oldPositionMatrix = matrix;
            }

            // Average normals between adjacent parts for smooth shading
            Vector3 frontNormal = frontNormalVecs[part];
            Vector3 backNormal = backNormalVecs[part];
            Vector3 prevFrontNormal = part > 0 ? frontNormalVecs[part - 1] : frontNormal;
            Vector3 prevBackNormal = part > 0 ? backNormalVecs[part - 1] : backNormal;

            // Averaged normals for top edge of this part (shared with bottom edge of previous part)
            Vector3 avgFrontTop = average(prevFrontNormal, frontNormal);
            Vector3 avgBackTop = average(prevBackNormal, backNormal);

            if (part == 0) {
                addTopVertex(worldrenderer, matrix, oldPositionMatrix,
                        0.3F,
                        0,
                        0F,
                        -0.3F,
                        0,
                        -0.06F, part);
            }

            if (part == CustomCapeRenderLayer.partCount - 1) {
                addBottomVertex(worldrenderer, matrix, matrix,
                        0.3F,
                        (part + 1) * (0.96F / CustomCapeRenderLayer.partCount),
                        0F,
                        -0.3F,
                        (part + 1) * (0.96F / CustomCapeRenderLayer.partCount),
                        -0.06F, part);
            }

            addLeftVertex(worldrenderer, matrix, oldPositionMatrix,
                    -0.3F,
                    (part + 1) * (0.96F / CustomCapeRenderLayer.partCount),
                    0F,
                    -0.3F,
                    part * (0.96F / CustomCapeRenderLayer.partCount),
                    -0.06F, part);

            addRightVertex(worldrenderer, matrix, oldPositionMatrix,
                    0.3F,
                    (part + 1) * (0.96F / CustomCapeRenderLayer.partCount),
                    0F,
                    0.3F,
                    part * (0.96F / CustomCapeRenderLayer.partCount),
                    -0.06F, part);

            addBackVertex(worldrenderer, matrix, oldPositionMatrix,
                    0.3F,
                    (part + 1) * (0.96F / CustomCapeRenderLayer.partCount),
                    -0.06F,
                    -0.3F,
                    part * (0.96F / CustomCapeRenderLayer.partCount),
                    -0.06F, part, backNormal, avgBackTop);

            addFrontVertex(worldrenderer, oldPositionMatrix, matrix,
                    0.3F,
                    (part + 1) * (0.96F / CustomCapeRenderLayer.partCount),
                    0F,
                    -0.3F,
                    part * (0.96F / CustomCapeRenderLayer.partCount),
                    0F, part, frontNormal, avgFrontTop);

            oldPositionMatrix = matrix;
        }
        Tessellator.getInstance().draw();
    }

    private Vector3 average(Vector3 a, Vector3 b) {
        return new Vector3((a.x + b.x) / 2f, (a.y + b.y) / 2f, (a.z + b.z) / 2f).normalize();
    }

    private Vector3 getNormalVec(Matrix4f matrix,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3) {
        Vector4 p1 = transform(matrix, new Vector4(x1, y1, z1, 1.0F));
        Vector4 p2 = transform(matrix, new Vector4(x2, y2, z2, 1.0F));
        Vector4 p3 = transform(matrix, new Vector4(x3, y3, z3, 1.0F));

        Vector3 v1 = p2.toVec3().subtract(p1.toVec3());
        Vector3 v2 = p3.toVec3().subtract(p1.toVec3());
        return v1.cross(v2).normalize();
    }

    private Vector4 transform(Matrix4f matrix, Vector4 vec) {
        Vector4f v4f = new Vector4f(vec.x, vec.y, vec.z, vec.w);
        v4f.transform(matrix);
        return new Vector4(v4f.x(), v4f.y(), v4f.z(), v4f.w());
    }

    void modifyPoseStack(CustomCapeRenderLayer layer, PoseStack poseStack, AbstractClientPlayer abstractClientPlayer, float h, int part) {
        if(WaveyCapesBase.config.capeMovement != CapeMovement.VANILLA) {
            modifyPoseStackSimulation(layer, poseStack, abstractClientPlayer, h, part);
            return;
        }
        modifyPoseStackVanilla(layer, poseStack, abstractClientPlayer, h, part);
    }

    private void modifyPoseStackSimulation(CustomCapeRenderLayer layer, PoseStack poseStack, AbstractClientPlayer abstractClientPlayer, float delta, int part) {
        BasicSimulation simulation = ((CapeHolder)abstractClientPlayer).getSimulation();
        if (simulation == null || simulation.empty()) {
            modifyPoseStackVanilla(layer, poseStack, abstractClientPlayer, delta, part);
            return;
        }
        java.util.List<CapePoint> points = simulation.getPoints();
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.0D, 0.125D);

        float x = points.get(part).getLerpX(delta) - points.get(0).getLerpX(delta);
        if(x > 0) {
            x = 0;
        }
        float y = points.get(0).getLerpY(delta) - part - points.get(part).getLerpY(delta);
        float z = points.get(0).getLerpZ(delta) - points.get(part).getLerpZ(delta);

        float sidewaysRotationOffset = 0;
        float partRotation = getRotation(points, part, delta);

        float height = 0;
        if (abstractClientPlayer.isSneaking()) {
            height += 25.0F;
            poseStack.translate(0, 0.15F, 0);
        }

        float naturalWindSwing = layer.getNatrualWindSwing(part);


        // vanilla rotating and wind
        poseStack.mulPose(Vector3f.XP.rotationDegrees(6.0F + height + naturalWindSwing));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(sidewaysRotationOffset / 2.0F));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - sidewaysRotationOffset / 2.0F));
        poseStack.translate(-z/CustomCapeRenderLayer.partCount, y/CustomCapeRenderLayer.partCount, x/CustomCapeRenderLayer.partCount); // movement from the simulation
        //offsetting so the rotation is on the cape part
        poseStack.translate(0, (0.48/16) , - (0.48/16)); // (0.48/16)
        poseStack.translate(0, part * 1f/CustomCapeRenderLayer.partCount, part * (0)/CustomCapeRenderLayer.partCount);
        poseStack.mulPose(Vector3f.XP.rotationDegrees(-partRotation)); // apply actual rotation
        // undoing the rotation
        poseStack.translate(0, -part * 1f/CustomCapeRenderLayer.partCount, -part * (0)/CustomCapeRenderLayer.partCount);
        poseStack.translate(0, -(0.48/16), (0.48/16));

    }

    private float getRotation(java.util.List<CapePoint> points, int part, float delta) {
        if (part == CustomCapeRenderLayer.partCount - 1) {
            return getRotation(points, part - 1, delta);
        }
        return (float) getAngle(points.get(part).getLerpedPos(delta),
                points.get(part + 1).getLerpedPos(delta));
    }

    private double getAngle(Vector3 a, Vector3 b) {
        Vector3 angle = b.subtract(a);
        return Math.toDegrees(Math.atan2(angle.x, angle.y)) + 180;
    }

    private void modifyPoseStackVanilla(CustomCapeRenderLayer layer, PoseStack poseStack, AbstractClientPlayer abstractClientPlayer, float h, int part) {
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.0D, 0.125D);
        double d = Mth.lerp(h, abstractClientPlayer.prevChasingPosX, abstractClientPlayer.chasingPosX)
                - Mth.lerp(h, abstractClientPlayer.prevPosX, abstractClientPlayer.posX);
        double e = Mth.lerp(h, abstractClientPlayer.prevChasingPosY, abstractClientPlayer.chasingPosY)
                - Mth.lerp(h, abstractClientPlayer.prevPosY, abstractClientPlayer.posY);
        double m = Mth.lerp(h, abstractClientPlayer.prevChasingPosZ, abstractClientPlayer.chasingPosZ)
                - Mth.lerp(h, abstractClientPlayer.prevPosZ, abstractClientPlayer.posZ);
        float n = abstractClientPlayer.prevRenderYawOffset + abstractClientPlayer.renderYawOffset - abstractClientPlayer.prevRenderYawOffset;
        double o = Math.sin(n * 0.017453292F);
        double p = -Math.cos(n * 0.017453292F);
        float height = (float) e * 10.0F;
        height = MathHelper.clamp_float(height, -6.0F, 32.0F);
        float swing = (float) (d * o + m * p) * easeOutSine(1.0F/CustomCapeRenderLayer.partCount*part)*100;
        swing = MathHelper.clamp_float(swing, 0.0F, 150.0F * easeOutSine(1F/CustomCapeRenderLayer.partCount*part));
        float sidewaysRotationOffset = (float) (d * p - m * o) * 100.0F;
        sidewaysRotationOffset = MathHelper.clamp_float(sidewaysRotationOffset, -20.0F, 20.0F);
        float t = Mth.lerp(h, abstractClientPlayer.prevCameraYaw, abstractClientPlayer.cameraYaw);
        height += Math.sin(Mth.lerp(h, abstractClientPlayer.prevDistanceWalkedModified, abstractClientPlayer.distanceWalkedModified) * 6.0F) * 32.0F * t;
        if (abstractClientPlayer.isSneaking()) {
            height += 25.0F;
            poseStack.translate(0, 0.15F, 0);
        }

        float naturalWindSwing = layer.getNatrualWindSwing(part);

        poseStack.mulPose(Vector3f.XP.rotationDegrees(6.0F + swing / 2.0F + height + naturalWindSwing));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(sidewaysRotationOffset / 2.0F));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - sidewaysRotationOffset / 2.0F));
    }

    private static void addBackVertex(WorldRenderer worldrenderer, Matrix4f matrix, Matrix4f oldMatrix, float x1, float y1, float z1, float x2, float y2, float z2, int part, Vector3 normal, Vector3 topNormal) {
        float i;
        Matrix4f k;
        if (x1 < x2) {
            i = x1;
            x1 = x2;
            x2 = i;
        }

        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;

            k = matrix;
            matrix = oldMatrix;
            oldMatrix = k;
        }

        float minU = .015625F;
        float maxU = .171875F;

        float minV = .03125F;
        float maxV = .53125F;

        float deltaV = maxV - minV;
        float vPerPart = deltaV / CustomCapeRenderLayer.partCount;
        maxV = minV + (vPerPart * (part + 1));
        minV = minV + (vPerPart * part);

        //oldMatrix - top edge uses averaged normal
        vertex(worldrenderer, oldMatrix, x1, y2, z1).tex(maxU, minV).normal(topNormal.x, topNormal.y, topNormal.z).endVertex();
        vertex(worldrenderer, oldMatrix, x2, y2, z1).tex(minU, minV).normal(topNormal.x, topNormal.y, topNormal.z).endVertex();
        //matrix - bottom edge uses current part normal
        vertex(worldrenderer, matrix, x2, y1, z2).tex(minU, maxV).normal(normal.x, normal.y, normal.z).endVertex();
        vertex(worldrenderer, matrix, x1, y1, z2).tex(maxU, maxV).normal(normal.x, normal.y, normal.z).endVertex();

   }

    private static void addFrontVertex(WorldRenderer worldrenderer, Matrix4f matrix, Matrix4f oldMatrix, float x1, float y1, float z1, float x2, float y2, float z2, int part, Vector3 normal, Vector3 topNormal) {
        float i;
        Matrix4f k;
        if (x1 < x2) {
            i = x1;
            x1 = x2;
            x2 = i;
        }

        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;

            k = matrix;
            matrix = oldMatrix;
            oldMatrix = k;
        }

        float minU = .1875F;
        float maxU = .34375F;

        float minV = .03125F;
        float maxV = .53125F;

        float deltaV = maxV - minV;
        float vPerPart = deltaV / CustomCapeRenderLayer.partCount;
        maxV = minV + (vPerPart * (part + 1));
        minV = minV + (vPerPart * part);

        //oldMatrix - bottom edge uses current part normal
        vertex(worldrenderer, oldMatrix, x1, y1, z1).tex(maxU, maxV).normal(normal.x, normal.y, normal.z).endVertex();
        vertex(worldrenderer, oldMatrix, x2, y1, z1).tex(minU, maxV).normal(normal.x, normal.y, normal.z).endVertex();
        //matrix - top edge uses averaged normal
        vertex(worldrenderer, matrix, x2, y2, z2).tex(minU, minV).normal(topNormal.x, topNormal.y, topNormal.z).endVertex();
        vertex(worldrenderer, matrix, x1, y2, z2).tex(maxU, minV).normal(topNormal.x, topNormal.y, topNormal.z).endVertex();

   }

    private static void addLeftVertex(WorldRenderer worldrenderer, Matrix4f matrix, Matrix4f oldMatrix, float x1, float y1, float z1, float x2, float y2, float z2, int part) {
        float i;
        if (x1 < x2) {
            i = x1;
            x1 = x2;
            x2 = i;
        }

        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;
        }

        float minU = 0;
        float maxU = .015625F;

        float minV = .03125F;
        float maxV = .53125F;

        float deltaV = maxV - minV;
        float vPerPart = deltaV / CustomCapeRenderLayer.partCount;
        maxV = minV + (vPerPart * (part + 1));
        minV = minV + (vPerPart * part);

        //matrix
        vertex(worldrenderer, matrix, x2, y1, z1).tex(maxU, maxV).normal(-1, 0, 0).endVertex();
        vertex(worldrenderer, matrix, x2, y1, z2).tex(minU, maxV).normal(-1, 0, 0).endVertex();
        //oldMatrix
        vertex(worldrenderer, oldMatrix, x2, y2, z2).tex(minU, minV).normal(-1, 0, 0).endVertex();
        vertex(worldrenderer, oldMatrix, x2, y2, z1).tex(maxU, minV).normal(-1, 0, 0).endVertex();

    }

    private static void addRightVertex(WorldRenderer worldrenderer, Matrix4f matrix, Matrix4f oldMatrix, float x1, float y1, float z1, float x2, float y2, float z2, int part) {
        float i;
        if (x1 < x2) {
            i = x1;
            x1 = x2;
            x2 = i;
        }

        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;
        }

        float minU = .171875F;
        float maxU = .1875F;

        float minV = .03125F;
        float maxV = .53125F;

        float deltaV = maxV - minV;
        float vPerPart = deltaV / CustomCapeRenderLayer.partCount;
        maxV = minV + (vPerPart * (part + 1));
        minV = minV + (vPerPart * part);

        //matrix
        vertex(worldrenderer, matrix, x2, y1, z2).tex(minU, maxV).normal(1, 0, 0).endVertex();
        vertex(worldrenderer, matrix, x2, y1, z1).tex(maxU, maxV).normal(1, 0, 0).endVertex();
        //oldMatrix
        vertex(worldrenderer, oldMatrix, x2, y2, z1).tex(maxU, minV).normal(1, 0, 0).endVertex();
        vertex(worldrenderer, oldMatrix, x2, y2, z2).tex(minU, minV).normal(1, 0, 0).endVertex();

    }

    private static void addBottomVertex(WorldRenderer worldrenderer, Matrix4f matrix, Matrix4f oldMatrix, float x1, float y1, float z1, float x2, float y2, float z2, int part) {
        float i;
        if (x1 < x2) {
            i = x1;
            x1 = x2;
            x2 = i;
        }

        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;
        }

        float minU = .171875F;
        float maxU = .328125F;

        float minV = 0;
        float maxV = .03125F;

        float deltaV = maxV - minV;
        float vPerPart = deltaV / CustomCapeRenderLayer.partCount;
        maxV = minV + (vPerPart * (part + 1));
        minV = minV + (vPerPart * part);

        //oldMatrix
        vertex(worldrenderer, oldMatrix, x1, y2, z2).tex(maxU, minV).normal(0, -1, 0).endVertex();
        vertex(worldrenderer, oldMatrix, x2, y2, z2).tex(minU, minV).normal(0, -1, 0).endVertex();
        //newMatrix
        vertex(worldrenderer, matrix, x2, y1, z1).tex(minU, maxV).normal(0, -1, 0).endVertex();
        vertex(worldrenderer, matrix, x1, y1, z1).tex(maxU, maxV).normal(0, -1, 0).endVertex();

    }

    private static WorldRenderer vertex(WorldRenderer worldrenderer, Matrix4f matrix4f, float f, float g, float h) {
        Vector4f vector4f = new Vector4f(f, g, h, 1.0F);
        vector4f.transform(matrix4f);
        worldrenderer.pos(vector4f.x(), vector4f.y(), vector4f.z());
        return worldrenderer;
    }

    private static void addTopVertex(WorldRenderer worldrenderer, Matrix4f matrix, Matrix4f oldMatrix, float x1, float y1, float z1, float x2, float y2, float z2, int part) {
        float i;
        if (x1 < x2) {
            i = x1;
            x1 = x2;
            x2 = i;
        }

        if (y1 < y2) {
            i = y1;
            y1 = y2;
            y2 = i;
        }

        float minU = .015625F;
        float maxU = .171875F;

        float minV = 0;
        float maxV = .03125F;

        float deltaV = maxV - minV;
        float vPerPart = deltaV / CustomCapeRenderLayer.partCount;
        maxV = minV + (vPerPart * (part + 1));
        minV = minV + (vPerPart * part);

        //oldMatrix
        vertex(worldrenderer, oldMatrix, x1, y2, z1).tex(maxU, maxV).normal(0, 1, 0).endVertex();
        vertex(worldrenderer, oldMatrix, x2, y2, z1).tex(minU, maxV).normal(0, 1, 0).endVertex();
        //newMatrix
        vertex(worldrenderer, matrix, x2, y1, z2).tex(minU, minV).normal(0, 1, 0).endVertex();
        vertex(worldrenderer, matrix, x1, y1, z2).tex(maxU, minV).normal(0, 1, 0).endVertex();
   }

    /**
     * https://easings.net/#easeOutSine
     *
     * @param x
     * @return
     */
    private static float easeOutSine(float x) {
        return (float) Math.sin((x * Math.PI) / 2f);
      }

}
