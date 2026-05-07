package net.countered.smoothf5.mixin;

import net.countered.smoothf5.SmoothF5Config;
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

/**
 * F5 perspective transition smoothing — zero player-velocity lag.
 *
 * <p><b>How it works:</b> at an F5 press the mod captures a one-shot world-space
 * "lag offset" — the vector from where vanilla now wants the camera to where
 * the camera actually was last frame. Each subsequent frame the camera is
 * placed at <em>vanilla's current target + lag × decay(t)</em>. The decay is a
 * fixed-duration cubic ease-out (0..TRANSITION_MS).
 *
 * <p>Because vanilla's target is recomputed every frame and tracks the player
 * perfectly, and the lag is independent of the player, the camera follows the
 * player at any speed (sprint, fly, elytra) with <b>no velocity-induced lag</b>.
 * The smoothing is purely the decaying perspective offset, which converges to
 * zero in TRANSITION_MS regardless of what the player is doing.
 *
 * <p>This replaces the upstream spring-damper, which inherently lagged a moving
 * target by {@code damping × velocity / stiffness} — the cause of the visible
 * decoupling reports during sprint and elytra flight.
 */
@Mixin(Camera.class)
public class CameraMixin {

    @Unique private Vec3  smooth_f5$posLag       = Vec3.ZERO;
    @Unique private float smooth_f5$yawLag       = 0F;
    @Unique private float smooth_f5$pitchLag     = 0F;
    @Unique private long  smooth_f5$transitionStartMs = 0L;
    @Unique private boolean smooth_f5$inTransition    = false;

    // What we wrote last frame. On chained F5 presses (user pressing F5 while
    // a previous transition is still decaying), we capture from this — the
    // camera's actual rendered position — instead of vanilla's "would-be"
    // target. Keeps chained transitions smooth.
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

        // F5 press detection. Track BOTH `detached` (3rd↔1st) and
        // `thirdPersonReverse` (3rd back↔front, also flips through SSR's
        // shoulder mode which uses CameraType.THIRD_PERSON_BACK with
        // mirrored=false). Each flip starts a fresh transition.
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
            // Steady state: camera = vanilla target. No override, zero lag.
            smooth_f5$lastFrameRendered      = targetPos;
            smooth_f5$lastFrameRenderedYaw   = targetYaw;
            smooth_f5$lastFrameRenderedPitch = targetPitch;
            return;
        }

        long durationMs = Math.max(1L, SmoothF5Config.transitionDurationMs);
        long elapsed = System.currentTimeMillis() - smooth_f5$transitionStartMs;

        if (elapsed >= durationMs) {
            // Transition complete. Camera = vanilla target from here on.
            smooth_f5$inTransition = false;
            smooth_f5$posLag   = Vec3.ZERO;
            smooth_f5$yawLag   = 0F;
            smooth_f5$pitchLag = 0F;
            smooth_f5$lastFrameRendered      = targetPos;
            smooth_f5$lastFrameRenderedYaw   = targetYaw;
            smooth_f5$lastFrameRenderedPitch = targetPitch;
            return;
        }

        // Cubic ease-out: lag(t) = lagInitial × (1 - t)^3.
        // Fast departure from old pose, gentle settle into new pose.
        float t = (float) elapsed / (float) durationMs;
        float invT = 1F - t;
        float lagFactor = invT * invT * invT;

        Vec3  finalPos   = targetPos.add(smooth_f5$posLag.scale(lagFactor));
        float finalYaw   = targetYaw   + smooth_f5$yawLag   * lagFactor;
        float finalPitch = targetPitch + smooth_f5$pitchLag * lagFactor;

        acc.callSetPosition(finalPos);
        acc.setYRot(finalYaw);
        acc.setXRot(finalPitch);

        smooth_f5$lastFrameRendered      = finalPos;
        smooth_f5$lastFrameRenderedYaw   = finalYaw;
        smooth_f5$lastFrameRenderedPitch = finalPitch;
    }
}
