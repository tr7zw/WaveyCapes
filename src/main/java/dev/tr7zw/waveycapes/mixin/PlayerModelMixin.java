package dev.tr7zw.waveycapes.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.tr7zw.waveycapes.accessor.PlayerEntityModelAccessor;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;

@Mixin(PlayerModel.class)
public class PlayerModelMixin<T extends LivingEntity> extends HumanoidModel<T> implements PlayerEntityModelAccessor {

    public PlayerModelMixin(ModelPart modelPart) {
        super(modelPart);
    }

    private ModelPart[] customCape = new ModelPart[18];

    @Inject(method = "<init>*", at = @At("RETURN"))
    public void onCreate(ModelPart modelPart, boolean bl, CallbackInfo info) {
        for (int i = 0; i < 16; i++) {
            this.customCape[i] = modelPart.getChild("customCape_" + i);
        }
    }

    @Inject(method = "createMesh", at = @At("RETURN"))
    private static void createMesh(CubeDeformation cubeDeformation, boolean bl,
            CallbackInfoReturnable<MeshDefinition> info) {
        PartDefinition partDefinition = info.getReturnValue().getRoot();
        for (int i = 0; i < 16; i++)
            partDefinition.addOrReplaceChild("customCape_" + i,
                    CubeListBuilder.create().texOffs(0, i)
                            .addBox(-5.0F, i, -1.0F, 10.0F, 1.0F, 1.0F, cubeDeformation, 1.0F, 0.5F),
                    PartPose.offset(0.0F, 0.0F, 0.0F));
    }

    @Inject(method = "renderCloak", at = @At("HEAD"), cancellable = true)
    public void renderCloak(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j, CallbackInfo info) {
        info.cancel();
    }

    @Override
    public ModelPart[] getCustomCapeParts() {
        return customCape;
    }

}
