package io.github.kimovoid.betavisuals.mixin.quality.rainsnow;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.kimovoid.betavisuals.BetaVisuals;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.GameRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "renderSnowAndRain", at = @At("HEAD"), cancellable = true)
    private void cancelRainAndSnow(float tickDelta, CallbackInfo ci) {
        if (BetaVisuals.OPTIONS.rainAndSnow == 3) {
            ci.cancel();
        }
    }

    @Inject(method = "tickRain", at = @At("HEAD"), cancellable = true)
    private void cancelRainAndSnowTick(CallbackInfo ci) {
        if (BetaVisuals.OPTIONS.rainAndSnow == 3) {
            ci.cancel();
        }
    }

    @WrapOperation(
            method = "renderSnowAndRain",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/options/GameOptions;fancyGraphics:Z",
                    opcode = Opcodes.GETFIELD
            )
    )
    private boolean setRainSnowQuality(GameOptions instance, Operation<Boolean> original) {
        switch (BetaVisuals.OPTIONS.rainAndSnow) {
            case 1:
                return true;
            case 2:
                return false;
            default:
                return original.call(instance);
        }
    }
}
