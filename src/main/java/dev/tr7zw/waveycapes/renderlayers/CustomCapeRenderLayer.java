package dev.tr7zw.waveycapes.renderlayers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import dev.tr7zw.waveycapes.accessor.PlayerEntityModelAccessor;
import dev.tr7zw.waveycapes.sim.StickSimulation;
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
    public CustomCapeRenderLayer(
            RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderLayerParent) {
        super(renderLayerParent);
    }

    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i,
            AbstractClientPlayer abstractClientPlayer, float f, float g, float h, float j, float k, float l) {
        if (!abstractClientPlayer.isCapeLoaded() || abstractClientPlayer.isInvisible()
                || !abstractClientPlayer.isModelPartShown(PlayerModelPart.CAPE)
                || abstractClientPlayer.getCloakTextureLocation() == null)
            return;
        ItemStack itemStack = abstractClientPlayer.getItemBySlot(EquipmentSlot.CHEST);
        if (itemStack.is(Items.ELYTRA))
            return;
        ModelPart[] parts = ((PlayerEntityModelAccessor) getParentModel()).getCustomCapeParts();
        StickSimulation simulation = ((PlayerEntityModelAccessor) getParentModel()).getSimulation();
        simulation.simulate();
        for (int part = 0; part < 16; part++) {
            ModelPart model = parts[part];
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
            if(part == 0) {
                simulation.points.get(0).position.x += (d * o + m * p);
                simulation.points.get(0).position.y = (float) (Mth.lerp(h, abstractClientPlayer.yo, abstractClientPlayer.getY())*4 + (abstractClientPlayer.isCrouching() ? -4 : 0));
            }
            float swing = 0;
            model.z = simulation.points.get(part).position.x - simulation.points.get(0).position.x;
            if(model.z > 0) {
                model.z = 0;
            }
            model.y = simulation.points.get(part).position.y - simulation.points.get(0).position.y - part;
            float sidewaysRotationOffset = (float) (d * p - m * o) * 100.0F;
            sidewaysRotationOffset = Mth.clamp(sidewaysRotationOffset, -20.0F, 20.0F);
            float t = Mth.lerp(h, abstractClientPlayer.oBob, abstractClientPlayer.bob);
            height += Mth.sin(Mth.lerp(h, abstractClientPlayer.walkDistO, abstractClientPlayer.walkDist) * 6.0F) * 32.0F * t;
            if (abstractClientPlayer.isCrouching()) {
                height += 25.0F;
                poseStack.translate(0, 0.2F, 0);
            }
            poseStack.mulPose(Vector3f.XP.rotationDegrees(6.0F + swing / 2.0F + height));
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(sidewaysRotationOffset / 2.0F));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - sidewaysRotationOffset / 2.0F));
            VertexConsumer vertexConsumer = multiBufferSource
                    .getBuffer(RenderType.entityCutout(abstractClientPlayer.getCloakTextureLocation()));
            model.render(poseStack, vertexConsumer,  i, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
        //System.out.println((simulation.points.get(0).position.x - simulation.points.get(15).position.x) + " " + simulation.points.get(15).position.x + " " + simulation.points.get(0).position.x);
    }
    
    private static int scale = 1000*60*60;
    
    /**
     * Returns between 0 and 2
     * 
     * @param posY
     * @return
     */
    private static float getWind(double posY) {
        float x = (System.currentTimeMillis()%scale)/10000f;
        float mod = Mth.clamp(1f/200f*(float)posY, 0f, 1f);
        return Mth.clamp((float) (Math.sin(2 * x) + Math.sin(Math.PI * x)) * mod, 0, 2);
    }
    
    
    /**
     * https://easings.net/#easeOutSine
     * 
     * @param x
     * @return
     */
    private static float easeOutSine(float x) {
        return Mth.sin((x * Mth.PI) / 2f);

      }
    
}