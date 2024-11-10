package dev.tr7zw.waveycapes.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.waveycapes.versionless.CapeHolder;
import dev.tr7zw.waveycapes.versionless.ExtendedPlayerRenderState;
import dev.tr7zw.waveycapes.renderlayers.CustomCapeRenderLayer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;

// spotless:off
//#if MC >= 12102
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
//#endif
//#if MC < 11700
//$$import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//$$ import net.minecraft.client.renderer.MultiBufferSource;
//#else
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
//#endif
//spotless:on

@Mixin(PlayerRenderer.class)
//spotless:off
//#if MC >= 12102
public abstract class PlayerRendererMixin
        extends LivingEntityRenderer<AbstractClientPlayer, PlayerRenderState, PlayerModel> {
//#else
//$$public abstract class PlayerRendererMixin
//$$        extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
//#endif

    @Unique
    private boolean injectedCape = false;

    // spotless:off
    //#if MC >= 12102
    public PlayerRendererMixin(Context context, PlayerModel entityModel, float f) {
        super(context, entityModel, f);
    }
    //#elseif MC >= 11700
    //$$public PlayerRendererMixin(Context context, PlayerModel<AbstractClientPlayer> entityModel, float f) {
    //$$    super(context, entityModel, f);
    //$$}
    //#else
    //$$public PlayerRendererMixin(EntityRenderDispatcher entityRenderDispatcher,
    //$$        PlayerModel<AbstractClientPlayer> entityModel, float f) {
    //$$    super(entityRenderDispatcher, entityModel, f);
    //$$}
    //#endif
    //spotless:on

    @Inject(method = "<init>*", at = @At("RETURN"))
    public void onCreate(CallbackInfo info) {
        injectedCape = true;
        addLayer(new CustomCapeRenderLayer(this));
    }

    //spotless:off
    //#if MC >= 12102
    @Inject(method = "extractRenderState(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;F)V", at = @At("HEAD"))
    private void addCapeHolder(AbstractClientPlayer abstractClientPlayer, PlayerRenderState playerRenderState, float f, CallbackInfo ci) {
        ExtendedPlayerRenderState extendedPlayerRenderState = (ExtendedPlayerRenderState) playerRenderState;
        extendedPlayerRenderState.setCapeHolder((CapeHolder) abstractClientPlayer);
        extendedPlayerRenderState.setUnderwater(abstractClientPlayer.isUnderWater());
    }
    //#endif
    //spotless:on

    // Dirty 1.16 workaround for slim skins for whatever reason not working right
    // spotless:off
    //#if MC < 11700
    //$$@Inject(method = "render", at = @At("HEAD"))
    //$$public void renderLegacyWorkaround(AbstractClientPlayer abstractClientPlayer, float f, float g, PoseStack poseStack,
    //$$        MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
    //$$    if(!injectedCape) {
    //$$        addLayer(new CustomCapeRenderLayer(this));
    //$$        injectedCape = true;
    //$$    }
    //$$}
    //#endif
    //spotless:on

}
