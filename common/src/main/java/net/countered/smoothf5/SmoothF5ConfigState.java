package net.countered.smoothf5;

/**
 * Loader-agnostic runtime config state. Each loader's config wrapper writes
 * {@link #transitionDurationMs} on load/reload; the mixin reads it each frame.
 *
 * <p>Decouples the mixin from any particular config library so the same mixin
 * source compiles against Forge, Fabric (via Forge Config API Port), and
 * NeoForge without conditionals.
 */
public final class SmoothF5ConfigState {
    public static volatile int transitionDurationMs = 500;
    private SmoothF5ConfigState() {}
}
