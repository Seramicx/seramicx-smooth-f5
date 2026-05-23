## Forge 1.1.1 / Fabric 1.0.0 / NeoForge 1.0.0

- Ported to Fabric 1.20.1 and NeoForge 1.21.1. Same smoothing behavior on all three loaders.
- Repo restructured to multi-loader: `common/` (shared mixin), `forge-1.20.1/`, `fabric-1.20.1/`, `neoforge-1.21.1/`.
- Fixed rotation smoothing on Forge: camera rotation now applied via `setRotation` invoker so the renderer's Quaternionf and forward/up/left vectors actually pick up the smoothed values.

## 1.1.0

- tweak camera following logic
