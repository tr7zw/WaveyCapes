package dev.tr7zw.waveycapes.delegate;

import dev.tr7zw.waveycapes.versionless.nms.MinecraftPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Delegate;
import net.minecraft.client.player.AbstractClientPlayer;

@AllArgsConstructor
public class PlayerDelegate implements MinecraftPlayer {

    @Getter
    @Delegate(types = MinecraftPlayer.class)
    private AbstractClientPlayer player;

    public double getXCloak() {
        return player.xCloak;
    }

    public double getZCloak() {
        return player.zCloak;
    }

    public float getYBodyRotO() {
        return player.yBodyRotO;
    }

    public float getYBodyRot() {
        return player.yBodyRot;
    }

    public double getYo() {
        return player.yo;
    }

    public double getXo() {
        return player.xo;
    }

    public double getZo() {
        return player.zo;
    }

}
