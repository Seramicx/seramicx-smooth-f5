package net.countered.smoothf5.forge;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SmoothF5Mod.MOD_ID)
public final class SmoothF5Mod {
    public static final String MOD_ID = "seramicx_smooth_f5";

    public SmoothF5Mod() {
        ModLoadingContext.get().registerConfig(
            ModConfig.Type.CLIENT, SmoothF5Config.CLIENT_SPEC,
            "seramicx_smooth_f5-client.toml"
        );
        FMLJavaModLoadingContext.get().getModEventBus().register(SmoothF5Config.class);
    }
}
