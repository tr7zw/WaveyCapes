package dev.tr7zw.waveycapes.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.waveycapes.versionless.CapeHolder;
import dev.tr7zw.waveycapes.renderlayers.CustomCapeRenderLayer;
//? if >= 1.21.11 {

import net.minecraft.client.model.player.*;
//? } else {
/*
import net.minecraft.client.model.*;
*///? }
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;

//? if >= 1.21.2 {

//? }
//? if < 1.17.0 {
/*
 import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
 import com.mojang.blaze3d.vertex.PoseStack;
 import net.minecraft.client.renderer.MultiBufferSource;
*///? } else {

import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
//? }

//? if >= 1.21.9 {

@Mixin(net.minecraft.client.renderer.entity.player.AvatarRenderer.class)
//? } else {
/*
 @Mixin(net.minecraft.client.renderer.entity.player.PlayerRenderer.class)
*///? }
   //? if >= 1.21.2 {

public abstract class PlayerRendererMixin
        //? if >= 1.21.9 {

        extends
        LivingEntityRenderer<AbstractClientPlayer, net.minecraft.client.renderer.entity.state.AvatarRenderState, PlayerModel> {
    //? } else {
    /*
            extends LivingEntityRenderer<AbstractClientPlayer, net.minecraft.client.renderer.entity.state.PlayerRenderState, PlayerModel> {
    *///? }
       //? } else {
       /*
        public abstract class PlayerRendererMixin
               extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
       *///? }

    @Unique
    private boolean injectedCape = false;

    //? if >= 1.21.2 {

    public PlayerRendererMixin(Context context, PlayerModel entityModel, float f) {
        super(context, entityModel, f);
    }
    //? } else if >= 1.17.0 {
    /*
     public PlayerRendererMixin(Context context, PlayerModel<AbstractClientPlayer> entityModel, float f) {
        super(context, entityModel, f);
     }
    *///? } else {
    /*
     public PlayerRendererMixin(EntityRenderDispatcher entityRenderDispatcher,
            PlayerModel<AbstractClientPlayer> entityModel, float f) {
        super(entityRenderDispatcher, entityModel, f);
     }
    *///? }

    @Inject(method = "<init>*", at = @At("RETURN"))
    public void onCreate(CallbackInfo info) {
        injectedCape = true;
        addLayer(new CustomCapeRenderLayer(this));
    }

    // Dirty 1.16 workaround for slim skins for whatever reason not working right
    //? if < 1.17.0 {
    /*
     @Inject(method = "render", at = @At("HEAD"))
     public void renderLegacyWorkaround(AbstractClientPlayer abstractClientPlayer, float f, float g, PoseStack poseStack,
            MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        if(!injectedCape) {
            addLayer(new CustomCapeRenderLayer(this));
            injectedCape = true;
        }
     }
    *///? }

}
