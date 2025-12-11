package dev.tr7zw.waveycapes;

import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper;
import net.minecraft.client.renderer.MultiBufferSource;
//? if >= 1.21.11 {

import net.minecraft.client.renderer.rendertype.*;
//? } else {
/*
import net.minecraft.client.renderer.*;
*///? }
import net.minecraft.resources.*;

public class VanillaCapeRenderer implements CapeRenderer {

    @Override
    public VertexConsumer getVertexConsumer(MultiBufferSource multiBufferSource, PlayerWrapper capeRenderInfo) {
        /*? >= 1.21.11 {*/ Identifier /*?} else {*//* ResourceLocation *//*?}*/ cape = capeRenderInfo.getCapeTexture();
        if (cape != null) {
            //? if >= 1.21.11 {

            return multiBufferSource.getBuffer(RenderTypes.entityTranslucent(cape));
            //? } else {
            /*
            return multiBufferSource.getBuffer(RenderType.entityTranslucent(cape));
            *///? }
        }
        return null;
    }

    @Override
    public boolean vanillaUvValues() {
        return true;
    }

}
