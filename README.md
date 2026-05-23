# Smooth F5

![Showcase](assets/showcase.gif)

A small fork of [Smooth F5](https://www.curseforge.com/minecraft/mc-mods/smooth-f5) by Countered with bidirectional smoothing and a zero-lag rewrite. Forge 1.20.1.

## Features (vs upstream)

- **Smooths the F5 perspective transition in BOTH directions.** Upstream only smooths first to third person, but going back from third to first just snaps. This fork eases the transition both ways.
- **Smooths every step of the F5 cycle when Shoulder Surfing Reloaded is installed.** The full cycle is `1st person -> vanilla 3rd back -> vanilla 3rd front -> SSR shoulder -> 1st`. Upstream only catches the first <-> third transitions. This fork also smooths back-to-front and front-to-SSR-shoulder, so every press eases instead of snapping.
- **Zero player-velocity lag.** Upstream's spring-damper inherently lagged a moving target by `damping × velocity / stiffness`, which is why the camera visibly decoupled while sprinting, flying, or elytra-gliding through an F5 transition. This fork uses a one-shot ease-out on the perspective change itself; the camera always tracks the player perfectly while the perspective offset decays in a fixed window. Sprint, fly, elytra — none affect the smoothing.
- **No constant micro-lag in third person.** Steady-state is just vanilla camera, no spring running every frame.
- **Fixes the rare 359-degree spin bug** when crossing the +/-180 yaw boundary. Yaw lag is wrapped correctly at capture.

## Compatibility

- Works with vanilla third-person view by itself.
- Works with Shoulder Surfing Reloaded — including custom sprint offset configs.
- No-op during Epic Fight TPS mode or Epic Fight lock-on. Epic Fight cancels vanilla `Camera.setup` and runs its own camera, so this mod stays out of the way.

## Config

`config/seramicx_smooth_f5-client.toml`:

- `transition_duration_ms` — length of the F5 ease-out in milliseconds (default `500`, range `50`–`5000`). Player movement does NOT add to this duration; the ease decays in a fixed window regardless of what the player is doing.

## Requires

Three loader/version targets are published from this repo:

| Target | Minecraft | Loader | Extra deps |
| --- | --- | --- | --- |
| Forge | 1.20.1 | Forge 47+ | none |
| Fabric | 1.20.1 | Fabric Loader 0.15+ | [Forge Config API Port](https://modrinth.com/mod/forge-config-api-port) (Fabric build) |
| NeoForge | 1.21.1 | NeoForge 21.1+ | none |

## Manual install

1. Install the relevant loader for your Minecraft version.
2. Download the matching jar from the [latest release](https://github.com/Seramicx/seramicx-smooth-f5/releases/latest):
   - `seramicx-smooth-f5-forge-1.20.1-*.jar` for Forge
   - `seramicx-smooth-f5-fabric-1.20.1-*.jar` for Fabric (also install Forge Config API Port)
   - `seramicx-smooth-f5-neoforge-1.21.1-*.jar` for NeoForge
3. Drop it into your `.minecraft/mods/` folder.

If upstream Smooth F5 is also installed, remove it — they conflict.

## Build from source

`./gradlew build` produces all three jars in `<subproject>/build/libs/`. Build a single target with e.g. `./gradlew :fabric-1.20.1:build`.

## Credits

Original mod by [Countered](https://www.curseforge.com/members/countered/projects). This fork keeps the original `net.countered.smoothf5` package. The F5-transition logic in `CameraMixin` was rewritten in this fork.

## License

MIT
