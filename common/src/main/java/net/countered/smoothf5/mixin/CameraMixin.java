package net.countered.smoothf5.mixin;

import net.countered.smoothf5.SmoothF5Config;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
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
    @Unique private Vec3 smooth_f5$smoothPos = Vec3.ZERO;
    @Unique private Vec3 smooth_f5$smoothVel = Vec3.ZERO;
    @Unique private float smooth_f5$smoothYaw;
    @Unique private float smooth_f5$smoothPitch;
    @Unique private float smooth_f5$yawVel, smooth_f5$pitchVel;

    @Unique private static final float POS_STIFFNESS = 0.3f;
    @Unique private static final float POS_DAMPING   = 1f;
    @Unique private static final float ROT_STIFFNESS = 0.7f;
    @Unique private static final float ROT_DAMPING   = 1.3f;

    // Transition is "done" once the smoothed pose is within these of target,
    // OR once MAX_TRANSITION_MS has elapsed (whichever comes first). The timer
    // cap prevents the smoothing from staying engaged if the user starts
    // mouse-turning mid-transition: the moving target prevents convergence,
    // so without the cap the smoothing-induced lag would persist indefinitely.
    @Unique private static final double POS_EPSILON = 0.05;
    @Unique private static final float  ROT_EPSILON = 0.5f;
    @Unique private static final long   MAX_TRANSITION_MS = 1000L;
    // Target must have moved less than this over the last STABILITY_WINDOW
    // frames to count as "settled". Per-frame stability is not enough: when
    // SSR is mid-settle on a sprint offset (or any other gradual lerp), the
    // per-frame delta drops below a small epsilon while the cumulative
    // movement over a few frames is still significant. A windowed check
    // catches that case and keeps the transition active until SSR is truly
    // done lerping.
    @Unique private static final double TARGET_STABLE_EPSILON = 0.05;
    @Unique private static final int    STABILITY_WINDOW = 20;

    @Unique private boolean smooth_f5$initialized = false;
    @Unique private boolean smooth_f5$wasDetached = false;
    @Unique private boolean smooth_f5$wasMirrored = false;
    @Unique private Vec3[] smooth_f5$targetHistory = new Vec3[STABILITY_WINDOW];
    @Unique private int    smooth_f5$historyIdx = 0;
    @Unique private int    smooth_f5$historyFilled = 0;

    // Two distinct transition windows. We smooth ONLY while one of these is
    // active and exit on convergence (or timer) - outside transitions, the
    // camera snaps to whatever target the game (or SSR, EpicFight TPS, etc.)
    // set, so the user doesn't get constant lag stacked on top of those
    // mods' own lerps.
    @Unique private boolean smooth_f5$inFpTransition = false; // 3rd -> 1st
    @Unique private boolean smooth_f5$inTpTransition = false; // any other F5 transition (1st -> 3rd, 3rd back ↔ 3rd front, 3rd front ↔ SSR)
    @Unique private long    smooth_f5$transitionStartMs = 0L;

    @Inject(method = "setup", at = @At("TAIL"))
    private void onSetup(BlockGetter level, Entity entity, boolean detached,
                         boolean thirdPersonReverse, float partialTick,
                         CallbackInfo ci) {
        Camera self = (Camera) (Object) this;
        CameraAccessor acc = (CameraAccessor) self;

        if (!smooth_f5$initialized) {
            smooth_f5$smoothPos = self.getPosition();
            smooth_f5$smoothYaw = acc.getYRot();
            smooth_f5$smoothPitch = acc.getXRot();
            smooth_f5$initialized = true;
            smooth_f5$wasDetached = detached;
            smooth_f5$wasMirrored = thirdPersonReverse;
            return;
        }

        // Detect F5 transitions. The smoothed pose from the previous frame is
        // already at the previous perspective's camera state (since we snap to
        // target every frame outside transitions), so it serves as the
        // transition's start pose without any extra capture.
        //
        // Track BOTH `detached` and `thirdPersonReverse` (mirrored). With SSR
        // installed and "vanilla 3rd person back" enabled in its config, the F5
        // cycle is:
        //   1st (detached=F)
        //   → 3rd back        (detached=T, mirrored=F)
        //   → 3rd front       (detached=T, mirrored=T)
        //   → SSR             (detached=T, mirrored=F - SSR uses CameraType.THIRD_PERSON_BACK)
        //   → 1st (detached=F)
        // Without `mirrored` tracking we'd miss the 3rd-back↔3rd-front and
        // 3rd-front↔SSR transitions because `detached` stays true through all
        // three 3rd-person variants.
        boolean transitionStarted = false;
        if (smooth_f5$wasDetached && !detached) {
            // 3rd -> 1st
            smooth_f5$inFpTransition = true;
            smooth_f5$inTpTransition = false;
            transitionStarted = true;
        } else if (!smooth_f5$wasDetached && detached) {
            // 1st -> 3rd
            smooth_f5$inTpTransition = true;
            smooth_f5$inFpTransition = false;
            transitionStarted = true;
        } else if (smooth_f5$wasMirrored != thirdPersonReverse) {
            // 3rd-person variant change (back ↔ front, or front ↔ SSR which
            // mirror-flips back to false). Smooth as a "TP transition".
            smooth_f5$inTpTransition = true;
            smooth_f5$inFpTransition = false;
            transitionStarted = true;
        }
        smooth_f5$wasDetached = detached;
        smooth_f5$wasMirrored = thirdPersonReverse;

        if (transitionStarted) {
            smooth_f5$transitionStartMs = System.currentTimeMillis();
            // Reset target history so the windowed stability check sees a
            // fresh transition and won't accept a stale target from the
            // previous perspective.
            smooth_f5$historyFilled = 0;
            smooth_f5$historyIdx = 0;
        }

        boolean inTransition = smooth_f5$inFpTransition || smooth_f5$inTpTransition;
        double steadyState = SmoothF5Config.steadyStateSmoothness;

        if (!inTransition && steadyState <= 0.0) {
            // Steady state with smoothing OFF: snap to target so subsequent
            // mouse turns / position updates from SSR / EF TPS / vanilla don't
            // get a smoothing lag stacked on top of whatever those mods do.
            smooth_f5$smoothPos = acc.getPosition();
            smooth_f5$smoothYaw = acc.getYRot();
            smooth_f5$smoothPitch = acc.getXRot();
            smooth_f5$smoothVel = Vec3.ZERO;
            smooth_f5$yawVel = smooth_f5$pitchVel = 0f;
            return;
        }

        Vec3 targetPos = acc.getPosition();
        float targetYaw = acc.getYRot();
        float targetPitch = acc.getXRot();

        float dt = Minecraft.getInstance().getDeltaFrameTime();

        // During F5 transitions: full base stiffness/damping (smoothness
        // ignored - transitions always feel the same). In steady state,
        // scale the spring's stiffness by the configured smoothness so 0
        // means snap and 1 means upstream behavior. Damping scales with
        // sqrt(stiffness) to keep the response shape consistent.
        float stiffnessScale = inTransition ? 1f : (float) Math.max(0.0, Math.min(1.0, steadyState));
        float posStiffness = POS_STIFFNESS * stiffnessScale;
        float posDamping   = POS_DAMPING   * (float) Math.sqrt(stiffnessScale);
        float rotStiffness = ROT_STIFFNESS * stiffnessScale;
        float rotDamping   = ROT_DAMPING   * (float) Math.sqrt(stiffnessScale);

        Vec3 diff = targetPos.subtract(smooth_f5$smoothPos);
        smooth_f5$smoothVel = smooth_f5$smoothVel.add(diff.scale(posStiffness * dt));
        smooth_f5$smoothVel = smooth_f5$smoothVel.scale((float) Math.exp(-posDamping * dt));
        smooth_f5$smoothPos = smooth_f5$smoothPos.add(smooth_f5$smoothVel.multiply(dt, dt, dt));

        // Wrap yaw diff so we never spring across the +/-180 boundary the long way.
        float yawDiff   = Mth.wrapDegrees(targetYaw - smooth_f5$smoothYaw);
        float pitchDiff = targetPitch - smooth_f5$smoothPitch;

        smooth_f5$yawVel   += yawDiff   * rotStiffness * dt;
        smooth_f5$pitchVel += pitchDiff * rotStiffness * dt;
        smooth_f5$yawVel   *= (float) Math.exp(-rotDamping * dt);
        smooth_f5$pitchVel *= (float) Math.exp(-rotDamping * dt);

        smooth_f5$smoothYaw   += smooth_f5$yawVel   * dt;
        smooth_f5$smoothPitch += smooth_f5$pitchVel * dt;

        // Exit transition on EITHER convergence (with target settled over a
        // window) or timer cap. Plain frame-to-frame stability isn't enough:
        // when SSR is mid-settle on a sprint offset, the per-frame delta
        // drops below a small epsilon a few frames before the offset has
        // actually finished lerping. So we compare the current target to
        // the target STABILITY_WINDOW frames ago - if it has moved more
        // than the epsilon over that window, the target is still settling
        // and we keep the transition active.
        //
        // The timer cap remains so a moving target (mouse-turn mid-
        // transition) can't keep smoothing engaged indefinitely.
        double posErr = smooth_f5$smoothPos.distanceTo(targetPos);
        Vec3 oldestInWindow = smooth_f5$historyFilled >= STABILITY_WINDOW
                ? smooth_f5$targetHistory[smooth_f5$historyIdx]
                : null;
        boolean targetStable = oldestInWindow != null
                && oldestInWindow.distanceTo(targetPos) < TARGET_STABLE_EPSILON;
        boolean converged = posErr < POS_EPSILON
                && Math.abs(yawDiff) < ROT_EPSILON
                && Math.abs(pitchDiff) < ROT_EPSILON
                && targetStable;
        boolean timedOut = (System.currentTimeMillis() - smooth_f5$transitionStartMs) > MAX_TRANSITION_MS;

        // Push current target into the ring buffer.
        smooth_f5$targetHistory[smooth_f5$historyIdx] = targetPos;
        smooth_f5$historyIdx = (smooth_f5$historyIdx + 1) % STABILITY_WINDOW;
        if (smooth_f5$historyFilled < STABILITY_WINDOW) smooth_f5$historyFilled++;
        if (inTransition && (converged || timedOut)) {
            smooth_f5$inFpTransition = false;
            smooth_f5$inTpTransition = false;
            smooth_f5$smoothPos = targetPos;
            smooth_f5$smoothYaw = targetYaw;
            smooth_f5$smoothPitch = targetPitch;
            smooth_f5$smoothVel = Vec3.ZERO;
            smooth_f5$yawVel = smooth_f5$pitchVel = 0f;
            return;
        }

        acc.callSetPosition(smooth_f5$smoothPos);
        acc.setYRot(smooth_f5$smoothYaw);
        acc.setXRot(smooth_f5$smoothPitch);
    }
}
