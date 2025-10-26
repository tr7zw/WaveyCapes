package dev.tr7zw.waveycapes.renderlayers;

//#if MC >= 11903
import org.joml.Matrix4f;
import org.joml.Vector4f;
//#else
//$$import com.mojang.math.Matrix4f;
//$$import com.mojang.math.Vector4f;
//#endif

//#if MC >= 12102
import net.minecraft.client.Minecraft;
//#endif

//#if MC < 12105
//$$import com.mojang.blaze3d.systems.RenderSystem;
//#endif
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.tr7zw.transition.mc.MathUtil;
import dev.tr7zw.transition.mc.VertexConsumerUtil;
import dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper;
import dev.tr7zw.waveycapes.CapeRenderer;
import dev.tr7zw.waveycapes.NMSUtil;
import dev.tr7zw.waveycapes.VanillaCapeRenderer;
import dev.tr7zw.waveycapes.WaveyCapesBase;
import dev.tr7zw.waveycapes.support.ModSupport;
import dev.tr7zw.waveycapes.support.SupportManager;
import dev.tr7zw.waveycapes.versionless.CapeHolder;
import dev.tr7zw.waveycapes.versionless.CapeMovement;
import dev.tr7zw.waveycapes.versionless.CapeStyle;
import dev.tr7zw.waveycapes.versionless.ModBase;
import dev.tr7zw.waveycapes.versionless.WindMode;
import dev.tr7zw.waveycapes.versionless.sim.BasicSimulation;
import dev.tr7zw.waveycapes.versionless.util.Vector3;
import dev.tr7zw.waveycapes.versionless.util.Vector4;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

import net.minecraft.client.renderer.texture.OverlayTexture;
//#if MC >= 12109
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
//#else
//$$import net.minecraft.client.renderer.MultiBufferSource;
//#endif
//#if MC < 12103
//$$import net.minecraft.world.entity.player.PlayerModelPart;
//#endif

//#if MC < 12102
//$$import net.minecraft.util.Mth;
//$$import net.minecraft.client.player.AbstractClientPlayer;
//#endif

//#if MC >= 12109
public class CustomCapeRenderLayer
        extends RenderLayer<net.minecraft.client.renderer.entity.state.AvatarRenderState, PlayerModel> {
    //#elseif MC >= 12102
    //$$public class CustomCapeRenderLayer extends RenderLayer<net.minecraft.client.renderer.entity.state.PlayerRenderState, PlayerModel> {
    //#else
    //$$public class CustomCapeRenderLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    //#endif

    //#if MC >= 12109
    public CustomCapeRenderLayer(
            RenderLayerParent<net.minecraft.client.renderer.entity.state.AvatarRenderState, PlayerModel> renderLayerParent) {
        super(renderLayerParent);
    }
    //#elseif MC >= 12102
    //$$public CustomCapeRenderLayer(RenderLayerParent<net.minecraft.client.renderer.entity.state.PlayerRenderState, PlayerModel> renderLayerParent) {
    //$$    super(renderLayerParent);
    //$$}
    //#else
    //$$public CustomCapeRenderLayer(
    //$$        RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderLayerParent) {
    //$$    super(renderLayerParent);
    //$$}
    //#endif

    //#if MC >= 12109
    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight,
            AvatarRenderState renderState, float f, float g) {
        PlayerWrapper capeRenderInfo = new PlayerWrapper(renderState);
        //#elseif MC >= 12102
        //$$public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight,
        //$$        net.minecraft.client.renderer.entity.state.PlayerRenderState renderState, float yRot, float xRot) {
        //$$    PlayerWrapper capeRenderInfo = new PlayerWrapper(renderState);
        //#else
        //$$public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight,
        //$$        AbstractClientPlayer abstractClientPlayer, float f, float g, float delta, float j, float k, float l) {
        //$$    PlayerWrapper capeRenderInfo = new PlayerWrapper(abstractClientPlayer);
        //#endif
        if (capeRenderInfo.isPlayerInvisible())
            return;
        if (capeRenderInfo.hasElytraEquipped())
            return;

        if (!capeRenderInfo.isCapeVisible()) {
            return;
        }

        poseStack.pushPose();
        getParentModel().body.translateAndRotate(poseStack);

        if (capeRenderInfo.hasChestplateEquipped()) {
            poseStack.translate(0.0F, -0.053125F, 0.06875F);
        }

        //#if MC >= 12109
        PoseStack stack = new PoseStack();
        stack.last().set(poseStack.last());
        WaveyCapesBase.INSTANCE.getCapeNodeCollector().submitCape(renderState, stack, packedLight);
        //#else
        //$$WaveyCapesBase.INSTANCE.getRenderer().render(capeRenderInfo, poseStack, multiBufferSource, packedLight);
        //#endif
        poseStack.popPose();
    }

}
