# Smooth F5

![Showcase](assets/showcase.gif)

A fork of [Smooth F5](https://www.curseforge.com/minecraft/mc-mods/smooth-f5) by Countered. Available for Forge 1.20.1, Fabric 1.20.1, and NeoForge 1.21.1.

## What this fork changes vs upstream

- Smooths the F5 transition in both directions, not just first to third.
- Smooths every step of the F5 cycle when Shoulder Surfing Reloaded is installed (first, vanilla third back, vanilla third front, SSR shoulder, first).
- The camera no longer lags behind the player when you press F5 while sprinting, flying, or gliding.
- No constant smoothing running in third person. Steady state is just vanilla.
- Fixes the occasional 359 degree spin when the F5 press crosses the yaw wrap.

## Compatibility

- Vanilla third person: works.
- Shoulder Surfing Reloaded, including custom sprint offsets: works.
- Epic Fight TPS mode or lock-on: no-op. Epic Fight runs its own camera, so this mod stays out of the way.

## Config

`config/seramicx_smooth_f5-client.toml`:

- `transition_duration_ms`: length of the F5 ease in milliseconds. Default 500, range 50 to 5000.

## Downloads

| Loader | Minecraft | Extra deps |
| --- | --- | --- |
| Forge 47+ | 1.20.1 | none |
| Fabric Loader 0.15+ | 1.20.1 | [Forge Config API Port](https://modrinth.com/mod/forge-config-api-port) |
| NeoForge 21.1+ | 1.21.1 | none |

Grab the matching jar from the [latest release](https://github.com/Seramicx/seramicx-smooth-f5/releases/latest) and drop it in `.minecraft/mods/`.

If the original Smooth F5 is installed, remove it. They conflict.

## Build from source

`./gradlew build` produces all three jars in `<subproject>/build/libs/`. Build a single target with `./gradlew :fabric-1.20.1:build` (or `:forge-1.20.1:build`, `:neoforge-1.21.1:build`).

## Credits

Original mod by [Countered](https://www.curseforge.com/members/countered/projects). This fork keeps the original `net.countered.smoothf5` package.

## License

MIT
