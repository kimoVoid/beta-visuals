package io.github.kimovoid.betavisuals.mixin.details.vignette;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.kimovoid.betavisuals.BetaVisuals;
import net.minecraft.client.gui.GameGui;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameGui.class)
public abstract class GameGuiMixin {

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;isFancyGraphicsEnabled()Z"))
    private boolean setVignette(Operation<Boolean> original) {
        switch (BetaVisuals.OPTIONS.vignette) {
            case 0:
                return original.call();
            case 1:
                return true;
            default:
                GL11.glBlendFunc(770, 771);
                return false;
        }
    }
}
