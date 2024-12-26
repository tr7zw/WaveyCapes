package dev.tr7zw.waveycapes.mixin;

//#if MC >= 12102
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
//#endif
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.tr7zw.waveycapes.support.ModSupport;
import dev.tr7zw.waveycapes.support.SupportManager;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;

@Mixin(LivingEntityRenderer.class)
//#if MC >= 12102
public class LivingEntityRendererMixin<S extends LivingEntity, T extends LivingEntityRenderState, M extends EntityModel<? super T>> {
//#else
//$$public class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {
//#endif

    @Inject(method = "addLayer", at = @At("HEAD"), cancellable = true)
    private void addLayer(RenderLayer<T, M> renderLayer, CallbackInfoReturnable<Boolean> info) {
        if ((Object) renderLayer instanceof CapeLayer) {
            info.cancel();
            return;
        }
        for (ModSupport support : SupportManager.getSupportedMods())
            if (support.blockFeatureRenderer(renderLayer)) {
                info.cancel();
                return;
            }
    }

}
