package dev.tr7zw.waveycapes.delegate;

import dev.tr7zw.waveycapes.versionless.nms.MinecraftPlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Delegate;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.Minecraft;

@AllArgsConstructor
public class PlayerDelegate implements MinecraftPlayer {

    @Getter
    @Delegate(types = MinecraftPlayer.class)
    //#if MC >= 12109
    private net.minecraft.world.entity.Avatar player;
    //#else
    //$$private AbstractClientPlayer player;
    //#endif

    public double getXCloak() {
        //#if MC >= 12109
        float delta = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
        if (player instanceof AbstractClientPlayer acp) {
            return acp.avatarState().getInterpolatedCloakX(delta);
        } else if (player instanceof net.minecraft.client.entity.ClientMannequin cm) {
            return cm.avatarState().getInterpolatedCloakX(delta);
        } else {
            return 0;
        }
        //#else
        //$$return player.xCloak;
        //#endif
    }

    public double getZCloak() {
        //#if MC >= 12109
        float delta = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
        if (player instanceof AbstractClientPlayer acp) {
            return acp.avatarState().getInterpolatedCloakZ(delta);
        } else if (player instanceof net.minecraft.client.entity.ClientMannequin cm) {
            return cm.avatarState().getInterpolatedCloakZ(delta);
        } else {
            return 0;
        }
        //#else
        //$$return player.zCloak;
        //#endif
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

    //#if MC <= 11605
    //$$  public float getXRot() {
    //$$      return player.xRot;
    //$$  }
    //$$  
    //$$  public float getYRot() {
    //$$      return player.yRot;
    //$$  }
    //#endif

}