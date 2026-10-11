package dev.tr7zw.waveycapes.render;

import java.util.function.UnaryOperator;

import com.mojang.blaze3d.vertex.VertexConsumer;
//? if >= 1.21.11 {

import net.minecraft.client.renderer.rendertype.*;
//? } else {

/*import net.minecraft.client.renderer.*;
*///? }

public record CapePass(RenderType renderType, UnaryOperator<VertexConsumer> wrapper) {
}
