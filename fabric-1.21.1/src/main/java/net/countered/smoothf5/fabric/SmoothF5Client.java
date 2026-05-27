package net.countered.smoothf5.fabric;

import net.fabricmc.api.ClientModInitializer;

public final class SmoothF5Client implements ClientModInitializer {
    public static final String MOD_ID = "seramicx_smooth_f5";

    @Override
    public void onInitializeClient() {
        SmoothF5Config.register();
    }
}
