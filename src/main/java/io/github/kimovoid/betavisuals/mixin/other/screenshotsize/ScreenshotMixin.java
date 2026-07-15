package io.github.kimovoid.betavisuals.mixin.other.screenshotsize;

import io.github.kimovoid.betavisuals.BetaVisuals;
import io.github.kimovoid.betavisuals.screenshot.HiResScreenshot;
import net.minecraft.client.Screenshot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.IOException;

@Mixin(Screenshot.class)
public class ScreenshotMixin {

    @Inject(method = "take", at = @At("HEAD"), cancellable = true)
    private static void doHiRes(File gameDir, int width, int height, CallbackInfoReturnable<String> cir) throws IOException {
        int scale = BetaVisuals.OPTIONS.screenshotSize;
        if (scale <= 1) {
            return;
        }
        cir.setReturnValue(HiResScreenshot.take(scale));
    }
}
