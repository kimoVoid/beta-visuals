package io.github.kimovoid.betavisuals.mixin.render.whitelinefix;

import net.minecraft.client.render.block.BlockRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BlockRenderer.class)
public class BlockRendererMixin {

    @ModifyVariable(method = "tesselateBottomFace", at = @At(value = "STORE"), ordinal = 3)
    private double addBottomMarginX(double original) {
        return original + 0.01 / 256.0;
    }

    @ModifyVariable(method = "tesselateBottomFace", at = @At(value = "STORE"), ordinal = 5)
    private double addBottomMarginZ(double original) {
        return original + 0.01 / 256.0;
    }

    @ModifyVariable(method = "tesselateTopFace", at = @At(value = "STORE"), ordinal = 3)
    private double addTopMarginX(double original) {
        return original + 0.01 / 256.0;
    }

    @ModifyVariable(method = "tesselateTopFace", at = @At(value = "STORE"), ordinal = 5)
    private double addTopMarginZ(double original) {
        return original + 0.01 / 256.0;
    }

    @ModifyVariable(method = "tesselateNorthFace", at = @At(value = "STORE"), ordinal = 3)
    private double addNorthMarginX(double original) {
        return original + 0.01 / 256.0;
    }

    @ModifyVariable(method = "tesselateNorthFace", at = @At(value = "STORE"), ordinal = 5)
    private double addNorthMarginY(double original) {
        return original + 0.01 / 256.0;
    }

    @ModifyVariable(method = "tesselateSouthFace", at = @At(value = "STORE"), ordinal = 3)
    private double addSouthMarginX(double original) {
        return original + 0.01 / 256.0;
    }

    @ModifyVariable(method = "tesselateSouthFace", at = @At(value = "STORE"), ordinal = 5)
    private double addSouthMarginY(double original) {
        return original + 0.01 / 256.0;
    }

    @ModifyVariable(method = "tesselateWestFace", at = @At(value = "STORE"), ordinal = 3)
    private double addWestMarginZ(double original) {
        return original + 0.01 / 256.0;
    }

    @ModifyVariable(method = "tesselateWestFace", at = @At(value = "STORE"), ordinal = 5)
    private double addWestMarginY(double original) {
        return original + 0.01 / 256.0;
    }

    @ModifyVariable(method = "tesselateEastFace", at = @At(value = "STORE"), ordinal = 3)
    private double addEastMarginZ(double original) {
        return original + 0.01 / 256.0;
    }

    @ModifyVariable(method = "tesselateEastFace", at = @At(value = "STORE"), ordinal = 5)
    private double addEastMarginY(double original) {
        return original + 0.01 / 256.0;
    }

    @ModifyVariable(method = "tesselateLiquid", at = @At(value = "STORE"), index = 41)
    private double addLiquidMargin$1(double original) {
        return original + 0.01 / 256.0;
    }

    @ModifyVariable(method = "tesselateLiquid", at = @At(value = "STORE"), index = 45)
    private double addLiquidMargin$2(double original) {
        return original + 0.01 / 256.0;
    }

    @ModifyVariable(method = "tesselateLiquid", at = @At(value = "STORE"), index = 47)
    private double addLiquidMargin$3(double original) {
        return original + 0.01 / 256.0;
    }
}
