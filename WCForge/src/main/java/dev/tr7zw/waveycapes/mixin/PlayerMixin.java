package dev.tr7zw.waveycapes.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.tr7zw.waveycapes.CapeHolder;
import dev.tr7zw.waveycapes.sim.BasicSimulation;
import net.minecraft.entity.player.EntityPlayer;

@Mixin(EntityPlayer.class)
public class PlayerMixin implements CapeHolder {

    private BasicSimulation simulation = null;

    @Override
    public BasicSimulation getSimulation() {
        return simulation;
    }

    @Override
    public void setSimulation(BasicSimulation sim) {
        this.simulation = sim;
    }

    @Inject(method = "onUpdate", at = @At("HEAD"))
    private void moveCloakUpdate(CallbackInfo info) {
        if((Object)this instanceof EntityPlayer) {
            simulate((EntityPlayer)(Object)this);
        }
    }

}
