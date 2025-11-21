package dev.tr7zw.waveycapes.renderlayers;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper;
import dev.tr7zw.waveycapes.WaveyCapesBase;
import net.minecraft.client.Minecraft;
//? if >= 1.21.11 {
import net.minecraft.client.model.player.*;
//?} else {
/*import net.minecraft.client.model.*;*/
//?}
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

//? if >= 1.21.9 {

import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
//? } else {
/*
 import net.minecraft.client.renderer.MultiBufferSource;
*///? }
   //? if < 1.21.3 {
   /*
    import net.minecraft.world.entity.player.PlayerModelPart;
   *///? }

//? if < 1.21.2 {
/*
 import net.minecraft.util.Mth;
 import net.minecraft.client.player.AbstractClientPlayer;
*///? }

//? if >= 1.21.9 {

public class CustomCapeRenderLayer
        extends RenderLayer<net.minecraft.client.renderer.entity.state.AvatarRenderState, PlayerModel> {
    //? } else if >= 1.21.2 {
    /*
     public class CustomCapeRenderLayer extends RenderLayer<net.minecraft.client.renderer.entity.state.PlayerRenderState, PlayerModel> {
    *///? } else {
    /*
     public class CustomCapeRenderLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    *///? }

    //? if >= 1.21.9 {

    public CustomCapeRenderLayer(
            RenderLayerParent<net.minecraft.client.renderer.entity.state.AvatarRenderState, PlayerModel> renderLayerParent) {
        super(renderLayerParent);
    }
    //? } else if >= 1.21.2 {
    /*
     public CustomCapeRenderLayer(RenderLayerParent<net.minecraft.client.renderer.entity.state.PlayerRenderState, PlayerModel> renderLayerParent) {
        super(renderLayerParent);
     }
    *///? } else {
    /*
     public CustomCapeRenderLayer(
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
                net.minecraft.client.renderer.entity.state.PlayerRenderState renderState, float yRot, float xRot) {
            PlayerWrapper capeRenderInfo = new PlayerWrapper(renderState);
                 float delta = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
        *///? } else {
        /*
         public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight,
                AbstractClientPlayer abstractClientPlayer, float f, float g, float delta, float j, float k, float l) {
            PlayerWrapper capeRenderInfo = new PlayerWrapper(abstractClientPlayer);
        *///? }
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

        //? if >= 1.21.9 {

        PoseStack stack = new PoseStack();
        stack.last().set(poseStack.last());
        WaveyCapesBase.INSTANCE.getCapeNodeCollector().submitCape(renderState, stack, packedLight);
        //? } else {
        /*
         WaveyCapesBase.INSTANCE.getRenderer().render(capeRenderInfo, poseStack, multiBufferSource, packedLight, delta);
        *///? }
        poseStack.popPose();
    }

}
