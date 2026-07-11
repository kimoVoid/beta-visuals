package io.github.kimovoid.betavisuals.mixin.other.chat;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.kimovoid.betavisuals.BetaVisuals;
import net.minecraft.client.gui.GameGui;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameGui.class)
public class GameGuiMixin {

    @ModifyArg(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/TextRenderer;drawWithShadow(Ljava/lang/String;III)V",
                    ordinal = 5
            ),
            index = 3
    )
    private int setChatTextOpacity(int orig) {
        int opacity = (int) (255 * BetaVisuals.OPTIONS.chatTextOpacity);
        return 0xFFFFFF + (opacity << 24);
    }

    @ModifyArg(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GameGui;fill(IIIII)V",
                    ordinal = 1
            ),
            index = 4
    )
    private int setChatBackgroundOpacity(int orig, @Local(index = 20) int e) {
        int opacity = (int) ((float) e * BetaVisuals.OPTIONS.chatBgOpacity);
        return opacity << 24;
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glTranslatef(FFF)V",
                    ordinal = 2,
                    shift = At.Shift.AFTER,
                    remap = false
            )
    )
    private void setChatScale(CallbackInfo ci) {
        GL11.glScalef(BetaVisuals.OPTIONS.chatScale, BetaVisuals.OPTIONS.chatScale, 1.0f);
    }
}
