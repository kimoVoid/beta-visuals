package io.github.kimovoid.betavisuals.mixin.other.asyncscreenshots;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import io.github.kimovoid.betavisuals.BetaVisuals;
import net.minecraft.client.Screenshot;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

@Mixin(Screenshot.class)
public class ScreenshotMixin {

    @WrapOperation(
            method = "take",
            at = @At(
                    target = "Lnet/minecraft/client/Screenshot;pixels:Ljava/nio/ByteBuffer;",
                    value = "FIELD",
                    opcode = Opcodes.GETSTATIC,
                    ordinal = 0
            )
    )
    private static ByteBuffer resizePixels(Operation<ByteBuffer> original) {
        return null;
    }

    @WrapOperation(
            method = "take",
            at = @At(
                    target = "Lnet/minecraft/client/Screenshot;pixelBuffer:[I",
                    value = "FIELD",
                    opcode = Opcodes.GETSTATIC,
                    ordinal = 0
            )
    )
    private static int[] resizePixelBuffer(Operation<int[]> original) {
        return null;
    }

    @WrapOperation(
            method = "take",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/awt/image/BufferedImage;setRGB(IIII[III)V"
            )
    )
    private static void doAsyncScreenshot(BufferedImage instance, int startX, int startY, int w, int h, int[] rgbArray, int offset, int scansize, Operation<Void> original, @Share("future") LocalRef<CompletableFuture<Void>> future) {
        if (!BetaVisuals.OPTIONS.asyncScreenshots) {
            original.call(instance, startX, startY, w, h, rgbArray, offset, scansize);
            return;
        }
        future.set(CompletableFuture.runAsync(() -> original.call(instance, startX, startY, w, h, rgbArray, offset, scansize)));
    }

    @WrapOperation(
            method = "take",
            at = @At(
                    value = "INVOKE",
                    target = "Ljavax/imageio/ImageIO;write(Ljava/awt/image/RenderedImage;Ljava/lang/String;Ljava/io/File;)Z"
            )
    )
    private static boolean saveAsyncScreenshot(RenderedImage im, String formatName, File output, Operation<Boolean> original, @Share("future") LocalRef<CompletableFuture<Void>> future) {
        if (!BetaVisuals.OPTIONS.asyncScreenshots) {
            return original.call(im, formatName, output);
        }

        future.get().thenRun(() -> {
            try {
                original.call(im, formatName, output);
            } catch (Exception e) {
                BetaVisuals.LOGGER.error("{}{}", e.toString(), e.fillInStackTrace());
            }
        });
        return true;
    }
}
