package dev.tr7zw.waveycapes.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import org.spongepowered.asm.mixin.Mixin;

//#if MC >= 12109
import dev.tr7zw.transition.mc.entitywrapper.PlayerWrapper;
import dev.tr7zw.waveycapes.CapeNodeCollector;
import dev.tr7zw.waveycapes.WaveyCapesBase;
import dev.tr7zw.waveycapes.WaveyCapesMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FeatureRenderDispatcher.class)
public class FeatureRenderDispatcherMixin {
    @Shadow
    @Final
    private MultiBufferSource.BufferSource bufferSource;

    @Inject(method = "renderAllFeatures", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeStorage;clear()V"))
    private void renderCapes(CallbackInfo ci) {
        CapeNodeCollector collector = WaveyCapesMod.INSTANCE.getCapeNodeCollector();
        PoseStack sharedStack = new PoseStack();
        float delta = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
        for (CapeNodeCollector.CapeNode cape : collector.getCapes()) {
            sharedStack.last().set(cape.pose());
            sharedStack.pushPose();
            WaveyCapesBase.INSTANCE.getRenderer().render(new PlayerWrapper(cape.state()), sharedStack,
                    this.bufferSource, cape.packedLight(), delta);
            sharedStack.popPose();
        }
        collector.clear();
    }
}

//#else
//$$import net.minecraft.client.Minecraft;
//$$
//$$@Mixin(Minecraft.class)
//$$public class FeatureRenderDispatcherMixin {
//$$}
//$$
//#endif