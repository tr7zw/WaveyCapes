package dev.tr7zw.waveycapes.support;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import com.mojang.math.Axis;

import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.core.impl.AnimationProcessor;
import dev.kosmx.playerAnim.core.util.Vec3f;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.tr7zw.waveycapes.versionless.util.Vector3;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;

public class PlayerAnimatorSupport implements AnimationSupport {

    @Override
    public Vector3 applyAnimationChanges(AbstractClientPlayer entity, float delta, Vector3 cur) {
        if (entity instanceof IAnimatedPlayer player && player.playerAnimator_getAnimation().isActive()) {
            AnimationProcessor anim = player.playerAnimator_getAnimation();
            anim.setTickDelta(delta); // Probably the tick is done, set tick delta.

            // get required data
            Vec3f rot = anim.get3DTransform("body", TransformType.ROTATION, Vec3f.ZERO);
            Vec3f pos = anim.get3DTransform("body", TransformType.POSITION, Vec3f.ZERO); // .scale(1 / 16f); It has been
                                                                                         // pre-scaled
            Vec3f headPos = anim.get3DTransform("head", TransformType.POSITION, Vec3f.ZERO).scale(1 / 16f);

            Matrix4f relativeTranslation = new Matrix4f();
            relativeTranslation.identity();
            relativeTranslation.scale(-1f); // 0.935 scaling is not even needed :D
            final float bodyOffset = 0.8f; // Distance from the base origin to the head pivot
            relativeTranslation.translate(0, -bodyOffset, 0); // Shift matrix down

            relativeTranslation.translate(pos.getX(), pos.getY(), pos.getZ()); // Apply torso
                                                                               // transformation

            relativeTranslation.rotate(Axis.ZP.rotation(rot.getZ()));
            relativeTranslation.rotate(Axis.YP.rotation(rot.getY()));
            relativeTranslation.rotate(Axis.XP.rotation(rot.getX()));

            relativeTranslation.translate(-headPos.getX(), -headPos.getY(), headPos.getZ());

            relativeTranslation.translate(0, bodyOffset, 0); // Roll back the first [0,1,0] translation.

            // calculate the actual rotations and
            float realYaw = Mth.rotLerp(delta, entity.yBodyRotO, entity.yBodyRot);

            Matrix4f matrix = new Matrix4f(); // To multiply from LEFT, I have to create a new instance?!
            matrix.mul(new Matrix4f().scale(-1, 1, 1)); // What is going on with this?!

            matrix.mul(new Matrix4f().rotate(Axis.YP.rotationDegrees(realYaw)));

            matrix.mul(new Matrix4f().scale(1, 1, -1));
            matrix.mul(relativeTranslation);

            Vector4f offset = new Vector4f(0, 0, 0, 1);
            offset.mul(matrix);
            float scale = 1;
            offset.mul(scale);
            cur.add(offset.x, offset.y, offset.z);
        }
        return cur;
    }

}
