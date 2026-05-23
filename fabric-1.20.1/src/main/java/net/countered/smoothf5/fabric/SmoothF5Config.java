package net.countered.smoothf5.fabric;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents;
import net.countered.smoothf5.SmoothF5ConfigState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

// Fabric config wrapper. Uses Forge Config API Port to drive the same
// ForgeConfigSpec schema as the Forge build, then pushes loaded values into
// the shared SmoothF5ConfigState.
public final class SmoothF5Config {

    public static final ForgeConfigSpec CLIENT_SPEC;
    private static final ForgeConfigSpec.IntValue TRANSITION_DURATION_MS;

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

    public static void register() {
        ForgeConfigRegistry.INSTANCE.register(
            SmoothF5Client.MOD_ID,
            ModConfig.Type.CLIENT,
            CLIENT_SPEC,
            "seramicx_smooth_f5-client.toml"
        );
        ModConfigEvents.loading(SmoothF5Client.MOD_ID).register(SmoothF5Config::applyValues);
        ModConfigEvents.reloading(SmoothF5Client.MOD_ID).register(SmoothF5Config::applyValues);
    }

    private static void applyValues(ModConfig config) {
        if (config.getSpec() == CLIENT_SPEC) {
            SmoothF5ConfigState.transitionDurationMs = TRANSITION_DURATION_MS.get();
        }
    }
}
