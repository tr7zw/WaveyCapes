package dev.tr7zw.waveycapes;

//#if MC >= 12109
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Getter;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;

import java.util.ArrayList;
import java.util.List;

public class CapeNodeCollector {

    @Getter
    private final List<CapeNode> capes = new ArrayList<>();

    public void submitCape(AvatarRenderState state, PoseStack stack, int packedLight) {
        capes.add(new CapeNode(state, stack, packedLight));
    }

    public void clear() {
        capes.clear();
    }

    public record CapeNode(AvatarRenderState state, PoseStack stack, int packedLight) {
    }
}
//#endif
