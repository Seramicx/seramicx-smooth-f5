package net.countered.smoothf5.mixin;

import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public interface CameraAccessor {
    @Accessor("xRot") float getXRot();
    @Accessor("yRot") float getYRot();
    @Accessor("position") Vec3 getPosition();

    @Invoker("setPosition")
    void callSetPosition(Vec3 pos);

    // 2-arg setRotation exists in 1.20.1 (only form) and 1.21.1 (deprecated
    // alias for 3-arg, passes roll=0). Calling this updates the rotation
    // Quaternionf plus forwards/up/left vectors; writing xRot/yRot fields
    // directly does not.
    @Invoker("setRotation")
    void callSetRotation(float yRot, float xRot);
}
