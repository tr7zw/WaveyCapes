package dev.tr7zw.waveycapes.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.waveycapes.delegate.PlayerDelegate;
import dev.tr7zw.waveycapes.versionless.CapeHolder;
import dev.tr7zw.waveycapes.versionless.sim.BasicSimulation;
import dev.tr7zw.waveycapes.versionless.util.Vector3;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;

@Mixin(Player.class)
public class PlayerMixin implements CapeHolder {

    @Getter
    @Setter
    private BasicSimulation simulation;

    @Getter
    @Setter
    private Vector3 lastPlayerAnimatorPosition = new Vector3();

    @Inject(method = "moveCloak", at = @At("HEAD"))
    private void moveCloakUpdate(CallbackInfo info) {
        if ((Object) this instanceof AbstractClientPlayer) {
            simulate(new PlayerDelegate((AbstractClientPlayer) (Object) this));
        }
    }

}
