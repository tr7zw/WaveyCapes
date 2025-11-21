package dev.tr7zw.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import net.minecraft.SharedConstants;
//? if >= 1.21.11 {
import net.minecraft.client.model.player.*;
//?} else {
/*import net.minecraft.client.model.*;*/
//?}
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
        //? if >= 1.21.9 {

        objenesis.newInstance(net.minecraft.client.renderer.entity.player.AvatarRenderer.class);
        //? } else {
        /*
         objenesis.newInstance(net.minecraft.client.renderer.entity.player.PlayerRenderer.class);
        *///? }
    }

}
