package io.github.kimovoid.betavisuals.mixin.general.fpslimit;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.kimovoid.betavisuals.BVOptions;
import io.github.kimovoid.betavisuals.BetaVisuals;
import net.minecraft.client.render.GameRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @ModifyExpressionValue(method = "render", at = @At(value = "CONSTANT", args = "intValue=200", ordinal = 0))
    public int modifyFpsTarget(int original) {
        return BetaVisuals.OPTIONS.fpsLimit;
    }

    @ModifyExpressionValue(method = "render", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/options/GameOptions;fpsLimit:I", ordinal = 0))
    public int overridePerformanceLevel1(int original) {
        return -1;
    }

    @ModifyExpressionValue(method = "render", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/options/GameOptions;fpsLimit:I", ordinal = 1))
    public int overridePerformanceLevel2(int original) {
        return -1;
    }

    @ModifyExpressionValue(method = "render", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/options/GameOptions;fpsLimit:I", ordinal = 1))
    public int overridePerformanceLevel0(int original) {
        return this.isLimited() ? 2 : 0;
    }

    @ModifyExpressionValue(method = "render", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/options/GameOptions;fpsLimit:I", ordinal = 3))
    public int nukeSleep(int original) {
        return -1;
    }

    @ModifyExpressionValue(method = "render", at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/options/GameOptions;fpsLimit:I", ordinal = 4))
    public int redirectMenuFpsLimit(int original) {
        return this.isLimited() ? 2 : 0;
    }

    @Unique
    private boolean isLimited() {
        return BetaVisuals.OPTIONS.fpsLimit < BVOptions.Option.FRAMERATE_LIMIT.getMax();
    }
}
