package io.github.kimovoid.betavisuals.mixin.general.brightness;

import io.github.kimovoid.betavisuals.BetaVisuals;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public class WorldMixin {

    @Inject(
            method = "<init>(Lnet/minecraft/world/storage/WorldStorage;Ljava/lang/String;JLnet/minecraft/world/dimension/Dimension;)V",
            at = @At("TAIL")
    )
    private void setBrightness$1(CallbackInfo ci) {
        BetaVisuals.OPTIONS.updateLighting((World) (Object) this);
    }

    @Inject(
            method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/world/dimension/Dimension;)V",
            at = @At("TAIL")
    )
    private void setBrightness$2(CallbackInfo ci) {
        BetaVisuals.OPTIONS.updateLighting((World) (Object) this);
    }

    @Inject(
            method = "<init>(Lnet/minecraft/world/storage/WorldStorage;Ljava/lang/String;Lnet/minecraft/world/dimension/Dimension;J)V",
            at = @At("TAIL")
    )
    private void setBrightness$3(CallbackInfo ci) {
        BetaVisuals.OPTIONS.updateLighting((World) (Object) this);
    }
}
