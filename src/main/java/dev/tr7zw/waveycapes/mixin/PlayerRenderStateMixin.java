package dev.tr7zw.waveycapes.mixin;

import org.spongepowered.asm.mixin.Mixin;
//spotless:off
//#if MC >= 12102
import dev.tr7zw.waveycapes.versionless.CapeHolder;
import dev.tr7zw.waveycapes.versionless.ExtendedPlayerRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerRenderState.class)
public class PlayerRenderStateMixin implements ExtendedPlayerRenderState {

    @Unique
    private CapeHolder capeHolder;

    @Unique
    private boolean underwater;

    @Override
    public CapeHolder getCapeHolder() {
        return capeHolder;
    }

    @Override
    public void setCapeHolder(CapeHolder capeHolder) {
        this.capeHolder = capeHolder;
    }

    @Override
    public boolean isUnderwater() {
        return underwater;
    }

    @Override
    public void setUnderwater(boolean underwater) {
        this.underwater = underwater;
    }
}
//#else
//$$import net.minecraft.client.Minecraft;
//$$
//$$@Mixin(Minecraft.class)
//$$public class PlayerRenderStateMixin {}
//#endif
//spotless:on
