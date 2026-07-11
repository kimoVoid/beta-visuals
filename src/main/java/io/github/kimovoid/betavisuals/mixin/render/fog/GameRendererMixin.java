package io.github.kimovoid.betavisuals.mixin.render.fog;

import io.github.kimovoid.betavisuals.BetaVisuals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.GameRenderer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GameRenderer.class, priority = 500)
public class GameRendererMixin {

    @Shadow private float renderDistance;
    @Shadow private Minecraft minecraft;

    @Inject(
            method = "setupFog",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glEnable(I)V",
                    remap = false
            )
    )
    private void doFogSlider(int mode, float tickDelta, CallbackInfo ci) {
        float fogStart = BetaVisuals.OPTIONS.fogStart;
        float fogEnd = 1.0F;
        if (mode < 0) {
            fogStart = 0.0F;
            fogEnd = 0.8F;
        }

        if (this.minecraft.world.dimension.id == -1) {
            fogStart = 0.0F;
            fogEnd = 1.0F;
        }

        GL11.glFogf(GL11.GL_FOG_START, this.renderDistance * fogStart);
        GL11.glFogf(GL11.GL_FOG_END, this.renderDistance * fogEnd);
    }
}
