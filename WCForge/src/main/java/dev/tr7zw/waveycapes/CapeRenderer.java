//package dev.tr7zw.waveycapes;
//
//import com.mojang.blaze3d.vertex.PoseStack;
//import com.mojang.blaze3d.vertex.VertexConsumer;
//
//import net.minecraft.client.model.geom.ModelPart;
//import net.minecraft.client.player.AbstractClientPlayer;
//import net.minecraft.client.renderer.MultiBufferSource;
//import net.minecraft.entity.player.EntityPlayer;
//
//public interface CapeRenderer {
//
//    public void render(EntityPlayer player, int part, ModelPart model, PoseStack poseStack, MultiBufferSource multiBufferSource,  int light, int overlay);
//    
//    public default VertexConsumer getVertexConsumer(MultiBufferSource multiBufferSource, AbstractClientPlayer player) {
//        return null;
//    }
//    
//    public boolean vanillaUvValues();
//    
//}
