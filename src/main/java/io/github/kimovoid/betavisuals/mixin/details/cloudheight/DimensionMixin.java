package io.github.kimovoid.betavisuals.mixin.details.cloudheight;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.kimovoid.betavisuals.BetaVisuals;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.dimension.Dimension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(Dimension.class)
public class DimensionMixin {

    @ModifyReturnValue(method = "getCloudHeight", at = @At("RETURN"))
    public float getCloudHeight(float original) {
        return original + BetaVisuals.OPTIONS.cloudHeight * 50.0f;
    }
}
