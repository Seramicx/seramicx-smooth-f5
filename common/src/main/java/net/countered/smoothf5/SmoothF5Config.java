package net.countered.smoothf5;

/**
 * Loader-agnostic config holder. The Forge entry point registers a
 * {@code ForgeConfigSpec} and copies values into these static fields on
 * load/reload; the mixin in {@code common} reads them directly.
 */
public final class SmoothF5Config {

    /**
     * How much steady-state smoothing to apply outside F5 transitions.
     * <ul>
     *   <li>{@code 0.0} (default): no smoothing in steady state. Camera snaps
     *       to whatever target the game / SSR / Epic Fight TPS sets each
     *       frame. F5 transitions still smooth in both directions.</li>
     *   <li>{@code 1.0}: full upstream-Smooth-F5 behavior - spring-damper
     *       runs every frame in 3rd person, giving a constant filmy ease on
     *       camera movement. Good without SSR; not recommended with SSR
     *       (stacks lag on top of SSR's own lerp).</li>
     *   <li>Intermediate values: scale the spring stiffness down toward 0
     *       for progressively heavier smoothing.</li>
     * </ul>
     */
    public static volatile double steadyStateSmoothness = 0.0;

    private SmoothF5Config() {}
}
