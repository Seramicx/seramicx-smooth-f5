package net.countered.smoothf5.forge;

import net.countered.smoothf5.SmoothF5Config;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

/**
 * Forge ForgeConfigSpec wrapper. Registered from {@link ExampleModForge}.
 * On config load/reload, copies values into {@link SmoothF5Config} so the
 * loader-agnostic mixin in {@code common} can read them.
 */
public final class SmoothF5ForgeConfig {

    public static final ForgeConfigSpec CLIENT_SPEC;
    private static final ForgeConfigSpec.DoubleValue STEADY_STATE_SMOOTHNESS;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.comment("Smoothing").push("smoothing");

        STEADY_STATE_SMOOTHNESS = b
            .comment(
                "How much steady-state smoothing to apply outside F5 transitions.",
                "0.0 (default) = no steady-state smoothing. Camera snaps to target each frame,",
                "    matching vanilla responsiveness. F5 transitions still smooth in both directions.",
                "    Recommended when SSR or Epic Fight TPS is installed (avoids stacking lag).",
                "1.0 = full upstream-Smooth-F5 behavior. Spring-damper runs every frame in 3rd",
                "    person, giving a constant filmy ease on camera movement. Good without SSR.",
                "Intermediate values scale stiffness toward 0 for progressively heavier smoothing."
            )
            .defineInRange("steady_state_smoothness", 0.0, 0.0, 1.0);

        b.pop();
        CLIENT_SPEC = b.build();
    }

    private SmoothF5ForgeConfig() {}

    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == CLIENT_SPEC) {
            SmoothF5Config.steadyStateSmoothness = STEADY_STATE_SMOOTHNESS.get();
        }
    }

    @SubscribeEvent
    public static void onReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == CLIENT_SPEC) {
            SmoothF5Config.steadyStateSmoothness = STEADY_STATE_SMOOTHNESS.get();
        }
    }
}
