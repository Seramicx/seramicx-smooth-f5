package net.countered.smoothf5;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

/**
 * Forge {@link ForgeConfigSpec} + the runtime values the mixin reads.
 *
 * <p>Single config knob: {@code transition_duration_ms} — how long an F5 ease
 * lasts. Default 500ms. The new no-lag design has nothing to tune for
 * "smoothness" because the camera always tracks the player perfectly; the only
 * meaningful parameter is how fast the perspective change eases in.
 */
public final class SmoothF5Config {

    public static final ForgeConfigSpec CLIENT_SPEC;
    private static final ForgeConfigSpec.IntValue TRANSITION_DURATION_MS;

    /** Runtime value the mixin reads each frame. Loaded from config below. */
    public static volatile int transitionDurationMs = 500;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.comment("Smoothing").push("smoothing");

        TRANSITION_DURATION_MS = b
            .comment(
                "Length of the F5 perspective ease-out, in milliseconds.",
                "  500 (default) - quick, snappy.",
                "  Higher (e.g. 800-1000) - more cinematic.",
                "  Lower (e.g. 200-300) - close to vanilla snap.",
                "Player movement does NOT add to this duration. The ease decays",
                "in a fixed window regardless of what the player is doing."
            )
            .defineInRange("transition_duration_ms", 500, 50, 5000);

        b.pop();
        CLIENT_SPEC = b.build();
    }

    private SmoothF5Config() {}

    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == CLIENT_SPEC) {
            transitionDurationMs = TRANSITION_DURATION_MS.get();
        }
    }

    @SubscribeEvent
    public static void onReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == CLIENT_SPEC) {
            transitionDurationMs = TRANSITION_DURATION_MS.get();
        }
    }
}
