package dev.tr7zw.waveycapes;

import net.minecraft.client.model.geom.ModelPart;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;

import java.util.function.IntUnaryOperator;

//spotless:off
//#if MC >= 11700
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
//#endif
//#if MC >= 11903
import org.joml.Quaternionf;
//#else
//$$import com.mojang.math.Quaternion;
//#endif
//spotless:on

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

public class NMSUtil {

    public static ResourceLocation getPlayerCape(AbstractClientPlayer player) {
        // spotless:off
        //#if MC >= 12002
        return player.getSkin().capeTexture();
        //#else
        //$$ return player.getCloakTextureLocation();
        //#endif
        //spotless:on
    }

    // spotless:off
    //#if MC >= 11903
    public static void conjugate(Quaternionf quaternion2) {
    quaternion2.conjugate();
    //#else
    //$$public static void conjugate(Quaternion quaternion2) {
    //$$ quaternion2.conj();
    //#endif
    // spotless:on
    }

    public static ModelPart[] buildCape(int texWidth, int texHight, IntUnaryOperator uvX, IntUnaryOperator uvY) {
        ModelPart[] customCape = new ModelPart[16];
        // spotless:off
        //#if MC >= 11700
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        for (int i = 0; i < 16; i++)
            partDefinition.addOrReplaceChild(
                    "customCape_" + i, CubeListBuilder.create().texOffs(uvX.applyAsInt(i), uvY.applyAsInt(i))
                            .addBox(-5.0F, i, -1.0F, 10.0F, 1, 1.0F, CubeDeformation.NONE, 1.0F, 0.5F),
                    PartPose.offset(0.0F, 0.0F, 0.0F));
        ModelPart modelPart = partDefinition.bake(texWidth, texHight);
        for (int i = 0; i < 16; i++) {
            customCape[i] = modelPart.getChild("customCape_" + i);
        }
        //#else
        //$$for (int i = 0; i < 16; i++) {
        //$$    ModelPart base = new ModelPart(64, 32, 0, i);
        //$$    customCape[i] = base.addBox(-5.0F, (float)i, -1.0F, 10.0F, 1.0F, 1F);
        //$$}
        //#endif
        // spotless:on
        return customCape;
    }

    public static void prepareViewMatrix(double xpos, double ypos) {
        // spotless:off
        //#if MC >= 11700
        RenderSystem.applyModelViewMatrix();
        //#else
        //$$ RenderSystem.pushMatrix();
        //$$ RenderSystem.translatef((float)xpos, (float)ypos, 1050.0F);
        //$$ RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        //#endif
        // spotless:on
    }

    public static void resetViewMatrix() {
        // spotless:off
        //#if MC >= 11700
        RenderSystem.applyModelViewMatrix();
        //#else
        //$$ RenderSystem.popMatrix();
        //#endif
        // spotless:on
    }

    public static void prepareLighting() {
        // spotless:off
        //#if MC >= 11700
        Lighting.setupForEntityInInventory();
        //#else
        //$$ Lighting.setupForFlatItems();
        //#endif
        // spotless:on
    }
//PreviewHelper.renderEntityInInventoryFollowsMouse(guiGraphics, x, y, size, 0, 0, this.minecraft.player);

    public static PoseStack getPoseStack() {
        // spotless:off
        //#if MC >= 11700 && MC <= 12004
        //$$ return RenderSystem.getModelViewStack();
        //#else
         return new PoseStack();
        //#endif
        // spotless:on
    }

}
