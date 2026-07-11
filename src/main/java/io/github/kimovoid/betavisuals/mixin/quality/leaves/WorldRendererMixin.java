package io.github.kimovoid.betavisuals.mixin.quality.leaves;

import io.github.kimovoid.betavisuals.BetaVisuals;
import net.minecraft.client.render.world.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @ModifyArg(method = "reload", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/LeavesBlock;setCulling(Z)V"))
    private boolean setLeafQuality(boolean culling) {
        switch (BetaVisuals.OPTIONS.leaves) {
            case 1:
                return true;
            case 2:
                return false;
            default:
                return culling;
        }
    }
}
