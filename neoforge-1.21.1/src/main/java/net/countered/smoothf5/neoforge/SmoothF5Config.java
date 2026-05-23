package net.countered.smoothf5.neoforge;

import net.countered.smoothf5.SmoothF5ConfigState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class SmoothF5Config {

    public static final ModConfigSpec CLIENT_SPEC;
    private static final ModConfigSpec.IntValue TRANSITION_DURATION_MS;

    static {
        ModConfigSpec.Builder b = new ModConfigSpec.Builder();
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
            SmoothF5ConfigState.transitionDurationMs = TRANSITION_DURATION_MS.get();
        }
    }

    @SubscribeEvent
    public static void onReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == CLIENT_SPEC) {
            SmoothF5ConfigState.transitionDurationMs = TRANSITION_DURATION_MS.get();
        }
    }
}
