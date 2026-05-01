package net.countered.smoothf5.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.countered.smoothf5.ExampleMod;

@Mod(ExampleMod.MOD_ID)
public final class ExampleModForge {
    public ExampleModForge() {
        EventBuses.registerModEventBus(ExampleMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // Register the config spec (writes to seramicx_smooth_f5-client.toml).
        // SmoothF5ForgeConfig listens for load/reload events on the mod bus
        // and pushes values into SmoothF5Config so the common-module mixin
        // can read them.
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SmoothF5ForgeConfig.CLIENT_SPEC, "seramicx_smooth_f5-client.toml");
        FMLJavaModLoadingContext.get().getModEventBus().register(SmoothF5ForgeConfig.class);

        ExampleMod.init();
    }
}
