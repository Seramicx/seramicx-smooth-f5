# Smooth F5

A small fork of [Smooth F5](https://www.curseforge.com/minecraft/mc-mods/smooth-f5) by Countered. Forge 1.20.1.

## What's different from upstream

- Smooths F5 in BOTH directions, not just first to third.
- Smooths every step of the F5 cycle when Shoulder Surfing Reloaded is installed (1st -> 3rd back -> 3rd front -> SSR shoulder -> 1st).
- No more constant micro-lag in third person. Smoothing only runs while the F5 transition is happening; the rest of the time the camera is fully responsive. (You can turn the constant-smoothing feel back on with `steady_state_smoothness` in the config if you liked it.)
- Smoothing window caps at ~500ms so it can't drag forever if you mouse-turn during it.
- Fixes the occasional 359-degree spin across the +/-180 yaw boundary.

No-op during Epic Fight TPS / lock-on (EF runs its own camera there).

## Requires

Minecraft 1.20.1, Forge 47+, [Architectury API](https://www.curseforge.com/minecraft/mc-mods/architectury-api).

## Credits

[Countered's original Smooth F5](https://www.curseforge.com/minecraft/mc-mods/smooth-f5).

## License

MIT
