package dev.tr7zw.waveycapes.support;

import dev.tr7zw.waveycapes.versionless.util.Vector3;
import net.minecraft.client.player.AbstractClientPlayer;

public interface AnimationSupport {

    public Vector3 applyAnimationChanges(AbstractClientPlayer entity, float delta, Vector3 cur);

}
