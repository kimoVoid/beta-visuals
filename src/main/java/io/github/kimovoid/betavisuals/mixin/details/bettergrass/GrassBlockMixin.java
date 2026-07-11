package io.github.kimovoid.betavisuals.mixin.details.bettergrass;

import io.github.kimovoid.betavisuals.BetaVisuals;
import net.minecraft.block.GrassBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GrassBlock.class)
public class GrassBlockMixin {

    @ModifyConstant(method = "getSprite", constant = @Constant(intValue = 68))
    private int setSnowyGrassSprite(int orig) {
        return BetaVisuals.OPTIONS.betterGrass ? 66 : orig;
    }
}
