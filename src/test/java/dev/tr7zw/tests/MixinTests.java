package dev.tr7zw.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import net.minecraft.SharedConstants;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.Bootstrap;

public class MixinTests {

    @BeforeAll
    public static void setup() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    public void testMixins() {
        Objenesis objenesis = new ObjenesisStd();
        objenesis.newInstance(LocalPlayer.class);
        objenesis.newInstance(PlayerModel.class);
        //#if MC >= 12109
        objenesis.newInstance(net.minecraft.client.renderer.entity.player.AvatarRenderer.class);
        //#else
        //$$objenesis.newInstance(net.minecraft.client.renderer.entity.player.PlayerRenderer.class);
        //#endif
    }

}