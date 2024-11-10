package dev.tr7zw.waveycapes.mixin;

//spotless:off
//#if MC >= 12102
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Minecraft.class)
public class PlayerModelMixin {}

//#else
//$$import org.spongepowered.asm.mixin.Final;
//$$import org.spongepowered.asm.mixin.Mixin;
//$$import org.spongepowered.asm.mixin.Shadow;
//$$import org.spongepowered.asm.mixin.injection.At;
//$$import org.spongepowered.asm.mixin.injection.Inject;
//$$import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//$$
//$$import com.mojang.blaze3d.vertex.PoseStack;
//$$import com.mojang.blaze3d.vertex.VertexConsumer;
//$$
//$$import dev.tr7zw.waveycapes.PlayerModelAccess;
//$$import net.minecraft.client.model.HumanoidModel;
//$$import net.minecraft.client.model.PlayerModel;
//$$import net.minecraft.client.model.geom.ModelPart;
//$$import net.minecraft.world.entity.LivingEntity;
//$$
//$$@Mixin(value = PlayerModel.class)
//$$public class PlayerModelMixin<T extends LivingEntity> extends HumanoidModel<T> implements PlayerModelAccess {
//$$
//$$    @Shadow
//$$    @Final
//$$    private ModelPart cloak;
//$$
//$$    public PlayerModelMixin(ModelPart modelPart) {
//$$        //#if MC >= 11700
//$$        super(modelPart);
//$$        //#else
//$$        //$$ super(0);
//$$        //#endif
//$$    }
//$$
//$$    @Inject(method = "renderCloak", at = @At("HEAD"), cancellable = true)
//$$    public void renderCloak(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j, CallbackInfo info) {
//$$        info.cancel();
//$$    }
//$$
//$$    @Override
//$$    public ModelPart getCloak() {
//$$        return cloak;
//$$    }
//$$
//$$}
//#endif
//spotless:on
