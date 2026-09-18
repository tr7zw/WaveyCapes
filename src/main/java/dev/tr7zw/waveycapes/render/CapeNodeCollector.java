package dev.tr7zw.waveycapes.render;

import com.mojang.blaze3d.vertex.*;
import dev.tr7zw.transition.mc.entitywrapper.*;
import dev.tr7zw.waveycapes.*;
import dev.tr7zw.waveycapes.support.*;
import net.minecraft.client.player.*;
import net.minecraft.client.renderer.*;
//? if >= 1.21.2 {
import net.minecraft.client.renderer.entity.state.*;
//? }
//? if >= 1.21.11 {

import net.minecraft.client.renderer.feature.*;
import net.minecraft.client.renderer.rendertype.*;
//? } else {

/*import net.minecraft.client.renderer.*;
*///? }

public class CapeNodeCollector {

    private final CustomCapeRenderer customCapeRenderer = new CustomCapeRenderer();
    private final VanillaCapeRenderer vanillaCape = new VanillaCapeRenderer();

    public void submitCape(
            //? if >= 1.21.9 {

            SubmitNodeCollector submitNodeCollector,
            //? } else {

            /*MultiBufferSource multiBufferSource,
            *///? }
            PlayerWrapper playerWrapper, PoseStack stack, int packedLight, float delta) {
        var renderer = getCapeRenderer(playerWrapper);
        if (renderer == null) {
            return;
        }
        var capeInfo = renderer.getCapeInfo(playerWrapper);
        if (capeInfo == null) {
            return;
        }
        //? if >= 26.3 {

        if (capeInfo.isGlint()) {
            submitNodeCollector.submitCustomGeometry(stack, RenderTypes.trimmedArmorGlint(), (pose, vertexConsumer) -> {
                PoseStack sharedStack = new PoseStack();
                sharedStack.last().set(pose);
                customCapeRenderer.render(playerWrapper, renderer, vertexConsumer, sharedStack, packedLight, delta);
            });
        }

        submitNodeCollector.submitCustomGeometry(stack, capeInfo.renderType(), (pose, vertexConsumer) -> {
            PoseStack sharedStack = new PoseStack();
            sharedStack.last().set(pose);
            customCapeRenderer.render(playerWrapper, renderer, vertexConsumer, sharedStack, packedLight, delta);
        });
        //? } else if >= 1.21.9 {
        /*
        if (capeInfo.isGlint()) {
            submitNodeCollector.submitCustomGeometry(stack, RenderTypes.entityGlint(), (pose, vertexConsumer) -> {
                PoseStack sharedStack = new PoseStack();
                sharedStack.last().set(pose);
                customCapeRenderer.render(playerWrapper, renderer, vertexConsumer, sharedStack, packedLight, delta);
            });
        }
        
        submitNodeCollector.submitCustomGeometry(stack, capeInfo.renderType(), (pose, vertexConsumer) -> {
            PoseStack sharedStack = new PoseStack();
            sharedStack.last().set(pose);
            customCapeRenderer.render(playerWrapper, renderer, vertexConsumer, sharedStack, packedLight, delta);
        });
         */
        //? } else {

        /*VertexConsumer vertexConsumer;
        if (capeInfo.isGlint()) {
            vertexConsumer = net.minecraft.client.renderer.entity.ItemRenderer.getFoilBuffer(multiBufferSource, capeInfo.renderType(), false, true);
        } else {
            vertexConsumer = multiBufferSource.getBuffer(capeInfo.renderType());
        }
        customCapeRenderer.render(playerWrapper, renderer, vertexConsumer, stack, packedLight, delta);
        *///? }
    }

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
