package io.github.kimovoid.betavisuals.mixin.render.fog;

import io.github.kimovoid.betavisuals.BetaVisuals;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = GameRenderer.class, priority = 500)
public class GameRendererMixin {

    @ModifyConstant(
            method = "setupFog",
            constant = @Constant(floatValue = 0.25F),
            remap = false
    )
    private float setFogStart(float constant) {
        return BetaVisuals.OPTIONS.fogStart + 0.05F;
    }
}
