package dev.tr7zw.waveycapes.renderlayers;

import dev.tr7zw.waveycapes.CapeHolder;
import dev.tr7zw.waveycapes.CapeMovement;
import dev.tr7zw.waveycapes.CapeStyle;
import dev.tr7zw.waveycapes.WaveyCapesBase;
import dev.tr7zw.waveycapes.WindMode;
import dev.tr7zw.waveycapes.sim.BasicSimulation;
import dev.tr7zw.waveycapes.sim.CapePoint;
import dev.tr7zw.waveycapes.sim.Vector3;
import dev.tr7zw.waveycapes.util.Mth;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.MathHelper;

public class CustomCapeRenderLayer implements LayerRenderer<AbstractClientPlayer> {

    static final int partCount = 16;
    private ModelRenderer[] customCape = new ModelRenderer[partCount];
    private final RenderPlayer playerRenderer;
    private SmoothCapeRenderer smoothCapeRenderer = new SmoothCapeRenderer();

    public CustomCapeRenderLayer(RenderPlayer playerRenderer) {
        this.playerRenderer = playerRenderer;
        buildMesh();
    }

    private void buildMesh() {
        ModelBase dummyModel = new ModelBase() {};
        customCape = new ModelRenderer[partCount];
        for (int i = 0; i < partCount; i++) {
            ModelRenderer base = new ModelRenderer(dummyModel, 0, i);
            base.setTextureSize(64, 32);
            this.customCape[i] = base.addBox(-5.0F, (float)i, -1.0F, 10, 1, 1);
        }
    }

    @Override
    public void doRenderLayer(AbstractClientPlayer abstractClientPlayer, float paramFloat1, float paramFloat2, float deltaTick,
            float animationTick, float paramFloat5, float paramFloat6, float paramFloat7) {
        if(abstractClientPlayer.isInvisible())return;

        if (!abstractClientPlayer.hasPlayerInfo() || abstractClientPlayer.isInvisible()
                || !abstractClientPlayer.isWearing(EnumPlayerModelParts.CAPE)
                || abstractClientPlayer.getLocationCape() == null) {
            return;
        }

        if(WaveyCapesBase.config.capeMovement != CapeMovement.VANILLA) {
            CapeHolder holder = (CapeHolder) abstractClientPlayer;
            holder.updateSimulation(abstractClientPlayer, partCount);
        }
        this.playerRenderer.bindTexture(abstractClientPlayer.getLocationCape());

        if (WaveyCapesBase.config.capeStyle == CapeStyle.SMOOTH) {
            smoothCapeRenderer.renderSmoothCape(this, abstractClientPlayer, deltaTick);
        } else {
            ModelRenderer[] parts = customCape;
            for (int part = 0; part < partCount; part++) {
                ModelRenderer model = parts[part];
                GlStateManager.pushMatrix();
                modifyPoseStack(abstractClientPlayer, deltaTick, part);
                model.render(0.0625F);
                GlStateManager.popMatrix();
            }
        }
    }

    private void modifyPoseStack(AbstractClientPlayer abstractClientPlayer, float h, int part) {
        if(WaveyCapesBase.config.capeMovement != CapeMovement.VANILLA) {
            modifyPoseStackSimulation(abstractClientPlayer, h, part);
            return;
        }
        modifyPoseStackVanilla(abstractClientPlayer, h, part);
    }

    private void modifyPoseStackSimulation(AbstractClientPlayer abstractClientPlayer, float delta, int part) {
        BasicSimulation simulation = ((CapeHolder)abstractClientPlayer).getSimulation();
        if (simulation == null || simulation.empty()) return;
        java.util.List<CapePoint> points = simulation.getPoints();
        GlStateManager.translate(0.0D, 0.0D, 0.125D);

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
            GlStateManager.translate(0, 0.15F, 0);
        }

        float naturalWindSwing = getNatrualWindSwing(part);


        // vanilla rotating and wind
        GlStateManager.rotate(6.0F + height + naturalWindSwing, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(sidewaysRotationOffset / 2.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(180.0F - sidewaysRotationOffset / 2.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-z/partCount, y/partCount, x/partCount); // movement from the simulation
        //offsetting so the rotation is on the cape part
        GlStateManager.translate(0, (0.48/16) , - (0.48/16)); // (0.48/16)
        GlStateManager.translate(0, part * 1f/partCount, part * (0)/partCount);
        GlStateManager.rotate(-partRotation, 1.0F, 0.0F, 0.0F);
        // undoing the rotation
        GlStateManager.translate(0, -part * 1f/partCount, -part * (0)/partCount);
        GlStateManager.translate(0, -(0.48/16), (0.48/16));

    }

    private float getRotation(java.util.List<CapePoint> points, int part, float delta) {
        if (part == partCount - 1) {
            return getRotation(points, part - 1, delta);
        }
        return (float) getAngle(points.get(part).getLerpedPos(delta),
                points.get(part + 1).getLerpedPos(delta));
    }

    private double getAngle(Vector3 a, Vector3 b) {
        Vector3 angle = b.subtract(a);
        return Math.toDegrees(Math.atan2(angle.x, angle.y)) + 180;
    }

    void modifyPoseStackVanilla(AbstractClientPlayer abstractClientPlayer, float h, int part) {
        GlStateManager.translate(0.0D, 0.0D, 0.125D);
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
        float swing = (float) (d * o + m * p) * easeOutSine(1.0F/partCount*part)*100;
        swing = MathHelper.clamp_float(swing, 0.0F, 150.0F * easeOutSine(1F/partCount*part));
        float sidewaysRotationOffset = (float) (d * p - m * o) * 100.0F;
        sidewaysRotationOffset = MathHelper.clamp_float(sidewaysRotationOffset, -20.0F, 20.0F);
        float t = Mth.lerp(h, abstractClientPlayer.prevCameraYaw, abstractClientPlayer.cameraYaw);
        height += Math.sin(Mth.lerp(h, abstractClientPlayer.prevDistanceWalkedModified, abstractClientPlayer.distanceWalkedModified) * 6.0F) * 32.0F * t;
        if (abstractClientPlayer.isSneaking()) {
            height += 25.0F;
            GlStateManager.translate(0, 0.15F, 0);
        }

        float naturalWindSwing = getNatrualWindSwing(part);

        GlStateManager.rotate(6.0F + swing / 2.0F + height + naturalWindSwing, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(sidewaysRotationOffset / 2.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(-sidewaysRotationOffset / 2.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
    }

    float getNatrualWindSwing(int part) {
        if (WaveyCapesBase.config.windMode == WindMode.WAVES) {
            long highlightedPart = (System.currentTimeMillis() / 3) % 360;
            float relativePart = (float) (part + 1) / partCount;
            return (float) (Math.sin(Math.toRadians((relativePart) * 360 - (highlightedPart))) * 3);
        }
        return 0;
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

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }

}
