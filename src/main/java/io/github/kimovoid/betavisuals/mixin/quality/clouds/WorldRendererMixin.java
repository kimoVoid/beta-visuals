package io.github.kimovoid.betavisuals.mixin.quality.clouds;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.kimovoid.betavisuals.BetaVisuals;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.render.world.WorldRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Inject(method = "renderClouds", at = @At("HEAD"), cancellable = true)
    private void disableClouds(CallbackInfo ci) {
        if (BetaVisuals.OPTIONS.clouds == 3) {
            ci.cancel();
        }
    }

    @WrapOperation(
            method = "renderClouds",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/options/GameOptions;fancyGraphics:Z",
                    opcode = Opcodes.GETFIELD
            )
    )
    private boolean setCloudQuality(GameOptions instance, Operation<Boolean> original) {
        return switch (BetaVisuals.OPTIONS.clouds) {
            case 1 -> true;
            case 2 -> false;
            default -> original.call(instance);
        };
    }
}
