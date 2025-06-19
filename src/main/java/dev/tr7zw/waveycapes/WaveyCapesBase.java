package dev.tr7zw.waveycapes;

import dev.tr7zw.waveycapes.delegate.PlayerDelegate;
import dev.tr7zw.waveycapes.support.AnimationSupport;
import dev.tr7zw.waveycapes.support.PlayerAnimatorSupport;
import dev.tr7zw.waveycapes.support.ShoulderSurfingSupport;
import dev.tr7zw.waveycapes.support.SupportManager;
import dev.tr7zw.waveycapes.versionless.ModBase;
import dev.tr7zw.waveycapes.versionless.nms.MinecraftPlayer;
import dev.tr7zw.waveycapes.versionless.util.Vector3;
import lombok.Getter;

public abstract class WaveyCapesBase extends ModBase {

    @Getter
    public static WaveyCapesBase INSTANCE;

    public void init() {
        INSTANCE = this;
        super.init();
        initSupportHooks();
    }

    @Override
    public Vector3 applyModAnimations(MinecraftPlayer player, Vector3 pos) {
        for (AnimationSupport sup : SupportManager.animationSupport) {
            pos = sup.applyAnimationChanges(((PlayerDelegate) player).getPlayer(), 0, pos);
        }
        return pos;
    }

    @Override
    public void initSupportHooks() {
        //#if MC >= 18000
        if (doesClassExist("dev.kosmx.playerAnim.core.impl.AnimationProcessor")) {
            SupportManager.animationSupport.add(new PlayerAnimatorSupport());
            LOGGER.info("Wavey Capes loaded PlayerAnimator support!");
        }
        //#endif
        if (doesClassExist("com.github.exopandora.shouldersurfing.api.client.ICameraEntityRenderer")) {
            ShoulderSurfingSupport.init();
            LOGGER.info("Wavey Capes loaded Shoulder Surfing support!");
        }
    }

}
