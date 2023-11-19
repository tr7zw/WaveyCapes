package dev.tr7zw.waveycapes.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.waveycapes.CapeHolder;
import dev.tr7zw.waveycapes.versionless.sim.BasicSimulation;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;

@Mixin(Player.class)
public class PlayerMixin implements CapeHolder {

    private BasicSimulation stickSimulation;

    @Override
    public BasicSimulation getSimulation() {
        return stickSimulation;
    }

    @Override
    public void setSimulation(BasicSimulation sim) {
        this.stickSimulation = sim;
    }

    @Inject(method = "moveCloak", at = @At("HEAD"))
    private void moveCloakUpdate(CallbackInfo info) {
        if ((Object) this instanceof AbstractClientPlayer) {
            simulate((AbstractClientPlayer) (Object) this);
        }
    }

}
