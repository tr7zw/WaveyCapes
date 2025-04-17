package dev.tr7zw.waveycapes.support;

import com.github.exopandora.shouldersurfing.api.client.ShoulderSurfing;

public class ShoulderSurfingSupport {

    public static void init() {
        SupportManager.setAlphaSupplier(() -> {
            return ShoulderSurfing.getInstance().getCameraEntityRenderer().getCameraEntityAlpha();
        });
    }

}
