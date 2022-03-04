package dev.tr7zw.waveycapes.support;

import dev.tr7zw.waveycapes.CapeRenderer;
import net.minecraft.client.player.AbstractClientPlayer;

public interface ModSupport {

    public boolean shouldBeUsed(AbstractClientPlayer player);
    
    public CapeRenderer getRenderer();
    
    public boolean blockFeatureRenderer(Object feature);
    
}
