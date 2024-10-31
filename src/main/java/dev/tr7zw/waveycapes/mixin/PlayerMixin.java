package dev.tr7zw.waveycapes.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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
public abstract class PlayerMixin extends Entity implements CapeHolder {

    public PlayerMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Unique
    @Getter
    @Setter
    private BasicSimulation simulation;

    @Unique
    @Getter
    @Setter
    private Vector3 lastPlayerAnimatorPosition = new Vector3();

    @Unique
    private boolean dirty = false;

    @Override
    public void setDirty() {
        this.dirty = true;
    }

    @Inject(method = "moveCloak", at = @At("HEAD"))
    private void moveCloakUpdate(CallbackInfo info) {
        if ((Object) this instanceof AbstractClientPlayer player) {
            updateSimulation(16);
            PlayerDelegate playerDelegate = new PlayerDelegate(player);
            if (dirty) {
                dirty = false;
                simulation.applyMovement(new Vector3(1f, 1f, 0));
                for (int i = 0; i < 5; i++) { // quickly doing a few simulation steps to get the cape int a stable
                    // configuration
                    simulate(playerDelegate);
                }
            }
            simulate(playerDelegate);
        }
    }

}
