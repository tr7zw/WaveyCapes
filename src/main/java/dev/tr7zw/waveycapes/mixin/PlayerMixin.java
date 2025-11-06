package dev.tr7zw.waveycapes.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.UUID;

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

//? if >= 1.21.9 {

@Mixin(value = LivingEntity.class)
//? } else {
/*
 @Mixin(net.minecraft.world.entity.player.Player.class)
*///? }
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

    @Inject(method = "tick", at = @At("TAIL"))
    private void moveCloakUpdate(CallbackInfo info) {
        //? if >= 1.21.9 {

        if (!((Object) this instanceof net.minecraft.world.entity.Avatar)) {
            return;
        }
        var entity = (net.minecraft.world.entity.Avatar) (Object) this;
        //? } else {
        /*
         if (!((Object) this instanceof AbstractClientPlayer)) {
            return;
         }
         var entity = (AbstractClientPlayer) (Object) this;
        *///? }
        updateSimulation(16);
        PlayerDelegate playerDelegate = new PlayerDelegate(entity);
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

    @Override
    public UUID getWCUUID() {
        return getUUID();
    }

}
