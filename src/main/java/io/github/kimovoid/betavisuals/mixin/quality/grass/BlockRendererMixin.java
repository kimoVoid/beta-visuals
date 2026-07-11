package io.github.kimovoid.betavisuals.mixin.quality.grass;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.kimovoid.betavisuals.BetaVisuals;
import net.minecraft.client.render.block.BlockRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockRenderer.class)
public class BlockRendererMixin {

    @WrapOperation(
            method = "*",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/render/block/BlockRenderer;fancyGraphics:Z",
                    opcode = Opcodes.GETSTATIC
            )
    )
    private boolean setGrassQuality(Operation<Boolean> original) {
        switch (BetaVisuals.OPTIONS.grass) {
            case 1:
                return true;
            case 2:
                return false;
            default:
                return original.call();
        }
    }
}
