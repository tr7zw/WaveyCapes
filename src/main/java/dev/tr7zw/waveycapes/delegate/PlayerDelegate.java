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
        //#if MC >= 12109
        return player.avatarState().getInterpolatedCloakX(0);
        //else
        //$$return player.xCloak;
        //#endif
    }

    public double getZCloak() {
      //#if MC >= 12109
        return player.avatarState().getInterpolatedCloakZ(0);
        //else
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
