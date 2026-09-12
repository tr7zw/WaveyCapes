//? if (fabric || neoforge) && 1.21.11 {
/*package dev.tr7zw.waveycapes.support;

import com.wynntils.core.components.*;
import com.wynntils.services.cosmetics.type.*;
import dev.tr7zw.transition.mc.entitywrapper.*;
import dev.tr7zw.waveycapes.render.*;
import net.minecraft.client.player.*;
import net.minecraft.client.renderer.rendertype.*;
import net.minecraft.world.entity.player.*;
import net.minecraftcapes.config.*;
import net.minecraftcapes.player.*;

public class WynntilsSupport implements ModSupport {

    private final WynntilsRenderer renderer = new WynntilsRenderer();

    @Override
    public boolean shouldBeUsed(PlayerWrapper capeRenderInfo) {
        if (capeRenderInfo.getAvatar() == null || !(capeRenderInfo.getAvatar() instanceof Player)) return false;
        return Services.Cosmetics.shouldRenderCape(capeRenderInfo.getEntity(), false);
    }

    @Override
    public CapeRenderer getRenderer() {
        return renderer;
    }

    @Override
    public boolean blockFeatureRenderer(Object feature) {
        return feature instanceof WynntilsCapeLayer;
    }

    private static class WynntilsRenderer implements CapeRenderer {

        @Override
        public CapeInfos getCapeInfo(PlayerWrapper capeRenderInfo) {
            if (capeRenderInfo.getEntity() == null) return null;
            var texture = Services.Cosmetics.getCapeTexture(capeRenderInfo.getEntity());
            if (texture == null) return null;
            return new CapeInfos(this, RenderTypes.entityTranslucent(texture), false);
        }
    }

}
*///? }