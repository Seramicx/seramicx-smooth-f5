package net.countered.smoothf5.mixin;

import net.countered.smoothf5.SmoothF5ConfigState;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public class CameraMixin {

    @Unique private Vec3  smooth_f5$posLag       = Vec3.ZERO;
    @Unique private float smooth_f5$yawLag       = 0F;
    @Unique private float smooth_f5$pitchLag     = 0F;
    @Unique private long  smooth_f5$transitionStartMs = 0L;
    @Unique private boolean smooth_f5$inTransition    = false;

    @Unique private Vec3  smooth_f5$lastFrameRendered      = Vec3.ZERO;
    @Unique private float smooth_f5$lastFrameRenderedYaw   = 0F;
    @Unique private float smooth_f5$lastFrameRenderedPitch = 0F;

    @Unique private boolean smooth_f5$wasDetached = false;
    @Unique private boolean smooth_f5$wasMirrored = false;
    @Unique private boolean smooth_f5$initialized = false;

    @Inject(method = "setup", at = @At("TAIL"))
    private void onSetup(BlockGetter level, Entity entity, boolean detached,
                         boolean thirdPersonReverse, float partialTick,
                         CallbackInfo ci) {
        Camera self = (Camera) (Object) this;
        CameraAccessor acc = (CameraAccessor) self;

        Vec3  targetPos   = acc.getPosition();
        float targetYaw   = acc.getYRot();
        float targetPitch = acc.getXRot();

        if (!smooth_f5$initialized) {
            smooth_f5$initialized = true;
            smooth_f5$wasDetached = detached;
            smooth_f5$wasMirrored = thirdPersonReverse;
            smooth_f5$lastFrameRendered      = targetPos;
            smooth_f5$lastFrameRenderedYaw   = targetYaw;
            smooth_f5$lastFrameRenderedPitch = targetPitch;
            return;
        }

        if (smooth_f5$wasDetached != detached
         || smooth_f5$wasMirrored != thirdPersonReverse) {
            smooth_f5$posLag   = smooth_f5$lastFrameRendered.subtract(targetPos);
            smooth_f5$yawLag   = Mth.wrapDegrees(smooth_f5$lastFrameRenderedYaw - targetYaw);
            smooth_f5$pitchLag = smooth_f5$lastFrameRenderedPitch - targetPitch;
            smooth_f5$transitionStartMs = System.currentTimeMillis();
            smooth_f5$inTransition = true;
        }
        smooth_f5$wasDetached = detached;
        smooth_f5$wasMirrored = thirdPersonReverse;

        if (!smooth_f5$inTransition) {
            smooth_f5$lastFrameRendered      = targetPos;
            smooth_f5$lastFrameRenderedYaw   = targetYaw;
            smooth_f5$lastFrameRenderedPitch = targetPitch;
            return;
        }

        long durationMs = Math.max(1L, SmoothF5ConfigState.transitionDurationMs);
        long elapsed = System.currentTimeMillis() - smooth_f5$transitionStartMs;

        if (elapsed >= durationMs) {
            smooth_f5$inTransition = false;
            smooth_f5$posLag   = Vec3.ZERO;
            smooth_f5$yawLag   = 0F;
            smooth_f5$pitchLag = 0F;
            smooth_f5$lastFrameRendered      = targetPos;
            smooth_f5$lastFrameRenderedYaw   = targetYaw;
            smooth_f5$lastFrameRenderedPitch = targetPitch;
            return;
        }

        float t = (float) elapsed / (float) durationMs;
        float invT = 1F - t;
        float lagFactor = invT * invT * invT;

        Vec3  finalPos   = targetPos.add(smooth_f5$posLag.scale(lagFactor));
        float finalYaw   = targetYaw   + smooth_f5$yawLag   * lagFactor;
        float finalPitch = targetPitch + smooth_f5$pitchLag * lagFactor;

        acc.callSetPosition(finalPos);
        acc.callSetRotation(finalYaw, finalPitch);

        smooth_f5$lastFrameRendered      = finalPos;
        smooth_f5$lastFrameRenderedYaw   = finalYaw;
        smooth_f5$lastFrameRenderedPitch = finalPitch;
    }
}
