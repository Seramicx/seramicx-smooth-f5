# Seramicx Smooth F5

![Showcase](assets/showcase.gif)

Fork of [Smooth F5](https://www.curseforge.com/minecraft/mc-mods/smooth-f5) by Countered. Smooth third-person camera when you press F5.

## Loaders

| Loader | Minecraft | Mod version | Extra |
|---|---|---|---|
| Forge | 1.20.1 | 1.1.1 | none |
| Fabric | 1.20.1 | 1.0.0 | Forge Config API Port |
| Fabric | 1.21.1 | 1.0.0 | Forge Config API Port |
| NeoForge | 1.21.1 | 1.0.0 | none |

## What changed from upstream

- Smooth camera when going first person to third and back, not only first to third.
- With [Shoulder Surfing Reloaded](https://www.curseforge.com/minecraft/mc-mods/shoulder-surfing-reloaded), every step of the F5 cycle is smoothed (first, vanilla third, SSR shoulder, back to first).
- No camera lag behind you if you hit F5 while sprinting, flying, or gliding.
- Third person while you are not pressing F5 stays vanilla. No always-on smoothing.
- Fixes the rare full camera spin on some F5 presses.

## Compatibility

- Vanilla third person
- Shoulder Surfing Reloaded, including custom sprint offsets
- Epic Fight TPS or lock-on: does nothing while Epic Fight owns the camera

## Config

`config/seramicx_smooth_f5-client.toml`:

- `transition_duration_ms` - F5 transition length in ms (default 500, range 50 to 5000)

## Install

1. Pick the jar for your loader and Minecraft version from [releases](https://github.com/Seramicx/seramicx-smooth-f5/releases/latest).
2. Drop it in `mods/`.
3. Remove Countered's Smooth F5 if it is installed. Only one Smooth F5 at a time.

Fabric 1.20.1 and 1.21.1 also need Fabric API and [Forge Config API Port](https://modrinth.com/mod/forge-config-api-port).

## Build

```
./gradlew build
```

Single target: `./gradlew :forge-1.20.1:build`, `:fabric-1.20.1:build`, `:fabric-1.21.1:build`, or `:neoforge-1.21.1:build`. Jars land in that subproject's `build/libs/`.

## Credits

Original mod by [Countered](https://www.curseforge.com/members/countered/projects). This fork keeps the `net.countered.smoothf5` package.

## License

MIT
