package io.github.kimovoid.betavisuals.mixin.other.showfps;

import io.github.kimovoid.betavisuals.BetaVisuals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GameGui;
import net.minecraft.client.gui.GuiElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameGui.class)
public class GameGuiMixin extends GuiElement {

    @Shadow private Minecraft minecraft;

    @Inject(method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glDisable(I)V",
                    ordinal = 2,
                    shift = At.Shift.AFTER,
                    remap = false
            )
    )
    public void showFps(CallbackInfo ci) {
        if (!this.minecraft.options.debugEnabled && BetaVisuals.OPTIONS.showFps) {
            this.minecraft.textRenderer.drawWithShadow(minecraft.fpsDebugInfo, 2, 2, 0xFFFFFF);
        }
    }
}
