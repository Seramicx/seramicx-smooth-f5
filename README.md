# Smooth F5

![Showcase](assets/showcase.gif)

A small fork of [Smooth F5](https://www.curseforge.com/minecraft/mc-mods/smooth-f5) by Countered with bidirectional smoothing and a few small fixes. Forge 1.20.1.

## Features (vs upstream)

- Smooths the F5 perspective transition in BOTH directions. Upstream only smooths first to third person, but going back from third to first just snaps. This fork eases the transition both ways.
- Smooths every step of the F5 cycle when Shoulder Surfing Reloaded is installed. The full cycle is `1st person -> vanilla 3rd back -> vanilla 3rd front -> SSR shoulder -> 1st`. Upstream only catches the first <-> third transitions. This fork also smooths back-to-front and front-to-SSR-shoulder, so every press eases instead of snapping.
- No constant micro-lag in third person. Upstream's spring-damper runs on every frame in third person, even when you're not transitioning, which gives the camera a slight permanent lag. This fork only smooths during the actual F5 transition window, then snaps to target so steady-state play is fully responsive. (You can bring upstream's constant smoothing back via the config option below if you preferred that feel.)
- Transition timeout. Caps the smoothing window at ~500ms even if the camera target keeps moving (like if you mouse-turn during the F5 press), so the smoothing-induced lag is bounded.
- Fixes the rare 359-degree spin bug when crossing the +/-180 yaw boundary. Upstream's spring would occasionally rotate the long way around; this fork wraps the yaw delta correctly.

## Compatibility

- Works with vanilla third-person view by itself.
- Works with Shoulder Surfing Reloaded.
- No-op during Epic Fight TPS mode or Epic Fight lock-on. Epic Fight cancels vanilla `Camera.setup` and runs its own camera, so this mod stays out of the way and lets EF handle smoothing in those modes.

## Config

`config/seramicx_smooth_f5-client.toml`:

- `steady_state_smoothness` - how much smoothing to apply outside the F5 transition window (default `0.0`). At `0.0` the camera snaps to target each frame outside transitions (the recommended setting if you have SSR or Epic Fight TPS, since those have their own internal smoothing). At `1.0` you get the upstream constant-3rd-person spring-damper feel back. Intermediate values scale stiffness toward 0.

## Requires

- Minecraft 1.20.1
- Forge 47+
- [Architectury API](https://www.curseforge.com/minecraft/mc-mods/architectury-api)

## Manual install

1. Install Forge 47+ for Minecraft 1.20.1.
2. Install Architectury API.
3. Download the jar from the [latest release](https://github.com/Seramicx/seramicx-smooth-f5/releases/latest).
4. Drop it into your `.minecraft/mods/` folder.

If upstream Smooth F5 is also installed, remove it - they conflict.

## Credits

Original mod by [Countered](https://www.curseforge.com/members/countered/projects). This fork keeps the original `net.countered.smoothf5` package and the same spring-damper math. Changes are in the F5-transition logic in `CameraMixin`.

## License

MIT
