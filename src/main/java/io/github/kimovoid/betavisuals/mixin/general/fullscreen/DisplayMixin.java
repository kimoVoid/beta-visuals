package io.github.kimovoid.betavisuals.mixin.general.fullscreen;

import io.github.kimovoid.betavisuals.BetaVisuals;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Display.class, remap = false)
public class DisplayMixin {

    @Inject(method = "setFullscreen", at = @At("TAIL"))
    private static void setFullscreen(boolean fullscreen, CallbackInfo ci) {
        BetaVisuals.OPTIONS.fullscreen = fullscreen;
        BetaVisuals.OPTIONS.save();
    }
}
