package dev.tr7zw.waveycapes.mixin;

import org.spongepowered.asm.mixin.Mixin;

import dev.tr7zw.waveycapes.CapeHolder;
import dev.tr7zw.waveycapes.sim.StickSimulation;
import net.minecraft.world.entity.player.Player;

@Mixin(Player.class)
public class PlayerMixin implements CapeHolder {

    private StickSimulation stickSimulation = new StickSimulation();
    
    @Override
    public StickSimulation getSimulation() {
        return stickSimulation;
    }

}
