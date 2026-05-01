# Seramicx's Smooth F5

A fork of [Smooth F5](https://www.curseforge.com/minecraft/mc-mods/smooth-f5) by Countered, with bidirectional F5 transition smoothing and a few small fixes. Forge 1.20.1.

## What changed from upstream

- Bidirectional smoothing. Upstream smooths the F5 transition from first to third person, but going back (third to first) just snaps. This fork smooths both directions.
- Per-perspective F5 cycle smoothing. With Shoulder Surfing Reloaded installed, F5 cycles 1st -> vanilla 3rd back -> vanilla 3rd front -> SSR shoulder -> 1st. Upstream only catches 1st <-> 3rd. This fork also smooths back-to-front and front-to-SSR.
- No constant 3rd-person lag. Upstream's spring-damper runs every frame in third person even when you're not transitioning, which gives a slight permanent camera lag. This fork only smooths during the actual F5 transition window, then snaps to the target so steady-state play is fully responsive. Optional config below to bring the constant smoothing back if you prefer that feel.
- Transition timeout. Caps the smoothing window at ~500ms even if the camera target keeps moving (e.g. you mouse-turn during the F5 press), so the smoothing-induced lag is bounded.
- Yaw wrap fix. Upstream's spring would occasionally rotate the long way around the +/-180 boundary (1 degree drift could become a 359 degree spin). Fixed via `Mth.wrapDegrees` on the yaw delta.

## Compatibility

- Works with vanilla 3rd-person view by itself.
- Works with Shoulder Surfing Reloaded.
- During Epic Fight TPS mode or Epic Fight lock-on, this mod is a no-op (Epic Fight cancels vanilla `Camera.setup` and runs its own camera logic). EF's own lerping handles smoothness in those modes.

## Requirements

- Minecraft 1.20.1 + Forge 47+
- [Architectury API](https://www.curseforge.com/minecraft/mc-mods/architectury-api)

## Config

`config/seramicx_smooth_f5-client.toml`:

- `steady_state_smoothness` - how much smoothing to apply outside F5 transitions (default 0.0). At 0 the camera snaps to target each frame outside transitions (recommended if you have SSR or Epic Fight TPS, since those have their own smoothing). At 1.0 you get the upstream constant-3rd-person spring-damper feel. Intermediate values scale stiffness toward 0.

## Credits

Original mod by [Countered](https://www.curseforge.com/members/countered/projects). This fork keeps the original `net.countered.smoothf5` package and the same spring-damper math; the changes are in `CameraMixin.java`'s transition logic.

## License

MIT
