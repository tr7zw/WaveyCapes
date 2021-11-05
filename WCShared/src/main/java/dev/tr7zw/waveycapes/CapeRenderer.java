package dev.tr7zw.waveycapes;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;

public interface CapeRenderer {

    public void render(AbstractClientPlayer player, int part, ModelPart model, PoseStack poseStack, MultiBufferSource multiBufferSource,  int light, int overlay);
    
}
