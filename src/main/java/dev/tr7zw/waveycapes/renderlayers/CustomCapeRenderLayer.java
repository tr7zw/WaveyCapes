package dev.tr7zw.waveycapes.renderlayers;

//spotless:off
//#if MC >= 11903
import org.joml.Matrix4f;
//#else
//$$import com.mojang.math.Matrix4f;
//#endif
//spotless:on

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.tr7zw.util.NMSHelper;
import dev.tr7zw.waveycapes.CapeRenderer;
import dev.tr7zw.waveycapes.NMSUtil;
import dev.tr7zw.waveycapes.PlayerModelAccess;
import dev.tr7zw.waveycapes.VanillaCapeRenderer;
import dev.tr7zw.waveycapes.WaveyCapesBase;
import dev.tr7zw.waveycapes.delegate.PlayerDelegate;
import dev.tr7zw.waveycapes.support.ModSupport;
import dev.tr7zw.waveycapes.support.SupportManager;
import dev.tr7zw.waveycapes.versionless.CapeHolder;
import dev.tr7zw.waveycapes.versionless.CapeMovement;
import dev.tr7zw.waveycapes.versionless.CapeStyle;
import dev.tr7zw.waveycapes.versionless.ModBase;
import dev.tr7zw.waveycapes.versionless.WindMode;
import dev.tr7zw.waveycapes.versionless.sim.BasicSimulation;
import dev.tr7zw.waveycapes.versionless.util.Vector3;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CustomCapeRenderLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private static final int PART_COUNT = 16;
    private ModelPart[] customCape = NMSUtil.buildCape(64, 64, x -> 0, y -> y);

    public CustomCapeRenderLayer(
            RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderLayerParent) {
        super(renderLayerParent);
    }

    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i,
            AbstractClientPlayer abstractClientPlayer, float f, float g, float delta, float j, float k, float l) {
        if (abstractClientPlayer.isInvisible())
            return;
        CapeRenderer renderer = getCapeRenderer(abstractClientPlayer, multiBufferSource);
        if (renderer == null)
            return;
        ItemStack itemStack = abstractClientPlayer.getItemBySlot(EquipmentSlot.CHEST);
        if (!itemStack.isEmpty() && itemStack.getItem() == Items.ELYTRA)
            return;
        if (getParentModel() instanceof PlayerModelAccess pma && !pma.getCloak().visible) {
            return;
        }

        CapeHolder holder = (CapeHolder) abstractClientPlayer;
        holder.updateSimulation(new PlayerDelegate(abstractClientPlayer), PART_COUNT);
        poseStack.pushPose();
        getParentModel().body.translateAndRotate(poseStack);
        if (ModBase.config.capeStyle == CapeStyle.SMOOTH && renderer.vanillaUvValues()) {
            renderSmoothCape(poseStack, multiBufferSource, renderer, abstractClientPlayer, delta, i);
        } else {
            ModelPart[] parts = customCape;
            for (int part = 0; part < PART_COUNT; part++) {
                ModelPart model = parts[part];
                modifyPoseStack(poseStack, abstractClientPlayer, delta, part);
                renderer.render(abstractClientPlayer, part, model, poseStack, multiBufferSource, i,
                        OverlayTexture.NO_OVERLAY);
                poseStack.popPose();
            }
        }
        poseStack.popPose();
    }

    private void renderSmoothCape(PoseStack poseStack, MultiBufferSource multiBufferSource, CapeRenderer capeRenderer,
            AbstractClientPlayer abstractClientPlayer, float delta, int light) {
        VertexConsumer bufferBuilder = capeRenderer.getVertexConsumer(multiBufferSource, abstractClientPlayer);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        Matrix4f oldPositionMatrix = null;
        for (int part = 0; part < PART_COUNT; part++) {
            modifyPoseStack(poseStack, abstractClientPlayer, delta, part);

            if (oldPositionMatrix == null) {
                oldPositionMatrix = poseStack.last().pose();
            }

            if (part == 0) {
                addTopVertex(bufferBuilder, poseStack.last().pose(), oldPositionMatrix, 0.3F, 0, 0F, -0.3F, 0, -0.06F,
                        part, light);
            }

            if (part == PART_COUNT - 1) {
                addBottomVertex(bufferBuilder, poseStack.last().pose(), poseStack.last().pose(), 0.3F,
                        (part + 1) * (0.96F / PART_COUNT), 0F, -0.3F, (part + 1) * (0.96F / PART_COUNT), -0.06F, part,
                        light);
            }

            addLeftVertex(bufferBuilder, poseStack.last().pose(), oldPositionMatrix, -0.3F,
                    (part + 1) * (0.96F / PART_COUNT), 0F, -0.3F, part * (0.96F / PART_COUNT), -0.06F, part, light);

            addRightVertex(bufferBuilder, poseStack.last().pose(), oldPositionMatrix, 0.3F,
                    (part + 1) * (0.96F / PART_COUNT), 0F, 0.3F, part * (0.96F / PART_COUNT), -0.06F, part, light);

            addBackVertex(bufferBuilder, poseStack.last().pose(), oldPositionMatrix, 0.3F,
                    (part + 1) * (0.96F / PART_COUNT), -0.06F, -0.3F, part * (0.96F / PART_COUNT), -0.06F, part, light);

            addFrontVertex(bufferBuilder, oldPositionMatrix, poseStack.last().pose(), 0.3F,
                    (part + 1) * (0.96F / PART_COUNT), 0F, -0.3F, part * (0.96F / PART_COUNT), 0F, part, light);

            oldPositionMatrix = new Matrix4f(poseStack.last().pose());
            poseStack.popPose();
        }

    }

    private void modifyPoseStack(PoseStack poseStack, AbstractClientPlayer abstractClientPlayer, float h, int part) {
        if (WaveyCapesBase.config.capeMovement != CapeMovement.VANILLA) {
            modifyPoseStackSimulation(poseStack, abstractClientPlayer, h, part);
            return;
        }
        modifyPoseStackVanilla(poseStack, abstractClientPlayer, h, part);
    }

    private void modifyPoseStackSimulation(PoseStack poseStack, AbstractClientPlayer abstractClientPlayer, float delta,
            int part) {
        BasicSimulation simulation = ((CapeHolder) abstractClientPlayer).getSimulation();
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

        float naturalWindSwing = getNatrualWindSwing(part, abstractClientPlayer.isUnderWater());

        // vanilla rotating and wind
        poseStack.mulPose(NMSHelper.XP.rotationDegrees(6.0F + height + naturalWindSwing));
        poseStack.mulPose(NMSHelper.ZP.rotationDegrees(sidewaysRotationOffset / 2.0F));
        poseStack.mulPose(NMSHelper.YP.rotationDegrees(180.0F - sidewaysRotationOffset / 2.0F));
        poseStack.translate(-z / PART_COUNT, y / PART_COUNT, x / PART_COUNT); // movement from the simulation
        // offsetting so the rotation is on the cape part
        // float offset = (float) (part * (16 / partCount))/16; // to fold the entire
        // cape into one position for debugging
        poseStack.translate(0, /*-offset*/ +(0.48 / 16), -(0.48 / 16)); // (0.48/16)
        poseStack.translate(0, part * 1f / PART_COUNT, part * (0) / PART_COUNT);
        poseStack.mulPose(NMSHelper.XP.rotationDegrees(-partRotation)); // apply actual rotation
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

        poseStack.mulPose(NMSHelper.XP.rotationDegrees(6.0F + swing / 2.0F + height + naturalWindSwing));
        poseStack.mulPose(NMSHelper.ZP.rotationDegrees(sidewaysRotationOffset / 2.0F));
        poseStack.mulPose(NMSHelper.YP.rotationDegrees(180.0F - sidewaysRotationOffset / 2.0F));
    }

    private float getNatrualWindSwing(int part, boolean underwater) {
        long highlightedPart = (System.currentTimeMillis() / (underwater ? 9 : 3)) % 360;
        float relativePart = (float) (part + 1) / PART_COUNT;
        if (WaveyCapesBase.config.windMode == WindMode.WAVES) {
            return (float) (Math.sin(Math.toRadians((relativePart) * 360 - (highlightedPart))) * 3);
        }
        return 0;
    }

    private static void addBackVertex(VertexConsumer bufferBuilder, Matrix4f matrix, Matrix4f oldMatrix, float x1,
            float y1, float z1, float x2, float y2, float z2, int part, int light) {
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
        float vPerPart = deltaV / PART_COUNT;
        maxV = minV + (vPerPart * (part + 1));
        minV = minV + (vPerPart * part);

        bufferBuilder.vertex(oldMatrix, x1, y2, z1).color(1f, 1f, 1f, 1f).uv(maxU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1, 0, 0).endVertex();
        bufferBuilder.vertex(oldMatrix, x2, y2, z1).color(1f, 1f, 1f, 1f).uv(minU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1, 0, 0).endVertex();
        bufferBuilder.vertex(matrix, x2, y1, z2).color(1f, 1f, 1f, 1f).uv(minU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1, 0, 0).endVertex();
        bufferBuilder.vertex(matrix, x1, y1, z2).color(1f, 1f, 1f, 1f).uv(maxU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1, 0, 0).endVertex();
    }

    private static void addFrontVertex(VertexConsumer bufferBuilder, Matrix4f matrix, Matrix4f oldMatrix, float x1,
            float y1, float z1, float x2, float y2, float z2, int part, int light) {
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
        float vPerPart = deltaV / PART_COUNT;
        maxV = minV + (vPerPart * (part + 1));
        minV = minV + (vPerPart * part);

        bufferBuilder.vertex(oldMatrix, x1, y1, z1).color(1f, 1f, 1f, 1f).uv(minU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1, 0, 0).endVertex();
        bufferBuilder.vertex(oldMatrix, x2, y1, z1).color(1f, 1f, 1f, 1f).uv(maxU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1, 0, 0).endVertex();
        bufferBuilder.vertex(matrix, x2, y2, z2).color(1f, 1f, 1f, 1f).uv(maxU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1, 0, 0).endVertex();
        bufferBuilder.vertex(matrix, x1, y2, z2).color(1f, 1f, 1f, 1f).uv(minU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1, 0, 0).endVertex();
    }

    private static void addLeftVertex(VertexConsumer bufferBuilder, Matrix4f matrix, Matrix4f oldMatrix, float x1,
            float y1, float z1, float x2, float y2, float z2, int part, int light) {
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
        float vPerPart = deltaV / PART_COUNT;
        maxV = minV + (vPerPart * (part + 1));
        minV = minV + (vPerPart * part);

        bufferBuilder.vertex(matrix, x2, y1, z1).color(1f, 1f, 1f, 1f).uv(minU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1, 0, 0).endVertex();
        bufferBuilder.vertex(matrix, x2, y1, z2).color(1f, 1f, 1f, 1f).uv(maxU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1, 0, 0).endVertex();
        bufferBuilder.vertex(oldMatrix, x2, y2, z2).color(1f, 1f, 1f, 1f).uv(maxU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1, 0, 0).endVertex();
        bufferBuilder.vertex(oldMatrix, x2, y2, z1).color(1f, 1f, 1f, 1f).uv(minU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1, 0, 0).endVertex();
    }

    private static void addRightVertex(VertexConsumer bufferBuilder, Matrix4f matrix, Matrix4f oldMatrix, float x1,
            float y1, float z1, float x2, float y2, float z2, int part, int light) {
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
        float vPerPart = deltaV / PART_COUNT;
        maxV = minV + (vPerPart * (part + 1));
        minV = minV + (vPerPart * part);

        bufferBuilder.vertex(matrix, x2, y1, z2).color(1f, 1f, 1f, 1f).uv(minU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1, 0, 0).endVertex();
        bufferBuilder.vertex(matrix, x2, y1, z1).color(1f, 1f, 1f, 1f).uv(maxU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1, 0, 0).endVertex();
        bufferBuilder.vertex(oldMatrix, x2, y2, z1).color(1f, 1f, 1f, 1f).uv(maxU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1, 0, 0).endVertex();
        bufferBuilder.vertex(oldMatrix, x2, y2, z2).color(1f, 1f, 1f, 1f).uv(minU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1, 0, 0).endVertex();
    }

    private static void addBottomVertex(VertexConsumer bufferBuilder, Matrix4f matrix, Matrix4f oldMatrix, float x1,
            float y1, float z1, float x2, float y2, float z2, int part, int light) {
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
        float vPerPart = deltaV / PART_COUNT;
        maxV = minV + (vPerPart * (part + 1));
        minV = minV + (vPerPart * part);

        bufferBuilder.vertex(oldMatrix, x1, y2, z2).color(1f, 1f, 1f, 1f).uv(maxU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1, 0, 0).endVertex();
        bufferBuilder.vertex(oldMatrix, x2, y2, z2).color(1f, 1f, 1f, 1f).uv(minU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1, 0, 0).endVertex();
        bufferBuilder.vertex(matrix, x2, y1, z1).color(1f, 1f, 1f, 1f).uv(minU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1, 0, 0).endVertex();
        bufferBuilder.vertex(matrix, x1, y1, z1).color(1f, 1f, 1f, 1f).uv(maxU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(1, 0, 0).endVertex();
    }

    private static void addTopVertex(VertexConsumer bufferBuilder, Matrix4f matrix, Matrix4f oldMatrix, float x1,
            float y1, float z1, float x2, float y2, float z2, int part, int light) {
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
        float vPerPart = deltaV / PART_COUNT;
        maxV = minV + (vPerPart * (part + 1));
        minV = minV + (vPerPart * part);

        bufferBuilder.vertex(oldMatrix, x1, y2, z1).color(1f, 1f, 1f, 1f).uv(maxU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 1, 0).endVertex();
        bufferBuilder.vertex(oldMatrix, x2, y2, z1).color(1f, 1f, 1f, 1f).uv(minU, maxV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 1, 0).endVertex();
        bufferBuilder.vertex(matrix, x2, y1, z2).color(1f, 1f, 1f, 1f).uv(minU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 1, 0).endVertex();
        bufferBuilder.vertex(matrix, x1, y1, z2).color(1f, 1f, 1f, 1f).uv(maxU, minV)
                .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(0, 1, 0).endVertex();
    }

    private static VanillaCapeRenderer vanillaCape = new VanillaCapeRenderer();

    private CapeRenderer getCapeRenderer(AbstractClientPlayer abstractClientPlayer,
            MultiBufferSource multiBufferSource) {
        for (ModSupport support : SupportManager.getSupportedMods()) {
            if (support.shouldBeUsed(abstractClientPlayer)) {
                return support.getRenderer();
            }
        }
        if (NMSUtil.getPlayerCape(abstractClientPlayer) == null || abstractClientPlayer.isInvisible()
                || !abstractClientPlayer.isModelPartShown(PlayerModelPart.CAPE)) {
            return null;
        } else {
            vanillaCape.vertexConsumer = multiBufferSource
                    .getBuffer(RenderType.entityCutout(NMSUtil.getPlayerCape(abstractClientPlayer)));
            return vanillaCape;
        }
    }

    /**
     * https://easings.net/#easeOutSine
     * 
     * @param x
     * @return
     */
    private static float easeOutSine(float x) {
        return Mth.sin((float) ((x * Math.PI) / 2f));
    }

}
