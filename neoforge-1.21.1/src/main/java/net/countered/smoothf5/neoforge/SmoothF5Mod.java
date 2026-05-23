package net.countered.smoothf5.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(SmoothF5Mod.MOD_ID)
public final class SmoothF5Mod {
    public static final String MOD_ID = "seramicx_smooth_f5";

    public SmoothF5Mod(IEventBus modBus, ModContainer container) {
        container.registerConfig(
            ModConfig.Type.CLIENT, SmoothF5Config.CLIENT_SPEC,
            "seramicx_smooth_f5-client.toml"
        );
        modBus.register(SmoothF5Config.class);
    }
}
