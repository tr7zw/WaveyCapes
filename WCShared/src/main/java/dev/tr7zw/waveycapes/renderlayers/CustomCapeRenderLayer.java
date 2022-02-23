package dev.tr7zw.waveycapes.renderlayers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import dev.tr7zw.waveycapes.CapeRenderer;
import dev.tr7zw.waveycapes.VanillaCapeRenderer;
import dev.tr7zw.waveycapes.WaveyCapesBase;
import dev.tr7zw.waveycapes.WindMode;
import dev.tr7zw.waveycapes.support.ModSupport;
import dev.tr7zw.waveycapes.support.SupportManager;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
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
    
    private int partCount = 16;
    private ModelPart[] customCape = new ModelPart[partCount];
    
    public CustomCapeRenderLayer(
            RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderLayerParent) {
        super(renderLayerParent);
        partCount = WaveyCapesBase.config.capeParts;
        buildMesh();
    }
    
    private void buildMesh() {
        customCape = new ModelPart[partCount];
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        for (int i = 0; i < partCount; i++)
            partDefinition.addOrReplaceChild("customCape_" + i,
                    CubeListBuilder.create().texOffs(0, (int) (i * (16f / partCount)))
                            .addBox(-5.0F, i * (16f / partCount), -1.0F, 10.0F, (16f / partCount), 1.0F, CubeDeformation.NONE, 1.0F, 0.5F),
                    PartPose.offset(0.0F, 0.0F, 0.0F));  
        ModelPart modelPart = partDefinition.bake(64,64);
        for (int i = 0; i < partCount; i++) {
            this.customCape[i] = modelPart.getChild("customCape_" + i);
        }
    }

    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i,
            AbstractClientPlayer abstractClientPlayer, float f, float g, float h, float j, float k, float l) {
        CapeRenderer renderer = getCapeRenderer(abstractClientPlayer, multiBufferSource);
        if(renderer == null) return;
        ItemStack itemStack = abstractClientPlayer.getItemBySlot(EquipmentSlot.CHEST);
        if (itemStack.is(Items.ELYTRA))
            return;
        if(partCount != WaveyCapesBase.config.capeParts) {
            partCount = WaveyCapesBase.config.capeParts;
            buildMesh();
        }
        ModelPart[] parts = customCape;
        for (int part = 0; part < partCount; part++) {
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
            float swing = (float) (d * o + m * p) * easeOutSine(1.0F/partCount*part)*100;
            swing += getWind(abstractClientPlayer.getY()) * 35f * easeOutSine(1F/partCount*part);
            swing = Mth.clamp(swing, 0.0F, 150.0F * easeOutSine(1F/partCount*part));
            float sidewaysRotationOffset = (float) (d * p - m * o) * 100.0F;
            sidewaysRotationOffset = Mth.clamp(sidewaysRotationOffset, -20.0F, 20.0F);
            float t = Mth.lerp(h, abstractClientPlayer.oBob, abstractClientPlayer.bob);
            height += Mth.sin(Mth.lerp(h, abstractClientPlayer.walkDistO, abstractClientPlayer.walkDist) * 6.0F) * 32.0F * t;
            if (abstractClientPlayer.isCrouching()) {
                height += 25.0F;
                poseStack.translate(0, 0.15F, 0);
            }
            
            long highlightedPart = (System.currentTimeMillis() / 100) % partCount;

            float maxSwing = 12f;
            float swingSteps = 1f;
            float naturalWindSwing = Math.max(maxSwing - (Math.abs(highlightedPart - part) * swingSteps), Math.max(maxSwing - (Math.abs((highlightedPart + partCount) - part) * swingSteps), maxSwing - (Math.abs((highlightedPart - partCount) - part) * swingSteps)));
            if (naturalWindSwing < 0) {
                naturalWindSwing = 0;
            }
            //TODO: clean this logic up
            if(WaveyCapesBase.config.windMode == WindMode.NONE) {
                naturalWindSwing = 0;
            }
            
            poseStack.mulPose(Vector3f.XP.rotationDegrees(6.0F + swing / 2.0F + height + naturalWindSwing));
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(sidewaysRotationOffset / 2.0F));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - sidewaysRotationOffset / 2.0F));
            renderer.render(abstractClientPlayer, part, model, poseStack, multiBufferSource, i, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
    }

    private static VanillaCapeRenderer vanillaCape = new VanillaCapeRenderer();
    
    private CapeRenderer getCapeRenderer(AbstractClientPlayer abstractClientPlayer, MultiBufferSource multiBufferSource) {
        for(ModSupport support : SupportManager.getSupportedMods()) {
            if(support.shouldBeUsed(abstractClientPlayer)) {
                return support.getRenderer();
            }
        }
        if (!abstractClientPlayer.isCapeLoaded() || abstractClientPlayer.isInvisible()
                || !abstractClientPlayer.isModelPartShown(PlayerModelPart.CAPE)
                || abstractClientPlayer.getCloakTextureLocation() == null) {
            return null;
        } else {
            vanillaCape.vertexConsumer = multiBufferSource
            .getBuffer(RenderType.entityCutout(abstractClientPlayer.getCloakTextureLocation()));
            return vanillaCape;
        }
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
